package com.zhe.common.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author HouEnZhu
 * @ClassName AnswerEntity
 * @Description TODO
 * @date 2022/10/28 16:47
 * @Version 1.0
 */
@TableName("answer")
@Data
public class AnswerDTO implements Serializable {

    /**
     * 回复者id
     */
    @TableField("u_id")
    private Integer uId;

    /**
     * 回复内容
     */
    @TableField("answer_content")
    private String answerContent;


    /**
     * 回复时间
     */
    @TableField("create_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 是否采纳
     */
    @TableField("is_select")
    private Boolean isSelect;

    /**
     * 图片
     */
    @TableField("photo")
    private String photo;

    @TableField(exist = false)
    private UserDto userDto;
}
