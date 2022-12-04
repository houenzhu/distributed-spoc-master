package com.zhe.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author HouEnZhu
 * @ClassName LearningProgressEntity
 * @Description 学习进度表
 * @date 2022/10/29 14:45
 * @Version 1.0
 */

@Data
@TableName("learning_progress")
@Accessors(chain = true)
public class LearningProgressEntity implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Integer id;

    /**
     * 学生id
     */
    @TableField("stu_id")
    private Integer stuId;

    /**
     * 课程id
     */
    @TableField("cou_id")
    private Long couId;

    /**
     * 班级id
     */
    @TableField("class_id")
    private Long classId;

    /**
     * 章节id
     */
    @TableField("cha_id")
    private Long chaId;

    /**
     * 时长
     */
    @TableField("time")
    private int time;

    /**
     * 更新日期
     */
    @TableField("update_time")
    @JsonFormat(pattern="yyyy年MM月dd日 HH时mm分ss",timezone = "GMT+8")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private StudentEntity studentEntity;
}
