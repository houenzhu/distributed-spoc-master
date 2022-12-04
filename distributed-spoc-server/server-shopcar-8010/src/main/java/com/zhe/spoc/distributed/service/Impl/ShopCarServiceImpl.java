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

    // 我的购物车
    @Override
    public CommonResult<?> myShopCar_1() {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<CoursEntity> coursEntities = myShopCar(user.getId());
        if (CollectionUtils.isEmpty(coursEntities)) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "空空如也!");
        }
        return CommonResult.success(coursEntities);
    }
    // 购买一个课程
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
            orderShopMapper.insertOrder(orderShopEntity); // 将购买的课程添加到订单，同时删除购物车已购买的课程
            deleteByCouId(shopCarEntity);
        }catch (Exception e) {
            //手动强制回滚事务，这里一定要第一时间处理
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.getStackTrace();
        }
        return shopCarMapper.buyOneCourse(shopCarEntity);
    }


    // 购买后更新积分
    @Override
    public Boolean updateScore(Double stuScore, Integer stuId) {
        return shopCarMapper.updateScore(stuScore, stuId);
    }

    // 购买后删除购物车的那个课程
    @Override
    public Boolean deleteByCouId(ShopCarEntity shopCarEntity) {
        return shopCarMapper.deleteByCouId(shopCarEntity);
    }

    // 批量购买课程
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> BuyMoreCourse(List<ShopCarEntity> shopCarEntityList) throws InterruptedException {
        // 获取全部课程的id
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        double Total = 0;
        for (ShopCarEntity shopCarEntity: shopCarEntityList) {
            // 拿到课程id
            Long couId = shopCarEntity.getCouId();
            CoursEntity coursEntity = serverCoursClient.BuyCourseId(couId);
            // 获取课程名称
            String couName = coursEntity.getCouName();
            // 判断是否已拥有该课程
            int have = orderShopMapper.selectThisCourse(user.getId(), couId);
            if (have > 0) {
                // 已经拥有了该课程
                return CommonResult.failed(ResultCode.FAILED, couName + "已购买过了, 请重新选择!");
            }
            // 没有该课程
            // 获取该课程价格
            Double couPrice = coursEntity.getCouPrice();
            // 算出总价
            Total = Total + couPrice;
        }
        // 创建锁对象
//        SimpleRedisLock lock = new SimpleRedisLock(BUY_COURSE_BY_KEY + user.getId(), stringRedisTemplate);
        RLock lock = redissonClient.getLock(BUY_COURSE_BY_KEY + user.getId());
        // 尝试获取锁
        boolean success = lock.tryLock(1L, TimeUnit.SECONDS);
        if (!success) {
            // 获取锁失败
            return CommonResult.failed(ResultCode.FAILED, "不允许重复购买!");
        }
        try {
            // 获取代理对象(事务)
            ShopCarService proxy = (ShopCarService) AopContext.currentProxy();
            return proxy.buyCourseInOrder(shopCarEntityList, user, Total);
        }finally {
            // 释放锁
            lock.unlock();
        }

    }

    @NotNull
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> buyCourseInOrder(List<ShopCarEntity> shopCarEntityList, UserEntity user, double Total) {
        // 判断自身积分是否足够买全部课程
        StudentEntity stuInfo = serverStuClient.getMyInfoFeign(user.getId());
        // 获取自身积分
        Double myScore = stuInfo.getStuScore();
        // 不够，返回错误信息
        if (myScore < Total) {
            return CommonResult.failed(ResultCode.FAILED, "积分不够哦!");
        }
        // 下单
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
            // 购买成功
            // 把购物车所购买的课程清除
            deleteByCouId(shopCarEntity);
        }
        // 购买了的商品添加到我的订单上
        Boolean success = shopCarMapper.BuyMoreCourse(orderShopEntities);

        // 把这些课程添加到我的课程上
        shopCarMapper.insertMoreMyCourse(stuCourseEntities);
        // 购买失败，返回错误信息，数据回滚
        if (!success) {
            return CommonResult.failed(ResultCode.FAILED, "购买失败!");
        }
        // 扣除积分
        myScore = myScore - Total;

        // 更新个人信息的积分
        updateScore(myScore, user.getId());
        // 返回成功信息
        return CommonResult.success(true, "购买成功!");
    }

    // 批量删除购物车里面的物品
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> deleteShopCar(List<ShopCarEntity> shopCarEntityList) {
        Boolean success = shopCarMapper.deleteShopCar(shopCarEntityList);
        if (!success) {
            return CommonResult.failed(ResultCode.FAILED, "删除失败!");
        }
        return CommonResult.success(true);
    }


}
