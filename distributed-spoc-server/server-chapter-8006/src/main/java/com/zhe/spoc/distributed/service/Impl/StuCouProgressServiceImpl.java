package com.zhe.spoc.distributed.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhe.common.api.CommonResult;
import com.zhe.common.api.ResultCode;
import com.zhe.common.entity.StuCouProgress;
import com.zhe.common.entity.UserEntity;
import com.zhe.spoc.distributed.mapper.ProgressMapper;
import com.zhe.spoc.distributed.mapper.StuCouProgressMapper;
import com.zhe.spoc.distributed.service.StuCouProgressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.zhe.common.utils.RedisConstants.MY_COURSE_PROGRESS_KEY;

/**
 * @author HouEnZhu
 * @ClassName StuCouProgressServiceImpl
 * @Description TODO
 * @date 2022/11/7 19:53
 * @Version 1.0
 */

@Service
@Slf4j
public class StuCouProgressServiceImpl extends ServiceImpl<StuCouProgressMapper, StuCouProgress>
        implements StuCouProgressService {

    @Autowired
    private StuCouProgressMapper stuCouProgressMapper;

    @Autowired
    private ProgressMapper progressMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 记录本章节的学习进度
     * @param stuCouProgress
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> SaveProgressChapter(StuCouProgress stuCouProgress) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = user.getId();
        stuCouProgress.setStuId(userId);
        // 转成Integer
        String time1 = stuCouProgress.getTime();
        String to_time = progressMapper.sec_to_time(Integer.parseInt(time1));
        stuCouProgress.setTime(to_time);
        // 保存或更新
        // 先判断是否有记录
        int record = stuCouProgressMapper.HaveProgress(stuCouProgress);
        // 无记录
        if (record == 0) {
            // 添加记录
            save(stuCouProgress);
            return CommonResult.success(true, "保存成功!");
        }
        // 有记录 更新记录
        int id = stuCouProgressMapper.ProgressId(stuCouProgress);
        stuCouProgress.setId(id);
        boolean success = updateById(stuCouProgress);
        if (!success) {
            return CommonResult.failed(ResultCode.FAILED, "保存失败!");
        }
        Long couId = stuCouProgress.getCouId();
        Long chaId = stuCouProgress.getChaId();
        // 删除缓存的值
        stringRedisTemplate.delete(MY_COURSE_PROGRESS_KEY + couId + ":" + chaId + ":" + userId);
        return CommonResult.success(true, "保存成功!");
    }

}
