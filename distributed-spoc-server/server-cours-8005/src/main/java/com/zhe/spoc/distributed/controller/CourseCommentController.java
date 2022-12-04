package com.zhe.spoc.distributed.controller;

import com.zhe.common.api.CommonResult;
import com.zhe.common.api.ResultCode;
import com.zhe.common.entity.CourseAppraiseEntity;
import com.zhe.spoc.distributed.service.CourseCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author HouEnZhu
 * @ClassName CourseCommentController
 * @Description TODO
 * @date 2022/10/26 19:49
 * @Version 1.0
 */

@RestController
@RequestMapping("/comment")
public class CourseCommentController {

    @Autowired
    private CourseCommentService courseCommentService;

    @PostMapping("/CourseComment")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult<Boolean> CourseComment(@RequestBody CourseAppraiseEntity courseAppraiseEntity) {
        Boolean aBoolean = courseCommentService.CommentCourse(courseAppraiseEntity);
        if (!aBoolean) {
            return CommonResult.failed(ResultCode.FAILED, "您输入的内容不能为空,请重试!");
        }
        return CommonResult.success(true, "评论成功!");
    }

    @GetMapping("/theCourseComment")
    @PreAuthorize("hasAnyAuthority('teacher', 'student')")
    public CommonResult<?> theCourseComment(@RequestParam Long courseId) {
        return courseCommentService.theCourseComment(courseId);
    }
}
