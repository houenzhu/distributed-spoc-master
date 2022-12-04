package com.zhe.spoc.distributed.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zhe.common.api.CommonResult;
import com.zhe.common.entity.CommentEntity;
import com.zhe.common.entity.TopicQuestionEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface QuestionService extends IService<TopicQuestionEntity> {

    Boolean InsertQuestion(TopicQuestionEntity topicQuestionEntity);

    Map<String, Object> MyQuestionMap();

    CommonResult<?> AllQuestion();

    CommonResult<?> GetAllAnswer(Long tqId);

    CommonResult<?> QuestionDetail(Long tqId);
}
