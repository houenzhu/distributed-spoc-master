package com.zhe.common.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhe.common.entity.CommentLikeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author HouEnZhu
 * @ClassName CommentDTO
 * @Description TODO
 * @date 2022/11/17 16:27
 * @Version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("c_comment")
public class CommentDTO implements Serializable {
    /**
     * 评论id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @TableField("c_id")
    private Long cId;

    /**
     * 评论内容
     */
    @TableField("c_content")
    private String cContent;

    /**
     * 发布日期
     */
    @TableField("c_data")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime cData;


    /**
     * 用户id
     */
    @TableField("u_id")
    private Integer uId;

    /**
     * 该评论点赞数
     */
    @TableField("c_like")
    private int cLike;


    @TableField(exist = false)
    private UserDto userDto;

    @TableField(exist = false)
    private Boolean isLike;
}
