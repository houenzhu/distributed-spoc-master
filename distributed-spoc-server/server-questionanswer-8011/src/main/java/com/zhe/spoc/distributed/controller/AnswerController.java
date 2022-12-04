package com.zhe.spoc.distributed.controller;

import com.zhe.common.api.CommonResult;
import com.zhe.common.api.ResultCode;
import com.zhe.common.entity.AnswerEntity;
import com.zhe.spoc.distributed.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author HouEnZhu
 * @ClassName AnswerController
 * @Description TODO
 * @date 2022/10/28 17:36
 * @Version 1.0
 */
@RestController
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    @PostMapping("/insertAnswer")
    @PreAuthorize("hasAnyAuthority('teacher', 'student')")
    public CommonResult<Boolean> insertAnswer(@RequestBody AnswerEntity answerEntity){
        Boolean aBoolean = answerService.insertAnswer(answerEntity);
        if (!aBoolean) {
            return CommonResult.failed(ResultCode.FAILED, "回复失败!");
        }
        return CommonResult.success(true, "回复成功!");
    }

    @PostMapping("/adopt")
    @PreAuthorize("hasAnyAuthority('teacher', 'student')")
    public CommonResult<Boolean> adopt(@RequestBody AnswerEntity answerEntity){
        Boolean aBoolean = answerService.updateAdopt(answerEntity);
        if (!aBoolean) {
            return CommonResult.failed(ResultCode.FAILED, "采纳失败!非发布问题者无法采纳");
        }
        return CommonResult.success(true, "采纳成功!");
    }

    /**
     * 我的发布问题
     * @return
     */
    @GetMapping("/myAnswer")
    @PreAuthorize("hasAnyAuthority('teacher', 'student')")
    public CommonResult<Map<String, Object>> myAnswer(){
        Map<String, Object> map = answerService.myAnswer();
        if (map == null) {
            return CommonResult.failed(ResultCode.FAILED, "还没有回复任何问题噢!");
        }
        return CommonResult.success(map);
    }

}
