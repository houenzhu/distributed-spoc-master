package com.zhe.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhe.common.dto.CourseDTO;
import com.zhe.common.dto.UserDto_1;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author HouEnZhu
 * @ClassName 学生表
 * @Description TODO
 * @date 2022/10/13 12:30
 * @Version 1.0
 */

@TableName("student")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentEntity implements Serializable {

    /**
     * 学生id
     */
    @TableField("stu_id")
    private Integer stuId;

    /**
     * 学生姓名
     */
    @TableField("stu_name")
    private String stuName;

    /**
     * 学生学习时长
     */
    @TableField("stu_study_time")
    private Integer stuStudyTime;

    /**
     * 学生积分
     */
    @TableField("stu_score")
    private Double stuScore;

    /**
     * 用户实体类
     */
    @TableField(exist = false)
    private UserEntity user;

    /**
     * 百分比
     */
    @TableField(exist = false)
    private double stuPercent;

    @TableField(exist = false)
    private UserDto_1 userDto_1;

    @TableField(exist = false)
    private List<CourseDTO> courseDTO;

}
