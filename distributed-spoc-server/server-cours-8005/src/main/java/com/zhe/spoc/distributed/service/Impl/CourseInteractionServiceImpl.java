package com.zhe.spoc.distributed.service.Impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhe.common.api.CommonResult;
import com.zhe.common.entity.CoursEntity;
import com.zhe.common.entity.CourseInteractionEntity;
import com.zhe.common.entity.StuCourseEntity;
import com.zhe.common.entity.UserEntity;
import com.zhe.spoc.distributed.mapper.CoursMapper;
import com.zhe.spoc.distributed.mapper.CourseInteractionMapper;
import com.zhe.spoc.distributed.service.CourseInteractionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author HouEnZhu
 * @ClassName CourseInteractionServiceImpl
 * @Description TODO
 * @date 2022/10/26 15:56
 * @Version 1.0
 */

@Service
@Slf4j
public class CourseInteractionServiceImpl extends ServiceImpl<CourseInteractionMapper, CourseInteractionEntity>
        implements CourseInteractionService {

    @Autowired
    private CourseInteractionMapper courseInteractionMapper;

    @Autowired
    private CoursMapper coursMapper;
    // 点赞或者取消
    @Override
    public Boolean NoDataRecommend(CourseInteractionEntity courseInteractionEntity) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = user.getId();
        courseInteractionEntity.setStuId(userId);
        CourseInteractionEntity courseInteractionEntity1 = courseInteractionMapper.CheckHaveData(courseInteractionEntity);
        if (courseInteractionEntity1 != null) { // 还未对此课程进行过三连操作
            if (!courseInteractionEntity1.getRecommend()) {
                courseInteractionEntity.setRecommend(true); // 点赞
                return courseInteractionMapper.HaveDataRecommend(courseInteractionEntity);
            }
            courseInteractionEntity.setRecommend(false); // 取消点赞
            courseInteractionMapper.HaveDataRecommend(courseInteractionEntity);
            return false;
        }
        courseInteractionEntity.setRecommend(true);
        return courseInteractionMapper.NoDataRecommend(courseInteractionEntity);
    }

    // 收藏或者取消
    @Override
    public Boolean NoDataCollect(CourseInteractionEntity courseInteractionEntity) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = user.getId();
        courseInteractionEntity.setStuId(userId);
        CourseInteractionEntity courseInteractionEntity1 = courseInteractionMapper.CheckHaveData(courseInteractionEntity);
        if (courseInteractionEntity1 != null) {
            if (!courseInteractionEntity1.getCollect()) {
                courseInteractionEntity.setCollect(true); // 收藏
                return courseInteractionMapper.HaveDataCollect(courseInteractionEntity);
            }
            courseInteractionEntity.setCollect(false); // 取消收藏
            courseInteractionMapper.HaveDataCollect(courseInteractionEntity);
            return false;
        }
        courseInteractionEntity.setCollect(true);
        return courseInteractionMapper.NoDataCollect(courseInteractionEntity);
    }

    // 拥有或未拥有
    @Override
    public Boolean NoDataHave(CourseInteractionEntity courseInteractionEntity) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = user.getId();
        courseInteractionEntity.setStuId(userId);
        StuCourseEntity stuCourseEntity = courseInteractionMapper.HaveCourse(courseInteractionEntity);
        CourseInteractionEntity courseInteractionEntity1 = courseInteractionMapper.CheckHaveData(courseInteractionEntity);
        if (courseInteractionEntity1 != null) {
            if (stuCourseEntity != null) { // 已经拥有该课程但是有点赞或者收藏的记录
                courseInteractionEntity.setHave(true);
                return courseInteractionMapper.HaveDataHave(courseInteractionEntity);
            }
            courseInteractionEntity.setHave(false); // 还未拥有该课程但是有点赞或者收藏的记录
            courseInteractionMapper.HaveDataHave(courseInteractionEntity);
            return false;
        }
        if (stuCourseEntity != null) {
            courseInteractionEntity.setHave(true); // 拥有该课程但没有点赞或者收藏的记录
            return courseInteractionMapper.NoDataHave(courseInteractionEntity);
        }
        courseInteractionEntity.setHave(false); // 还未拥有该课程也没有点赞或者收藏的记录
        return courseInteractionMapper.NoDataHave(courseInteractionEntity);
    }

    /**
     * 判断该学生是否点赞、收藏、或者拥有
     */
    @Override
    public CommonResult<?> judge(Long couId) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CourseInteractionEntity judge = courseInteractionMapper.judge(couId, user.getId());
        if (judge == null) {
            return CommonResult.success(null);
        }
        return CommonResult.success(judge);
    }

    /**
     * 查看我的收藏
     * @return
     */
    @Override
    public CommonResult<?> checkMyCollect() {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // 获取该学生id
        Integer userId = user.getId();

        List<CourseInteractionEntity> courseInteractionEntities = courseInteractionMapper.checkMyCollect(userId);
        if (CollectionUtils.isEmpty(courseInteractionEntities)) {
            return CommonResult.success("暂时没有收藏记录!");
        }
        // 我收藏的课程id
        List<Long> courseIds = courseInteractionEntities.stream()
                .distinct()
                .map(CourseInteractionEntity::getCouId)
                .collect(Collectors.toList());
        List<CoursEntity> coursEntities = coursMapper.selectBatchIds(courseIds);
        return CommonResult.success(coursEntities);
    }

}
