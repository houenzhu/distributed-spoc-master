package com.zhe.spoc.distributed.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhe.common.api.CommonResult;
import com.zhe.common.entity.StuCouProgress;

public interface StuCouProgressService extends IService<StuCouProgress> {
    CommonResult<Boolean> SaveProgressChapter(StuCouProgress stuCouProgress);
}
