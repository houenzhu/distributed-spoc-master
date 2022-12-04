package com.zhe.spoc.distributed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhe.common.entity.CommentEntity;
import com.zhe.common.entity.TopicQuestionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author HouEnZhu
 * @ClassName QuestionMapper
 * @Description TODO
 * @date 2022/10/28 16:50
 * @Version 1.0
 */

@Mapper
public interface QuestionMapper extends BaseMapper<TopicQuestionEntity> {
    Boolean InsertQuestion(TopicQuestionEntity topicQuestionEntity);

    List<TopicQuestionEntity> MyQuestion(@Param("userId") Integer userId);

    TopicQuestionEntity Question(@Param("questId") Long questId);

    List<TopicQuestionEntity> AllQuestion();

    /**
     * 问题详情
     * @param questId
     * @return
     */
    TopicQuestionEntity QuestionDetails(@Param("questId") Long questId);
}
