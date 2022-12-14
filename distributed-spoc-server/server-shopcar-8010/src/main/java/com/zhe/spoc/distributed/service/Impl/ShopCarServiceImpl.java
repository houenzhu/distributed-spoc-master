package com.zhe.spoc.distributed.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhe.common.api.CommonResult;
import com.zhe.common.api.ResultCode;
import com.zhe.common.entity.*;
import com.zhe.spoc.distributed.client.ServerCoursClient;
import com.zhe.spoc.distributed.client.ServerStuClient;
import com.zhe.spoc.distributed.client.ServerUserClient;
import com.zhe.spoc.distributed.config.RedissonConfig;
import com.zhe.spoc.distributed.mapper.OrderShopMapper;
import com.zhe.spoc.distributed.mapper.ShopCarMapper;
import com.zhe.spoc.distributed.service.ShopCarService;
import com.zhe.spoc.distributed.utils.SimpleRedisLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.zhe.common.utils.RedisConstants.BUY_COURSE_BY_KEY;
import static com.zhe.common.utils.RedisConstants.BUY_COURSE_BY_KEY_TTL;

/**
 * @author HouEnZhu
 * @ClassName ShopCarServiceImpl
 * @Description TODO
 * @date 2022/10/26 10:26
 * @Version 1.0
 */

@Service
@Slf4j
public class ShopCarServiceImpl extends ServiceImpl<ShopCarMapper, ShopCarEntity> implements ShopCarService {

    @Autowired
    private ShopCarMapper shopCarMapper;

    @Autowired
    private OrderShopMapper orderShopMapper;

    @Autowired
    private ServerStuClient serverStuClient;

    @Autowired
    private ServerCoursClient serverCoursClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;
    @Override
    public Boolean addShopCar(ShopCarEntity shopCarEntity) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer stuId = user.getId();
        shopCarEntity.setStuId(stuId);
        return shopCarMapper.addShopCar(shopCarEntity);
    }

    @Override
    public List<CoursEntity> myShopCar(Integer stuId) {
        return shopCarMapper.myShopCar(stuId);
    }

    // ???????????????
    @Override
    public CommonResult<?> myShopCar_1() {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<CoursEntity> coursEntities = myShopCar(user.getId());
        if (CollectionUtils.isEmpty(coursEntities)) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "????????????!");
        }
        return CommonResult.success(coursEntities);
    }
    // ??????????????????
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean buyOneCourse(ShopCarEntity shopCarEntity) {
        OrderShopEntity orderShopEntity = new OrderShopEntity();
        CoursEntity coursEntity = serverCoursClient.BuyCourseId(shopCarEntity.getCouId());
        Double couPrice = coursEntity.getCouPrice();
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        shopCarEntity.setStuId(user.getId());
        StudentEntity myInfo= serverStuClient.getMyInfoFeign(user.getId());
        Double stuScore = myInfo.getStuScore();
        if (stuScore < couPrice) {
            return false;
        }
        try{
            stuScore = stuScore - couPrice;
            updateScore(stuScore, user.getId());
            orderShopEntity.setStuId(user.getId())
                    .setCost(coursEntity.getCouPrice())
                    .setCouId(coursEntity.getCouId());
            orderShopMapper.insertOrder(orderShopEntity); // ???????????????????????????????????????????????????????????????????????????
            deleteByCouId(shopCarEntity);
        }catch (Exception e) {
            //????????????????????????????????????????????????????????????
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.getStackTrace();
        }
        return shopCarMapper.buyOneCourse(shopCarEntity);
    }


    // ?????????????????????
    @Override
    public Boolean updateScore(Double stuScore, Integer stuId) {
        return shopCarMapper.updateScore(stuScore, stuId);
    }

    // ???????????????????????????????????????
    @Override
    public Boolean deleteByCouId(ShopCarEntity shopCarEntity) {
        return shopCarMapper.deleteByCouId(shopCarEntity);
    }

    // ??????????????????
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> BuyMoreCourse(List<ShopCarEntity> shopCarEntityList) throws InterruptedException {
        // ?????????????????????id
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        double Total = 0;
        for (ShopCarEntity shopCarEntity: shopCarEntityList) {
            // ????????????id
            Long couId = shopCarEntity.getCouId();
            CoursEntity coursEntity = serverCoursClient.BuyCourseId(couId);
            // ??????????????????
            String couName = coursEntity.getCouName();
            // ??????????????????????????????
            int have = orderShopMapper.selectThisCourse(user.getId(), couId);
            if (have > 0) {
                // ????????????????????????
                return CommonResult.failed(ResultCode.FAILED, couName + "???????????????, ???????????????!");
            }
            // ???????????????
            // ?????????????????????
            Double couPrice = coursEntity.getCouPrice();
            // ????????????
            Total = Total + couPrice;
        }
        // ???????????????
//        SimpleRedisLock lock = new SimpleRedisLock(BUY_COURSE_BY_KEY + user.getId(), stringRedisTemplate);
        RLock lock = redissonClient.getLock(BUY_COURSE_BY_KEY + user.getId());
        // ???????????????
        boolean success = lock.tryLock(1L, TimeUnit.SECONDS);
        if (!success) {
            // ???????????????
            return CommonResult.failed(ResultCode.FAILED, "?????????????????????!");
        }
        try {
            // ??????????????????(??????)
            ShopCarService proxy = (ShopCarService) AopContext.currentProxy();
            return proxy.buyCourseInOrder(shopCarEntityList, user, Total);
        }finally {
            // ?????????
            lock.unlock();
        }

    }

    @NotNull
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> buyCourseInOrder(List<ShopCarEntity> shopCarEntityList, UserEntity user, double Total) {
        // ?????????????????????????????????????????????
        StudentEntity stuInfo = serverStuClient.getMyInfoFeign(user.getId());
        // ??????????????????
        Double myScore = stuInfo.getStuScore();
        // ???????????????????????????
        if (myScore < Total) {
            return CommonResult.failed(ResultCode.FAILED, "???????????????!");
        }
        // ??????
        List<OrderShopEntity> orderShopEntities = new ArrayList<>();
        List<StuCourseEntity> stuCourseEntities = new ArrayList<>();
        for (ShopCarEntity shopCarEntity: shopCarEntityList) {
            OrderShopEntity orderShopEntity = new OrderShopEntity();
            StuCourseEntity stuCourseEntity = new StuCourseEntity();
            Long couId = shopCarEntity.getCouId();
            CoursEntity coursEntity = serverCoursClient.BuyCourseId(couId);
            orderShopEntity.setStuId(user.getId())
                    .setCost(coursEntity.getCouPrice())
                    .setCouId(coursEntity.getCouId());

            stuCourseEntity.setStuId(user.getId()).setCouId(couId);
            orderShopEntities.add(orderShopEntity);
            stuCourseEntities.add(stuCourseEntity);
            shopCarEntity.setStuId(user.getId());
            // ????????????
            // ????????????????????????????????????
            deleteByCouId(shopCarEntity);
        }
        // ??????????????????????????????????????????
        Boolean success = shopCarMapper.BuyMoreCourse(orderShopEntities);

        // ???????????????????????????????????????
        shopCarMapper.insertMoreMyCourse(stuCourseEntities);
        // ????????????????????????????????????????????????
        if (!success) {
            return CommonResult.failed(ResultCode.FAILED, "????????????!");
        }
        // ????????????
        myScore = myScore - Total;

        // ???????????????????????????
        updateScore(myScore, user.getId());
        // ??????????????????
        return CommonResult.success(true, "????????????!");
    }

    // ????????????????????????????????????
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> deleteShopCar(List<ShopCarEntity> shopCarEntityList) {
        Boolean success = shopCarMapper.deleteShopCar(shopCarEntityList);
        if (!success) {
            return CommonResult.failed(ResultCode.FAILED, "????????????!");
        }
        return CommonResult.success(true);
    }


}
