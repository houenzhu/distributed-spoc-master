package com.zhe.spoc.distributed.service.Impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhe.common.api.CommonResult;
import com.zhe.common.api.ResultCode;
import com.zhe.common.entity.LearningProgressEntity;
import com.zhe.common.entity.StuCouProgress;
import com.zhe.common.entity.StudentEntity;
import com.zhe.common.entity.UserEntity;
import com.zhe.spoc.distributed.client.ServerStuClient;
import com.zhe.spoc.distributed.mapper.ProgressMapper;
import com.zhe.spoc.distributed.service.ProgressService;
import com.zhe.spoc.distributed.utils.CacheClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.zhe.common.utils.RedisConstants.*;

/**
 * @author HouEnZhu
 * @ClassName ProgressServiceImpl
 * @Description TODO
 * @date 2022/10/30 10:41
 * @Version 1.0
 */

@Service
@Slf4j
public class ProgressServiceImpl extends ServiceImpl<ProgressMapper, LearningProgressEntity> implements ProgressService {

    @Autowired
    private ProgressMapper progressMapper;

    @Autowired
    private ServerStuClient serverStuClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private CacheClient cacheClient;

    // 记录学习时长
    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<Boolean> InsertStudentProgress(LearningProgressEntity learningProgress) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        learningProgress.setStuId(user.getId());
        StudentEntity stuInfo = serverStuClient.getMyInfoFeign(user.getId());
        Integer time = progressMapper.checkStudent(learningProgress); // 查询这个学生是否有学习这个章节的记录 这是之前的时长
        int beforeTime = learningProgress.getTime(); // 获取学习的时长
        if (time == null || time == 0){
            // 奖励机制
            if (beforeTime < 60){
                return CommonResult.failed(ResultCode.FAILED, "您观看的时长不足一分钟!没有积分奖励!");
            }
            try {
                Double money = (beforeTime / 60) * 1.5; // 学习积分增加
                Double stuScore = stuInfo.getStuScore();
                stuScore = stuScore + money;
                progressMapper.addScoreStuTime(stuScore, beforeTime, user.getId());
                return CommonResult.success(progressMapper.InsertStudentProgress(learningProgress), "学习时长记录成功!积分增加了: " + money); // 若没有 插入一条
            }catch (Exception e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                log.debug("出错啦!!");
                e.getStackTrace();
            }
        }
        try {
            // 奖励机制
            if (beforeTime < 60){
                return CommonResult.failed(ResultCode.FAILED, "您观看的时长不足一分钟!");
            }
            Double money = (beforeTime / 60) * 1.5; // 学习积分增加
            Double stuScore = stuInfo.getStuScore();
            Integer stuStudyTime = stuInfo.getStuStudyTime();
            stuStudyTime = stuStudyTime + beforeTime; // 添加总学习时长
            stuScore = stuScore + money;
            progressMapper.addScoreStuTime(stuScore,stuStudyTime,user.getId());
            time = time + beforeTime; // 加上记录的时长
            learningProgress.setTime(time);
            return CommonResult.success(progressMapper.UpdateStudentProgress(learningProgress), "记录成功!积分增加了: " + money); // 有则更新
        }catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.getStackTrace();
            return CommonResult.failed(ResultCode.FAILED, "失败了!");
        }
    }


    @Override
    public CommonResult<Boolean> RecordStudentProgress(int second) {
        return null;
    }

    /**
     * 下次学习时恢复本次学习进度
     * @return
     */
    @Override
    public CommonResult<String> ResumeProgress(StuCouProgress stuCouProgress) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = user.getId();
        Long couId = stuCouProgress.getCouId();
        Long chaId = stuCouProgress.getChaId();
        // 先查redis
        String progressJson = redisTemplate.opsForValue().get(MY_COURSE_PROGRESS_KEY + couId + ":" + chaId + ":" + userId);
        // 有
        if (StrUtil.isNotBlank(progressJson)) {
            // 返回结果
            return CommonResult.success(progressJson, "恢复成功!");
        }
        // 没有
        if (progressJson != null) {
            // 返回错误结果
            return CommonResult.failed("暂无结果");
        }

        // 查询数据库
        // 没有数据
        stuCouProgress.setStuId(userId);
        String progressTime = progressMapper.progressTime(stuCouProgress);
        if (StrUtil.isBlank(progressTime)) {
            redisTemplate.opsForValue().set(MY_COURSE_PROGRESS_KEY + couId + ":" + chaId + ":" + userId, "",
                    MY_COURSE_PROGRESS_NULL_KEY_TTL, TimeUnit.SECONDS);
            // 返回错误结果
            return CommonResult.failed("暂无结果");
        }

        // 写入redis
        cacheClient.set(MY_COURSE_PROGRESS_KEY + couId + ":" + chaId + ":" + userId, JSONUtil.toJsonStr(progressTime),
                MY_COURSE_PROGRESS_KEY_TTL, TimeUnit.MINUTES);
        return CommonResult.success(progressTime, "恢复成功!");
    }


}
