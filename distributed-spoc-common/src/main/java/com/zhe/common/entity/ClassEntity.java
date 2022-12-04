package com.zhe.common.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author HouEnZhu
 * @ClassName ClassEntity
 * @Description TODO
 * @date 2022/10/14 19:08
 * @Version 1.0
 */
@TableName("class")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassEntity implements Serializable {

    /**
     * 班级id(雪花id)
     */
    @TableField("class_id")
    @TableId(type = IdType.ASSIGN_ID)
    private Long classId;

    /**
     * 班级名称
     */
    @TableField("class_name")
    private String className;

    /**
     * 教师id
     */
    @TableField("tea_id")
    private Integer teaId;

    /**
     * 班级人数
     */
    @TableField("class_student_num")
    private int classStudentNum;

    /**
     * 是否删除
     */
    @TableField("deleted")
    private boolean deleted;

    /**
     *创建时间
     */
    @TableField("create_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createTime;


    private UserEntity user;

    private List<UserEntity> userEntityList;

    private List<CoursEntity> coursEntities;
}
