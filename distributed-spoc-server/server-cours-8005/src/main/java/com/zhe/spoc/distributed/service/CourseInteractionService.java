package com.zhe.spoc.distributed.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhe.common.api.CommonResult;
import com.zhe.common.entity.CourseInteractionEntity;

public interface CourseInteractionService extends IService<CourseInteractionEntity> {
    // 还未进行过三连之一操作的 (点赞)
    Boolean NoDataRecommend(CourseInteractionEntity courseInteractionEntity);

    // 还未进行过三连之一操作的 (收藏)
    Boolean NoDataCollect(CourseInteractionEntity courseInteractionEntity);

    // 还未进行过三连之一操作的 (拥有)
    Boolean NoDataHave(CourseInteractionEntity courseInteractionEntity);

    CommonResult<?> judge(Long couId);

    CommonResult<?> checkMyCollect();
}
