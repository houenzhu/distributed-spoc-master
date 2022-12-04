package com.zhe.spoc.distributed.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhe.common.entity.CoursEntity;
import com.zhe.common.entity.CourseInteractionEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface StuCourseService extends IService<CoursEntity> {
    List<CoursEntity> courseList(Integer stuId);

    Map<String, Object> MyCourseList();

    List<CoursEntity> couseList_1(Long classId);

    Map<String, Object> MycouseList_1(Long classId);

    List<CoursEntity> OptionalCourses();

    List<CoursEntity> findByCourse(CoursEntity coursEntity);

    CoursEntity findByOneCourse(Long couId);

    List<CourseInteractionEntity> Recommend(Long couId);

    List<CourseInteractionEntity> Collect(Long couId);


}
