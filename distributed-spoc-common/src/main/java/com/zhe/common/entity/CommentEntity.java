package com.zhe.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhe.common.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author HouEnZhu
 * @ClassName CommentEntity
 * @Description TODO
 * @date 2022/10/24 14:11
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("c_comment")
public class CommentEntity implements Serializable {

    /**
     * 评论id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @TableField("c_id")
    private Long ccId;

    /**
     * 评论内容
     */
    @TableField("c_content")
    private String commentContent;

    /**
     * 发布日期
     */
    @TableField("c_data")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime cData;

    /**
     * 资讯id
     */
    @TableField("info_id")
    private Long infoId;

    /**
     * 用户id
     */
    @TableField("u_id")
    private Integer uuId;

    /**
     * 角色
     */
    @TableField("role")
    private Boolean role;

    /**
     * 该评论点赞数
     */
    @TableField("c_like")
    private Boolean cLike;

    @TableField(exist = false)
    private UserDto userDto;
}
