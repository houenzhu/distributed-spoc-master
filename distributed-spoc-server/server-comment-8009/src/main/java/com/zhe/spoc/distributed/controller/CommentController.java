package com.zhe.spoc.distributed.controller;

import com.zhe.common.api.CommonResult;
import com.zhe.common.api.ResultCode;
import com.zhe.common.entity.CommentEntity;
import com.zhe.spoc.distributed.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author HouEnZhu
 * @ClassName CommentController
 * @Description TODO
 * @date 2022/10/24 14:21
 * @Version 1.0
 */

@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 评论资讯
     * @param commentEntity
     * @return
     */
    @PostMapping("/insertComment")
    @PreAuthorize("hasAnyAuthority('teacher', 'student')")
    public CommonResult<Boolean> insertComment(@RequestBody CommentEntity commentEntity) {
        Boolean aBoolean = commentService.InsertComment(commentEntity);
        if (!aBoolean) {
            return CommonResult.failed(ResultCode.FAILED, "评论失败!");
        }
        return CommonResult.success(true, "评论成功!");
    }

    @GetMapping("/MyComment")
    @PreAuthorize("hasAnyAuthority('teacher', 'student')")
    public CommonResult<Map<String, Object>> MyComment() {
        Map<String, Object> map = commentService.MyCommentMap();
        if (map == null) {
            return CommonResult.failed(ResultCode.FAILED, "还没有发布任何问题噢!");
        }
        return CommonResult.success(map);
    }
}
