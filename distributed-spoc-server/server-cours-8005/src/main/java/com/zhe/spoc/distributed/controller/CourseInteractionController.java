package com.zhe.spoc.distributed.controller;

import com.zhe.common.api.CommonResult;
import com.zhe.common.api.ResultCode;
import com.zhe.common.entity.CommentEntity;
import com.zhe.common.entity.CourseInteractionEntity;
import com.zhe.spoc.distributed.service.CourseInteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author HouEnZhu
 * @ClassName CourseInteractionController
 * @Description TODO
 * @date 2022/10/26 15:58
 * @Version 1.0
 */

@RestController
@RequestMapping("/interaction")
public class CourseInteractionController {

    @Autowired
    private CourseInteractionService courseInteractionService;

    @PostMapping("/recommand")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult<Boolean> recommand(@RequestBody CourseInteractionEntity courseInteractionEntity) {
        Boolean aBoolean = courseInteractionService.NoDataRecommend(courseInteractionEntity);
        if (!aBoolean) {
            return CommonResult.success(true, "已取消点赞!");
        }
        return CommonResult.success( true, "点赞成功!");
    }

    @PostMapping("/collect")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult<Boolean> collect(@RequestBody CourseInteractionEntity courseInteractionEntity) {
        Boolean aBoolean = courseInteractionService.NoDataCollect(courseInteractionEntity);
        if (!aBoolean) {
            return CommonResult.success(true, "已取消收藏!");
        }
        return CommonResult.success( true, "收藏成功!");
    }

    @PostMapping("/have")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult<Boolean> have(@RequestBody CourseInteractionEntity courseInteractionEntity) {
        Boolean aBoolean = courseInteractionService.NoDataHave(courseInteractionEntity);
        if (!aBoolean) {
            return CommonResult.success(true, "还未拥有该课程!");
        }
        return CommonResult.success( true, "已经拥有该课程!");
    }

    /**
     * 判断该学生是否点赞、收藏、或者拥有
     */
    @GetMapping("/judge")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult<?> judge(@RequestParam Long couId) {
        return courseInteractionService.judge(couId);
    }

    /**
     * 查看我的收藏
     * @return
     */
    @GetMapping("/checkMyCollect")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult<?> checkMyCollect() {
        return courseInteractionService.checkMyCollect();
    }
}
