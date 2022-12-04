package com.zhe.spoc.distributed.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhe.common.api.CommonResult;
import com.zhe.common.entity.AnswerEntity;
import com.zhe.common.entity.TopicQuestionEntity;
import com.zhe.common.entity.UserEntity;
import com.zhe.spoc.distributed.mapper.AnswerMapper;
import com.zhe.spoc.distributed.mapper.QuestionMapper;
import com.zhe.spoc.distributed.service.AnswerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HouEnZhu
 * @ClassName AnswerServiceImpl
 * @Description TODO
 * @date 2022/10/28 17:36
 * @Version 1.0
 */
@Service
@Slf4j
public class AnswerServiceImpl extends ServiceImpl<AnswerMapper, AnswerEntity> implements AnswerService {

    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean insertAnswer(AnswerEntity answerEntity) {
        if (answerEntity == null) {
            return false;
        }
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        answerEntity.setUId(user.getId());
        Boolean a = false;
        try {
            a = answerMapper.insertAnswer(answerEntity);
        }catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // 事务回滚
            e.getStackTrace();
            throw new RuntimeException();
        }
        return a;
    }

    @Override
    public Boolean updateAdopt(AnswerEntity answerEntity) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        answerEntity.setUId(user.getId());// 采纳者id
        answerEntity.setIsSelect(true);
        return answerMapper.updateAdopt(answerEntity);
    }

    @Override
    public Map<String, Object> myAnswer() {
        Map<String, Object> map = new HashMap<>();
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<AnswerEntity> answerEntities = answerMapper.myAnswer(user.getId());
        if (CollectionUtils.isEmpty(answerEntities)) {
            return null;
        }
        for (AnswerEntity answerEntity: answerEntities) {
            TopicQuestionEntity question = questionMapper.Question(answerEntity.getTqId());
            answerEntity.setTopicQuestionEntity(question);
        }
        map.put("myAnswer", answerEntities);
        return map;
    }
}
