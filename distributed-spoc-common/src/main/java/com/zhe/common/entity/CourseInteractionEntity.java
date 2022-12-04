package com.zhe.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author HouEnZhu
 * @ClassName CourseInteractionEntity
 * @Description 课程点赞互动表
 * @date 2022/10/26 15:51
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("cou_class")
public class CourseInteractionEntity implements Serializable {

    /**
     * id
     */
    @TableField("id")
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 课程id
     */
    @TableField("cou_id")
    private Long couId;

    /**
     * 学生id
     */
    @TableField("stu_id")
    private Integer stuId;

    /**
     * 是否收藏
     */
    @TableField("collect")
    private Boolean Collect;

    /**
     * 是否拥有
     */
    @TableField("have")
    private Boolean Have;

    /**
     * 是否点赞
     */
    @TableField("recommend")
    private Boolean Recommend;
}
