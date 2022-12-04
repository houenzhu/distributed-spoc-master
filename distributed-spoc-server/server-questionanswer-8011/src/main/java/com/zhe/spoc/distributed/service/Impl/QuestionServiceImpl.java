package com.zhe.spoc.distributed.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhe.common.api.CommonResult;
import com.zhe.common.api.ResultCode;
import com.zhe.common.dto.AnswerDTO;
import com.zhe.common.dto.UserDto;
import com.zhe.common.dto.UserDto_1;
import com.zhe.common.entity.CommentEntity;
import com.zhe.common.entity.TopicQuestionEntity;
import com.zhe.common.entity.UserEntity;
import com.zhe.spoc.distributed.client.ServerUserClient;
import com.zhe.spoc.distributed.mapper.AnswerMapper;
import com.zhe.spoc.distributed.mapper.QuestionMapper;
import com.zhe.spoc.distributed.service.QuestionService;
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
import java.util.stream.Collectors;

/**
 * @author HouEnZhu
 * @ClassName QuestionServiceImpl
 * @Description TODO
 * @date 2022/10/28 16:50
 * @Version 1.0
 */

@Service
@Slf4j
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, TopicQuestionEntity> implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    private ServerUserClient serverUserClient;
    // 提出问题
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean InsertQuestion(TopicQuestionEntity topicQuestionEntity) {
        if (topicQuestionEntity == null) {
            return false;
        }
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        topicQuestionEntity.setUId(user.getId());
        boolean a = false;
        try {
            a = questionMapper.InsertQuestion(topicQuestionEntity);
        }catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.getStackTrace();
            throw new RuntimeException();
        }
        return a;
    }

    @Override
    public Map<String, Object> MyQuestionMap() {
        Map<String, Object> map = new HashMap<>();
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<TopicQuestionEntity> topicQuestionEntities = questionMapper.MyQuestion(user.getId());
        if (CollectionUtils.isEmpty(topicQuestionEntities)) {
            return null;
        }
        map.put("myComment", topicQuestionEntities);
        return map;
    }

    /**
     * 全部问题
     * @return
     */
    @Override
    public CommonResult<?> AllQuestion() {
        List<TopicQuestionEntity> topicQuestionEntities = questionMapper.AllQuestion();
        if (CollectionUtils.isEmpty(topicQuestionEntities)) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "暂无问题");
        }
        for (TopicQuestionEntity topicQuestionEntity : topicQuestionEntities) {
            Long questId = topicQuestionEntity.getQuestId();
            Integer uId = topicQuestionEntity.getUId();
            UserDto trueName = serverUserClient.getTrueName(uId);
            topicQuestionEntity.setUserDto(trueName);
            int allAnswerNum = answerMapper.AllAnswerNum(questId);
            topicQuestionEntity.setAnswerNum(allAnswerNum);
        }
        return CommonResult.success(topicQuestionEntities);
    }

    @Override
    public CommonResult<?> GetAllAnswer(Long tqId) {
        if (tqId == null) {
            return CommonResult.failed(ResultCode.FAILED, "问题id不能为空!");
        }
        List<AnswerDTO> answerDTOS = answerMapper.AnswerByQuestId(tqId);
        if (CollectionUtils.isEmpty(answerDTOS)) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "暂无回复噢");
        }
       answerDTOS
               .forEach(answer -> {
                   Integer uId = answer.getUId();
                   UserDto trueName = serverUserClient.getTrueName(uId);
                   answer.setUserDto(trueName);
               });

        return CommonResult.success(answerDTOS);
    }

    @Override
    public CommonResult<?> QuestionDetail(Long tqId) {
        if (tqId == null) {
            return CommonResult.failed(ResultCode.FAILED, "tqId不能为空!");
        }
        TopicQuestionEntity topicQuestionEntity = questionMapper.QuestionDetails(tqId);
        if (topicQuestionEntity == null) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "没有改问题或者已经删除了!");
        }
        Integer uId = topicQuestionEntity.getUId();
        UserEntity myUserInfo = serverUserClient.getMyUserInfo(uId);
        topicQuestionEntity.setUserDto_1(BeanUtil.copyProperties(myUserInfo, UserDto_1.class));
        return CommonResult.success(topicQuestionEntity);
    }
}
