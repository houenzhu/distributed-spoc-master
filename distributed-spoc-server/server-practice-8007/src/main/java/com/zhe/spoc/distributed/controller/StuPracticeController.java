package com.zhe.spoc.distributed.controller;

import com.zhe.common.api.CommonResult;
import com.zhe.common.api.ResultCode;
import com.zhe.common.entity.StuPracticeEntity;
import com.zhe.spoc.distributed.service.PracticeService;
import com.zhe.spoc.distributed.service.StuPracticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

/**
 * @author HouEnZhu
 * @ClassName StuPracticeController
 * @Description TODO
 * @date 2022/10/28 11:50
 * @Version 1.0
 */

@RestController
public class StuPracticeController {

    @Autowired
    private PracticeService practiceService;

    @Autowired
    private StuPracticeService stuPracticeService;

    // 报名实践
    @PostMapping("/insertStuPractice")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult<?> insertStuPractice(@RequestBody StuPracticeEntity stuPracticeEntity) {
        return practiceService.insertStuPractice(stuPracticeEntity);
    }

    @GetMapping("/SelectPractice")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult<Map<String, Object>> SelectPractice() {
        Map<String, Object> map = practiceService.SelectPractice();
        if (map == null) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "暂无实践课程信息");
        }
        return CommonResult.success(map);
    }

    // 取消我的实践
    @PostMapping("/deleteMyPractice")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult<Boolean> deleteMyPractice(@RequestBody StuPracticeEntity stuPracticeEntity) {
        Boolean aBoolean = stuPracticeService.deleteMyPractice(stuPracticeEntity);
        if (!aBoolean) {
            return CommonResult.failed(ResultCode.FAILED, "取消失败");
        }
        return CommonResult.success(true, "取消成功!");
    }

    // 查看我报名的实践
    @GetMapping("/CheckMyPractice")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult<Map<String, Object>> CheckMyPractice() {
        Map<String, Object> map = stuPracticeService.MyPractice();
        if (map == null) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "暂无报名的线下实践信息");
        }
        return CommonResult.success(map);
    }
}
