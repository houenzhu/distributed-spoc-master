package com.zhe.spoc.distributed.controller;

import com.zhe.common.api.CommonResult;
import com.zhe.common.api.ResultCode;
import com.zhe.common.entity.TopicQuestionEntity;
import com.zhe.spoc.distributed.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author HouEnZhu
 * @ClassName QuestionController
 * @Description TODO
 * @date 2022/10/28 16:55
 * @Version 1.0
 */

@RestController
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @PostMapping("/insertQuestion")
    @PreAuthorize("hasAnyAuthority('student', 'teacher')")
    public CommonResult<Boolean> insertQuestion(@RequestBody TopicQuestionEntity topicQuestionEntity) {
        Boolean aBoolean = questionService.InsertQuestion(topicQuestionEntity);
        if (!aBoolean) {
            return CommonResult.failed(ResultCode.FAILED, "发布失败!");
        }
        return CommonResult.success(true, "发布成功!");
    }

    /**
     * 获取我的评论
     * @return
     */
    @GetMapping("/MyQuestion")
    @PreAuthorize("hasAnyAuthority('teacher', 'student')")
    public CommonResult<Map<String, Object>> MyQuestion() {
        Map<String, Object> map = questionService.MyQuestionMap();
        if (map == null) {
            return CommonResult.failed(ResultCode.FAILED, "还没有发布任何问题噢!");
        }
        return CommonResult.success(map);
    }

    /**
     * 获取全部问题
     * @return
     */
    @GetMapping("/AllQuestion")
    @PreAuthorize("hasAnyAuthority('teacher', 'student')")
    public CommonResult<?> AllQuestion() {
        return questionService.AllQuestion();
    }

    /**
     * 获取这个问题的全部回复
     * @param tqId
     * @return
     */
    @GetMapping("/GetAllAnswer")
    @PreAuthorize("hasAnyAuthority('teacher', 'student')")
    public CommonResult<?> GetAllAnswer(@RequestParam Long tqId) {
        return questionService.GetAllAnswer(tqId);
    }

    /**
     * 问题详情
     * @return
     */
    @GetMapping("/QuestionDetail")
    @PreAuthorize("hasAnyAuthority('teacher', 'student')")
    public CommonResult<?> QuestionDetail(@RequestParam Long tqId) {
        return questionService.QuestionDetail(tqId);
    }

}
