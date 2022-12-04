package com.zhe.spoc.distributed.controller;

import com.zhe.common.api.CommonResult;
import com.zhe.common.api.ResultCode;
import com.zhe.common.entity.LearningProgressEntity;
import com.zhe.common.entity.StuCouProgress;
import com.zhe.spoc.distributed.service.ProgressService;
import com.zhe.spoc.distributed.service.StuCouProgressService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author HouEnZhu
 * @ClassName ProgressController
 * @Description TODO
 * @date 2022/10/30 11:36
 * @Version 1.0
 */

@RestController
public class ProgressController {

    @Autowired
    private ProgressService progressService;

    @Autowired
    private StuCouProgressService stuCouProgressService;

    /**
     * 记录学习时长
     * @param progressEntity
     * @return
     */
    @PostMapping("/RecordProgress")
    @PreAuthorize("hasAnyAuthority('teacher', 'student')")
    public CommonResult<Boolean> RecordProgress(@RequestBody LearningProgressEntity progressEntity) {
        return progressService.InsertStudentProgress(progressEntity);
    }

    @GetMapping("/ResumeProgress")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult<String> ResumeProgress(@RequestBody StuCouProgress stuCouProgress) {
        return progressService.ResumeProgress(stuCouProgress);
    }

    /**
     * 保存或更新章节进度
     */
    @PostMapping("/RecordProgressChapter")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult<Boolean> RecordProgressChapter(@RequestBody StuCouProgress stuCouProgress) {
        return stuCouProgressService.SaveProgressChapter(stuCouProgress);
    }
}
