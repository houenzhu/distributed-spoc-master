package com.zhe.spoc.distributed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhe.common.entity.CoursEntity;
import com.zhe.common.entity.CourseInteractionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author HouEnZhu
 * @ClassName StuCourseMapper
 * @Description TODO
 * @date 2022/10/25 10:33
 * @Version 1.0
 */

@Mapper
@Repository
public interface StuCourseMapper extends BaseMapper<CoursEntity> {
    List<CoursEntity> couseList(@Param("stuId") Integer stuId);

    List<CoursEntity> couseList_1(@Param("classId") Long classId);

    List<CoursEntity> OptionalCourses();

    List<CoursEntity> findByCourse(CoursEntity coursEntity);

    CoursEntity findByOneCourse(@Param("couId") Long couId);

    // 记录点赞数
    List<CourseInteractionEntity> Recommend(@Param("couId") Long couId);

    List<CourseInteractionEntity> Collect(@Param("couId") Long couId);
}
