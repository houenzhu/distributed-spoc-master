package com.zhe.spoc.distributed.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhe.common.api.CommonResult;
import com.zhe.common.dto.UserDto;
import com.zhe.common.entity.CoursEntity;
import com.zhe.common.entity.PracticeEntity;
import com.zhe.common.entity.StuPracticeEntity;
import com.zhe.common.entity.UserEntity;
import com.zhe.spoc.distributed.client.ServerCoursClient;
import com.zhe.spoc.distributed.client.ServerUserClient;
import com.zhe.spoc.distributed.mapper.PracticeMapper;
import com.zhe.spoc.distributed.service.PracticeService;
import com.zhe.spoc.distributed.utils.CacheClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.zhe.common.utils.RedisConstants.*;

/**
 * @author HouEnZhu
 * @ClassName PracticeServiceImpl
 * @Description TODO
 * @date 2022/10/23 16:35
 * @Version 1.0
 */

@Service
@Slf4j
public class PracticeServiceImpl extends ServiceImpl<PracticeMapper, PracticeEntity> implements PracticeService {

    @Autowired
    private PracticeMapper practiceMapper;

    @Autowired
    private ServerUserClient serverUserClient;

    @Autowired
    private ServerCoursClient serverCoursClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private CacheClient cacheClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<?> insertPractice(PracticeEntity practiceEntity) {
        if (practiceEntity.getCouId() == null) {
            return CommonResult.failed("????????????????????????!");
        }
        if (practiceEntity.getPraMain() == null) {
            return CommonResult.failed("??????????????????!");
        }
        if (practiceEntity.getPraName() == null) {
            return CommonResult.failed("??????????????????!");
        }
        if (practiceEntity.getPraTime() == null) {
            return CommonResult.failed("????????????????????????");
        }
        if (practiceEntity.getPraSite() == null) {
            return CommonResult.failed("????????????????????????!");
        }
        if (practiceEntity.getEndTime() == null) {
            return CommonResult.failed("????????????????????????!");
        }
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        practiceEntity.setTeaId(user.getId());
        Boolean success = practiceMapper.insertPractice(practiceEntity);
        if (!success) {
            return CommonResult.failed("????????????!");
        }
        return CommonResult.success(true, "????????????!");
    }

    @Override
    public Boolean removePractice(Long praId) {
        return practiceMapper.removePractice(praId);
    }

    @Override
    public List<PracticeEntity> practiceList(Integer teaId, Integer begin, Integer count) {
        begin = (begin - 1) * count;
        return practiceMapper.practiceList(teaId, begin, count);
    }

    public CommonResult<?> myPractice(Integer begin, Integer count){
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<PracticeEntity> practiceEntities = practiceList(user.getId(), begin, count);
        if (CollectionUtils.isEmpty(practiceEntities)) {
            return CommonResult.failed("????????????????????????!");
        }
        for (PracticeEntity practiceEntity: practiceEntities) {
            Long couId = practiceEntity.getCouId();
            CoursEntity coursEntity = serverCoursClient.BuyCourseId(couId);
            practiceEntity.setCoursEntity(coursEntity);
            if (practiceEntity.getPraTime().isBefore(LocalDateTime.now()) && practiceEntity.getEndTime().isAfter(LocalDateTime.now())) {
                // ??????????????????????????????
                practiceEntity.setIsBegin(true);
                practiceMapper.updateStatus(practiceEntity);
            }
            practiceEntity.setIsBegin(false);
            practiceMapper.updateStatus(practiceEntity);
        }
        return CommonResult.success(practiceEntities);
    }


    /**
     * ??????????????????????????????
     * @param praName
     * @return
     */
    public CommonResult<?> practiceEntityListByName(String praName, Integer pageNum, Integer pageSize) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<PracticeEntity> practiceEntities = practiceMapper.practiceListByName(user.getId(), praName, pageNum, pageSize);
        if (CollectionUtils.isEmpty(practiceEntities)) {
            return CommonResult.failed("???????????????");
        }
        for (PracticeEntity practiceEntity: practiceEntities) {
            Long couId = practiceEntity.getCouId();
            CoursEntity coursEntity = serverCoursClient.BuyCourseId(couId);
            practiceEntity.setCoursEntity(coursEntity);
            if (practiceEntity.getPraTime().isBefore(LocalDateTime.now()) && practiceEntity.getEndTime().isAfter(LocalDateTime.now())) {
                // ??????????????????????????????
                practiceEntity.setIsBegin(true);
                practiceMapper.updateStatus(practiceEntity);
            }
            practiceEntity.setIsBegin(false);
            practiceMapper.updateStatus(practiceEntity);
        }
        return CommonResult.success(practiceEntities);
    }


    // ????????????????????????
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<?>  insertStuPractice(StuPracticeEntity stuPracticeEntity) {
        if (stuPracticeEntity.getPraId() == null) {
            return CommonResult.failed("??????id????????????");
        }
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // ??????????????????id
        Long praId = stuPracticeEntity.getPraId();
        PracticeEntity practiceEntity = practiceMapper.selectById(praId);

        if (practiceEntity == null) {
            return CommonResult.failed("???????????????!");
        }

        // ???????????????????????????
        if (practiceEntity.getEndTime().isBefore(LocalDateTime.now())) {
            // ???????????????????????????????????????????????????
            return CommonResult.failed("??????????????????!");
        }

        // ???????????????????????????
        if (practiceEntity.getPraTime().isBefore(LocalDateTime.now()) && practiceEntity.getEndTime().isAfter(LocalDateTime.now())) {
            return CommonResult.failed("?????????????????????!???????????????!");
        }

        stuPracticeEntity.setStuId(user.getId());
        Integer integer = practiceMapper.checkPractice(stuPracticeEntity);
        if (integer > 0) {
            return CommonResult.failed("?????????????????????!");
        }

        Boolean isSuccess = practiceMapper.insertStuPractice(stuPracticeEntity);
        if(!isSuccess) {
            return CommonResult.failed("????????????!");
        }
        return CommonResult.success("????????????!");
    }

    // ??????????????????
    @Override
    public Map<String, Object> SelectPractice() {
        Map<String, Object> map = new HashMap<>();
        List<PracticeEntity> practiceEntities = practiceMapper.SelectAllPractice();
        if (CollectionUtils.isEmpty(practiceEntities)) {
            return null;
        }
        for (PracticeEntity practiceEntity : practiceEntities) {
            UserEntity teacherInfo = serverUserClient.getTeacherInfo(practiceEntity.getTeaId());
            practiceEntity.setUser(teacherInfo);
            CoursEntity coursEntity = serverCoursClient.BuyCourseId(practiceEntity.getCouId());
            practiceEntity.setCoursEntity(coursEntity);
        }
        map.put("practiceInfo", practiceEntities);
        return map;
    }

    // ?????????????????????
    @Override
    public List<PracticeEntity> SelectAllPracticeInPage(Integer begin, Integer count) {
        begin = (begin - 1) * count;
        List<PracticeEntity> practiceEntities = practiceMapper.SelectAllPracticeInPage(begin, count);
        if (CollectionUtils.isEmpty(practiceEntities)) {
            return null;
        }
        for (PracticeEntity practiceEntity: practiceEntities) {
            Integer teaId = practiceEntity.getTeaId();
            UserEntity teacherInfo = serverUserClient.getTeacherInfo(teaId);
            practiceEntity.setUser(teacherInfo);
            Long couId = practiceEntity.getCouId();
            CoursEntity coursEntity = serverCoursClient.BuyCourseId(couId);
            practiceEntity.setCoursEntity(coursEntity);
            if (practiceEntity.getPraTime().isBefore(LocalDateTime.now()) && practiceEntity.getEndTime().isAfter(LocalDateTime.now())) {
                // ??????????????????????????????
                practiceEntity.setIsBegin(true);
                practiceMapper.updateStatus(practiceEntity);
            }else{
                practiceEntity.setIsBegin(false);
                practiceMapper.updateStatus(practiceEntity);
            }
        }
        return practiceEntities;
    }

    // ????????????(??????)
    @Override
    public CommonResult<?> SelectAllPracticeMapInPage(Integer begin, Integer count) {
        Map<String, Object> map = new HashMap<>();
        List<PracticeEntity> practiceEntities = SelectAllPracticeInPage(begin, count);
        if (CollectionUtils.isEmpty(practiceEntities)) {
            return CommonResult.failed("??????????????????!");
        }
        map.put("practiceInfo", practiceEntities);
        return CommonResult.success(map);
    }

    @Override
    public Integer MyPracticeCount() {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return practiceMapper.MyPracticeCount(user.getId());
    }

    // ??????id????????????????????????
    @Override
    public CommonResult<?> practiceEntityById(Long praId) {
        String key = PRACTICE_ID_KEY + praId;
        // ??????redis
        String practiceJson = redisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(practiceJson)) {
            // ?????? ????????????
            PracticeEntity practiceEntity = JSONUtil.toBean(practiceJson, PracticeEntity.class);
            return CommonResult.success(practiceEntity);
        }
        if(practiceJson != null) {
            // ?????????????????? ??????????????????
            return null;
        }
        // ?????????????????????????????????
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PracticeEntity practiceEntity = practiceMapper.practiceListById(user.getId(), praId);
        if (practiceEntity == null) {
            // ?????????????????????
            cacheClient.set(key, "", PRACTICE_ID_NULL, TimeUnit.SECONDS);
            return null;
        }
        Integer teaId = practiceEntity.getTeaId();
        UserEntity teacherInfo = serverUserClient.getTeacherInfo(teaId);
        practiceEntity.setUser(teacherInfo);
        Long couId = practiceEntity.getCouId();
        CoursEntity coursEntity = serverCoursClient.BuyCourseId(couId);
        practiceEntity.setCoursEntity(coursEntity);
        if (practiceEntity.getPraTime().isBefore(LocalDateTime.now()) && practiceEntity.getEndTime().isAfter(LocalDateTime.now())) {
            // ??????????????????????????????
            practiceEntity.setIsBegin(true);
            practiceMapper.updateStatus(practiceEntity);
        }
        practiceEntity.setIsBegin(false);
        practiceMapper.updateStatus(practiceEntity);

        // ??????redis
        cacheClient.set(key, JSONUtil.toJsonStr(practiceEntity),PRACTICE_ID_TTL, TimeUnit.MINUTES);
        return CommonResult.success(practiceEntity);
    }

    @Override
    public CommonResult<?> PracticeNum() {
        int num = practiceMapper.PracticeNum();
        return CommonResult.success(num);
    }

    /**
     * ??????????????????????????????
     * @param praName
     * @return
     */
    @Override
    public CommonResult<?> myPracticeByNameNum(String praName) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int num = practiceMapper.myPracticeByNameNum(praName, user.getId());
        return CommonResult.success(num);
    }

    /**
     * id????????????????????????
     * @param praId
     * @return
     */
    @Override
    public CommonResult<?> myPracticeByIdNum(Long praId) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int num = practiceMapper.myPracticeByIdNum(praId, user.getId());
        return CommonResult.success(num);
    }

    /**
     * ???????????????????????????
     * @param praName
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public CommonResult<?> PracticeByName(String praName, Integer pageNum, Integer pageSize) {
        pageNum = (pageNum - 1) * pageSize;
        List<PracticeEntity> practiceEntities = practiceMapper.AllpracticeListByName(praName, pageNum, pageSize);
        if (CollectionUtils.isEmpty(practiceEntities)) {
            return CommonResult.failed("???????????????");
        }
        for (PracticeEntity practiceEntity: practiceEntities) {
            Long couId = practiceEntity.getCouId();
            CoursEntity coursEntity = serverCoursClient.BuyCourseId(couId);
            practiceEntity.setCoursEntity(coursEntity);
            UserDto trueName = serverUserClient.getTrueName(practiceEntity.getTeaId());
            practiceEntity.setUser(BeanUtil.copyProperties(trueName, UserEntity.class));
            if (practiceEntity.getPraTime().isBefore(LocalDateTime.now()) && practiceEntity.getEndTime().isAfter(LocalDateTime.now())) {
                // ??????????????????????????????
                practiceEntity.setIsBegin(true);
                practiceMapper.updateStatus(practiceEntity);
            }
            practiceEntity.setIsBegin(false);
            practiceMapper.updateStatus(practiceEntity);
        }
        return CommonResult.success(practiceEntities);
    }

    /**
     * ?????????????????????????????????
     * @param praName
     * @return
     */
    @Override
    public CommonResult<?> PracticeByNameNum(String praName) {
        int num = practiceMapper.PracticeByNameNum(praName);
        return CommonResult.success(num);
    }

    /**
     * ??????id???????????????????????????
     * @param praId
     * @return
     */
    @Override
    public CommonResult<?> PracticeByIdNum(Long praId) {
        int num = practiceMapper.PracticeByIdNum(praId);
        return CommonResult.success(num);
    }

    /**
     * ??????id?????????????????????
     * @param praId
     * @return
     */
    @Override
    public CommonResult<?> PracticeById(Long praId) {
        List<PracticeEntity> list = new ArrayList<>();
        PracticeEntity practiceEntity = practiceMapper.practiceById(praId);
        if (practiceEntity == null) {
            return CommonResult.failed("?????????????????????????????????!");
        }
        Long couId = practiceEntity.getCouId();
        CoursEntity coursEntity = serverCoursClient.BuyCourseId(couId);
        practiceEntity.setCoursEntity(coursEntity);
        UserDto trueName = serverUserClient.getTrueName(practiceEntity.getTeaId());
        practiceEntity.setUser(BeanUtil.copyProperties(trueName, UserEntity.class));
        if (practiceEntity.getPraTime().isBefore(LocalDateTime.now()) && practiceEntity.getEndTime().isAfter(LocalDateTime.now())) {
            // ??????????????????????????????
            practiceEntity.setIsBegin(true);
            practiceMapper.updateStatus(practiceEntity);
        }
        practiceEntity.setIsBegin(false);
        practiceMapper.updateStatus(practiceEntity);
        list.add(practiceEntity);
        return CommonResult.success(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<?> updatePractice(PracticeEntity practiceEntity) {
        try{
            if (practiceEntity.getPraTime() == null) {
                return CommonResult.failed("????????????????????????!");
            } else if (practiceEntity.getEndTime() == null) {
                return CommonResult.failed("????????????????????????!");
            }
            Boolean isSuccess = practiceMapper.updatePractice(practiceEntity);
            if(isSuccess) {
                return CommonResult.success("????????????!");
            }
        }catch (Exception e) {
            e.getStackTrace();
            throw new RuntimeException();
        }
        return CommonResult.failed("????????????!");
    }
}
