package com.zhe.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author HouEnZhu
 * @ClassName CommentLikeEntity
 * @Description TODO
 * @date 2022/11/27 22:56
 * @Version 1.0
 */

@TableName("comment_like")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class CommentLikeEntity implements Serializable {

    /**
     * id
     */
    @TableField("id")
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 评论id
     */
    @TableField("comment_id")
    private Long commentId;

    /**
     * 是否点赞
     */
    @TableField("is_like")
    private Boolean isLike;
}
