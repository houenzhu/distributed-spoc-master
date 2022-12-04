package com.zhe.spoc.distributed.client;

import com.zhe.common.dto.CourseDTO;
import com.zhe.common.entity.CoursEntity;
import com.zhe.common.entity.CourseInteractionEntity;
import com.zhe.common.entity.StudentEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("cours-server")
public interface ServerCoursClient {

    // 获取该班级所有课程(用于远程调用)
    @GetMapping("/teacher/getAllCours/{classId}")
    @PreAuthorize("hasAuthority('teacher')")
    public List<CoursEntity> getAllCours(@PathVariable("classId") Long classId);

    // 用于查看价格或者其他的
    @GetMapping("/teacher/BuyCourseId/{couId}")
    @PreAuthorize("hasAnyAuthority('student', 'teacher')")
    public CoursEntity BuyCourseId(@PathVariable("couId") Long couId);


    // 统计点赞数
    @GetMapping("/student/Recommend/{couId}")
    @PreAuthorize("hasAnyAuthority('student', 'teacher')")
    public List<CourseInteractionEntity> Recommend(@PathVariable("couId") Long couId);

    // 统计拥有数
    @GetMapping("/student/Collect/{couId}")
    @PreAuthorize("hasAnyAuthority('student', 'teacher')")
    public List<CourseInteractionEntity> Collect(@PathVariable("couId") Long couId);

    @GetMapping("/teacher/getCourseDto/{couId}")
    @PreAuthorize("hasAnyAuthority('teacher')")
    CourseDTO getCourseDto(@PathVariable("couId") Long couId);
}
