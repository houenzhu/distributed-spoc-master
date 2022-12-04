package com.zhe.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhe.common.dto.UserDto;
import com.zhe.common.dto.UserDto_1;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author HouEnZhu
 * @ClassName TopicQuestionEntity
 * @Description TODO
 * @date 2022/10/28 15:59
 * @Version 1.0
 */

@TableName("topic_question")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicQuestionEntity implements Serializable {

    /**
     * id
     */
    @TableField("quest_id")
    @TableId(type = IdType.ASSIGN_ID)
    private Long questId;

    /**
     * 发布者id
     */
    @TableField("u_id")
    private Integer uId;

    /**
     * 问题标题
     */
    @TableField("question_title")
    private String questionTitle;

    /**
     * 内容
     */
    @TableField("problem_main")
    private String problemMain;

    /**
     * 发布时间
     */
    @TableField("problem_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime problemTime;

    /**
     * 是否为问题
     */
    @TableField("is_question")
    private Boolean isQuestion;

    /**
     * 图片
     */
    @TableField("photo")
    private String photo;

    @TableField(exist = false)
    private UserDto userDto;

    @TableField(exist = false)
    private UserDto_1 userDto_1;

    @TableField(exist = false)
    private int AnswerNum;
}
