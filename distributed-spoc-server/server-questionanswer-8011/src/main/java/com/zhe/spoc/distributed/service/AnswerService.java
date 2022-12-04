package com.zhe.spoc.distributed.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhe.common.api.CommonResult;
import com.zhe.common.entity.AnswerEntity;

import java.util.Map;

public interface AnswerService extends IService<AnswerEntity> {
    Boolean insertAnswer(AnswerEntity answerEntity);

    Boolean updateAdopt(AnswerEntity answerEntity);

    Map<String, Object> myAnswer();

}
