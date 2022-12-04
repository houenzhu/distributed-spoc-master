package com.zhe.spoc.distributed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhe.common.dto.AnswerDTO;
import com.zhe.common.entity.AnswerEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author HouEnZhu
 * @ClassName AnswerMapper
 * @Description TODO
 * @date 2022/10/28 17:34
 * @Version 1.0
 */

@Mapper
public interface AnswerMapper extends BaseMapper<AnswerEntity> {
    Boolean insertAnswer(AnswerEntity answerEntity);

    Boolean updateAdopt(AnswerEntity answerEntity);

    List<AnswerEntity> myAnswer(@Param("uId") Integer uId);

    int AllAnswerNum(@Param("tqId") Long tqId);

    List<AnswerDTO> AnswerByQuestId(@Param("tqId") Long tqId);
}
