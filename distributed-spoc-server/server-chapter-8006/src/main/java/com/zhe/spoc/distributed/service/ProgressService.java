package com.zhe.spoc.distributed.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhe.common.api.CommonResult;
import com.zhe.common.entity.LearningProgressEntity;
import com.zhe.common.entity.StuCouProgress;

import java.util.List;

public interface ProgressService extends IService<LearningProgressEntity> {

    CommonResult<Boolean> InsertStudentProgress(LearningProgressEntity learningProgress);

    CommonResult<Boolean> RecordStudentProgress(int second);

    CommonResult<String> ResumeProgress(StuCouProgress stuCouProgress);

}
