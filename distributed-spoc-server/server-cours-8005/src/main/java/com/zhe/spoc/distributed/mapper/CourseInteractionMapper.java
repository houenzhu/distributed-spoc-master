package com.zhe.spoc.distributed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhe.common.entity.CoursEntity;
import com.zhe.common.entity.CourseInteractionEntity;
import com.zhe.common.entity.StuCourseEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author HouEnZhu
 * @ClassName CourseInteractionMapper
 * @Description TODO
 * @date 2022/10/26 15:51
 * @Version 1.0
 */

@Mapper
@Repository
public interface CourseInteractionMapper extends BaseMapper<CourseInteractionEntity> {
    // 还未进行过三连之一操作的 (点赞)
    Boolean NoDataRecommend(CourseInteractionEntity courseInteractionEntity);
    Boolean HaveDataRecommend(CourseInteractionEntity courseInteractionEntity);

    // 还未进行过三连之一操作的 (收藏)
    Boolean NoDataCollect(CourseInteractionEntity courseInteractionEntity);
    Boolean HaveDataCollect(CourseInteractionEntity courseInteractionEntity);

    // 查看该课程是否拥有
    StuCourseEntity HaveCourse(CourseInteractionEntity courseInteractionEntity);
    Boolean NoDataHave(CourseInteractionEntity courseInteractionEntity);
    Boolean HaveDataHave(CourseInteractionEntity courseInteractionEntity);
    // 查询该课程是否有三连的痕迹
    CourseInteractionEntity CheckHaveData(CourseInteractionEntity courseInteractionEntity);


    CourseInteractionEntity judge(@Param("couId") Long couId, @Param("stuId") Integer stuId);

    /**
     * 查看我的收藏
     * @param userId
     * @return
     */
    List<CourseInteractionEntity> checkMyCollect(@Param("userId") Integer userId);
}
