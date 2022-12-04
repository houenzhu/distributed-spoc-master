package com.zhe.common.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhe.common.entity.StudentEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author HouEnZhu
 * @ClassName CourseDTO
 * @Description TODO
 * @date 2022/11/14 23:01
 * @Version 1.0
 */
@TableName("cours")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDTO implements Serializable {
    @TableField("cou_id")
    @TableId(type = IdType.ASSIGN_ID)
    private Long couId; //课程id

    @TableField("cou_name")
    private String couName; //课程名称

    @TableField("tea_id")
    private Integer teaId; // 教师id

    @TableField(exist = false)
    private UserDto userDto;

    @TableField(exist = false)
    private double StuProgress; // 学生进度返回

    @TableField(exist = false)
    private StudentEntity studentEntity;
}
