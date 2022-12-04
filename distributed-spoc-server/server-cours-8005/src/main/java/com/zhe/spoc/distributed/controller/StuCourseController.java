package com.zhe.spoc.distributed.controller;

import com.zhe.common.api.CommonResult;
import com.zhe.common.api.ResultCode;
import com.zhe.common.entity.CoursEntity;
import com.zhe.common.entity.CourseInteractionEntity;
import com.zhe.spoc.distributed.service.CoursService;
import com.zhe.spoc.distributed.service.StuCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author HouEnZhu
 * @ClassName StuCourseController
 * @Description TODO
 * @date 2022/10/25 10:35
 * @Version 1.0
 */

@RestController
@RequestMapping("/student")
public class StuCourseController {

    @Autowired
    private StuCourseService stuCourseService;

    @Autowired
    private CoursService coursService;

    @GetMapping("/MyCourseAll")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult<Map<String, Object>> MyCourseAll() {
        Map<String, Object> map = stuCourseService.MyCourseList();
        if (map == null) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "没有课程信息");
        }
        return CommonResult.success(map);
    }

    @GetMapping("/MyClassCourseAll")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult<Map<String, Object>> MyClassCourseAll(@RequestParam Long classId) {
        Map<String, Object> map = stuCourseService.MycouseList_1(classId);
        if (map == null) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "暂无数据");
        }
        return CommonResult.success(map);
    }

    // 浏览可选课程
    @GetMapping("/OptionalCourses")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult<List<CoursEntity>> OptionalCourses() {
        List<CoursEntity> coursEntities = stuCourseService.OptionalCourses();
        if (coursEntities == null) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "暂无数据");
        }
        return CommonResult.success(coursEntities);
    }

    // 多条件动态搜索
    @GetMapping("/findByCourse")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult<List<CoursEntity>> findByCourse(@RequestBody CoursEntity coursEntity) {
        List<CoursEntity> byCourse = stuCourseService.findByCourse(coursEntity);
        if (byCourse == null) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "暂无数据");
        }
        return CommonResult.success(byCourse);
    }

    @GetMapping("/findByOne")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult<CoursEntity> findByOne(@RequestParam Long couId) {
        CoursEntity byOneCourse = stuCourseService.findByOneCourse(couId);
        if (byOneCourse == null) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "暂无数据");
        }
        return CommonResult.success(byOneCourse);
    }

    @GetMapping("/Recommend/{couId}")
    @PreAuthorize("hasAnyAuthority('student', 'teacher')")
    public List<CourseInteractionEntity> Recommend(@PathVariable("couId") Long couId) {
        return stuCourseService.Recommend(couId);
    }

    @GetMapping("/Collect/{couId}")
    @PreAuthorize("hasAnyAuthority('student', 'teacher')")
    public List<CourseInteractionEntity> Collect(@PathVariable("couId") Long couId) {
        return stuCourseService.Collect(couId);
    }

    /**
     * 学生端用于分类课程查看
     * @param couType
     * @return
     */
    @GetMapping("/getAllCourseByType")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult<?> getAllCourseByType(@RequestParam Integer couType) {
        return coursService.getAllCourseByType(couType);
    }

}
