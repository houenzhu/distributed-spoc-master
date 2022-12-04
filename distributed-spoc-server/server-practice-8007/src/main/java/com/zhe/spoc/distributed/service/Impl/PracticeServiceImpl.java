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
            return CommonResult.failed("所属课程不能为空!");
        }
        if (practiceEntity.getPraMain() == null) {
            return CommonResult.failed("内容不能为空!");
        }
        if (practiceEntity.getPraName() == null) {
            return CommonResult.failed("标题不能为空!");
        }
        if (practiceEntity.getPraTime() == null) {
            return CommonResult.failed("开始时间不能为空");
        }
        if (practiceEntity.getPraSite() == null) {
            return CommonResult.failed("实践地点不能为空!");
        }
        if (practiceEntity.getEndTime() == null) {
            return CommonResult.failed("结束时间不能为空!");
        }
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        practiceEntity.setTeaId(user.getId());
        Boolean success = practiceMapper.insertPractice(practiceEntity);
        if (!success) {
            return CommonResult.failed("创建失败!");
        }
        return CommonResult.success(true, "创建成功!");
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
            return CommonResult.failed("没有创建任何实践!");
        }
        for (PracticeEntity practiceEntity: practiceEntities) {
            Long couId = practiceEntity.getCouId();
            CoursEntity coursEntity = serverCoursClient.BuyCourseId(couId);
            practiceEntity.setCoursEntity(coursEntity);
            if (practiceEntity.getPraTime().isBefore(LocalDateTime.now()) && practiceEntity.getEndTime().isAfter(LocalDateTime.now())) {
                // 如果进行中，更换状态
                practiceEntity.setIsBegin(true);
                practiceMapper.updateStatus(practiceEntity);
            }
            practiceEntity.setIsBegin(false);
            practiceMapper.updateStatus(practiceEntity);
        }
        return CommonResult.success(practiceEntities);
    }


    /**
     * 模糊查询我发布的实践
     * @param praName
     * @return
     */
    public CommonResult<?> practiceEntityListByName(String praName, Integer pageNum, Integer pageSize) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<PracticeEntity> practiceEntities = practiceMapper.practiceListByName(user.getId(), praName, pageNum, pageSize);
        if (CollectionUtils.isEmpty(practiceEntities)) {
            return CommonResult.failed("没有该实践");
        }
        for (PracticeEntity practiceEntity: practiceEntities) {
            Long couId = practiceEntity.getCouId();
            CoursEntity coursEntity = serverCoursClient.BuyCourseId(couId);
            practiceEntity.setCoursEntity(coursEntity);
            if (practiceEntity.getPraTime().isBefore(LocalDateTime.now()) && practiceEntity.getEndTime().isAfter(LocalDateTime.now())) {
                // 如果进行中，更换状态
                practiceEntity.setIsBegin(true);
                practiceMapper.updateStatus(practiceEntity);
            }
            practiceEntity.setIsBegin(false);
            practiceMapper.updateStatus(practiceEntity);
        }
        return CommonResult.success(practiceEntities);
    }


    // 学生报名线下实践
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<?>  insertStuPractice(StuPracticeEntity stuPracticeEntity) {
        if (stuPracticeEntity.getPraId() == null) {
            return CommonResult.failed("实践id不能为空");
        }
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // 先获取该实践id
        Long praId = stuPracticeEntity.getPraId();
        PracticeEntity practiceEntity = practiceMapper.selectById(praId);

        if (practiceEntity == null) {
            return CommonResult.failed("实践不见了!");
        }

        // 判断该实践是否结束
        if (practiceEntity.getEndTime().isBefore(LocalDateTime.now())) {
            // 实践结束实践在当前实践之前，则结束
            return CommonResult.failed("实践已经结束!");
        }

        // 如果实践正在进行中
        if (practiceEntity.getPraTime().isBefore(LocalDateTime.now()) && practiceEntity.getEndTime().isAfter(LocalDateTime.now())) {
            return CommonResult.failed("实践已经开始了!请下次再来!");
        }

        stuPracticeEntity.setStuId(user.getId());
        Integer integer = practiceMapper.checkPractice(stuPracticeEntity);
        if (integer > 0) {
            return CommonResult.failed("你已经报名过了!");
        }

        Boolean isSuccess = practiceMapper.insertStuPractice(stuPracticeEntity);
        if(!isSuccess) {
            return CommonResult.failed("报名失败!");
        }
        return CommonResult.success("报名成功!");
    }

    // 查看实践内容
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

    // 封装分页的内容
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
                // 如果进行中，更换状态
                practiceEntity.setIsBegin(true);
                practiceMapper.updateStatus(practiceEntity);
            }else{
                practiceEntity.setIsBegin(false);
                practiceMapper.updateStatus(practiceEntity);
            }
        }
        return practiceEntities;
    }

    // 分页查询(教师)
    @Override
    public CommonResult<?> SelectAllPracticeMapInPage(Integer begin, Integer count) {
        Map<String, Object> map = new HashMap<>();
        List<PracticeEntity> practiceEntities = SelectAllPracticeInPage(begin, count);
        if (CollectionUtils.isEmpty(practiceEntities)) {
            return CommonResult.failed("没有实践信息!");
        }
        map.put("practiceInfo", practiceEntities);
        return CommonResult.success(map);
    }

    @Override
    public Integer MyPracticeCount() {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return practiceMapper.MyPracticeCount(user.getId());
    }

    // 根据id查询我发布的实践
    @Override
    public CommonResult<?> practiceEntityById(Long praId) {
        String key = PRACTICE_ID_KEY + praId;
        // 先查redis
        String practiceJson = redisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(practiceJson)) {
            // 存在 直接返回
            PracticeEntity practiceEntity = JSONUtil.toBean(practiceJson, PracticeEntity.class);
            return CommonResult.success(practiceEntity);
        }
        if(practiceJson != null) {
            // 若为空字符串 防止缓存穿透
            return null;
        }
        // 若为空，直接查询数据库
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PracticeEntity practiceEntity = practiceMapper.practiceListById(user.getId(), praId);
        if (practiceEntity == null) {
            // 把缓存空置写入
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
            // 如果进行中，更换状态
            practiceEntity.setIsBegin(true);
            practiceMapper.updateStatus(practiceEntity);
        }
        practiceEntity.setIsBegin(false);
        practiceMapper.updateStatus(practiceEntity);

        // 写入redis
        cacheClient.set(key, JSONUtil.toJsonStr(practiceEntity),PRACTICE_ID_TTL, TimeUnit.MINUTES);
        return CommonResult.success(practiceEntity);
    }

    @Override
    public CommonResult<?> PracticeNum() {
        int num = practiceMapper.PracticeNum();
        return CommonResult.success(num);
    }

    /**
     * 模糊查询我发布的数量
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
     * id查询我发布的数量
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
     * 模糊查询发布的实践
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
            return CommonResult.failed("没有该实践");
        }
        for (PracticeEntity practiceEntity: practiceEntities) {
            Long couId = practiceEntity.getCouId();
            CoursEntity coursEntity = serverCoursClient.BuyCourseId(couId);
            practiceEntity.setCoursEntity(coursEntity);
            UserDto trueName = serverUserClient.getTrueName(practiceEntity.getTeaId());
            practiceEntity.setUser(BeanUtil.copyProperties(trueName, UserEntity.class));
            if (practiceEntity.getPraTime().isBefore(LocalDateTime.now()) && practiceEntity.getEndTime().isAfter(LocalDateTime.now())) {
                // 如果进行中，更换状态
                practiceEntity.setIsBegin(true);
                practiceMapper.updateStatus(practiceEntity);
            }
            practiceEntity.setIsBegin(false);
            practiceMapper.updateStatus(practiceEntity);
        }
        return CommonResult.success(practiceEntities);
    }

    /**
     * 模糊查询发布实践的数量
     * @param praName
     * @return
     */
    @Override
    public CommonResult<?> PracticeByNameNum(String praName) {
        int num = practiceMapper.PracticeByNameNum(praName);
        return CommonResult.success(num);
    }

    /**
     * 根据id查询发布的实践数量
     * @param praId
     * @return
     */
    @Override
    public CommonResult<?> PracticeByIdNum(Long praId) {
        int num = practiceMapper.PracticeByIdNum(praId);
        return CommonResult.success(num);
    }

    /**
     * 根据id查询发布的实践
     * @param praId
     * @return
     */
    @Override
    public CommonResult<?> PracticeById(Long praId) {
        List<PracticeEntity> list = new ArrayList<>();
        PracticeEntity practiceEntity = practiceMapper.practiceById(praId);
        if (practiceEntity == null) {
            return CommonResult.failed("该实践不存在或已被删除!");
        }
        Long couId = practiceEntity.getCouId();
        CoursEntity coursEntity = serverCoursClient.BuyCourseId(couId);
        practiceEntity.setCoursEntity(coursEntity);
        UserDto trueName = serverUserClient.getTrueName(practiceEntity.getTeaId());
        practiceEntity.setUser(BeanUtil.copyProperties(trueName, UserEntity.class));
        if (practiceEntity.getPraTime().isBefore(LocalDateTime.now()) && practiceEntity.getEndTime().isAfter(LocalDateTime.now())) {
            // 如果进行中，更换状态
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
                return CommonResult.failed("开始时间不能为空!");
            } else if (practiceEntity.getEndTime() == null) {
                return CommonResult.failed("结束时间不能为空!");
            }
            Boolean isSuccess = practiceMapper.updatePractice(practiceEntity);
            if(isSuccess) {
                return CommonResult.success("修改成功!");
            }
        }catch (Exception e) {
            e.getStackTrace();
            throw new RuntimeException();
        }
        return CommonResult.failed("更新失败!");
    }
}
