package com.zhe.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author HouEnZhu
 * @ClassName CourseChapterAppraiseEntity
 * @Description 课程章节互动表
 * @date 2022/10/28 10:46
 * @Version 1.0
 */

@TableName("course_chapter_appraise")
@Data
public class CourseChapterAppraiseEntity implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Integer id;

    /**
     * 课程id
     */
    @TableField("cou_id")
    private Long couId;

    /**
     * 章节id
     */
    @TableField("cha_id")
    private Long chaId;

    /**
     * 学生id
     */
    @TableField("stu_id")
    private Integer stuId;

    /**
     * 评论内容
     */
    @TableField("cha_appraise")
    private String chaAppraise;
}
