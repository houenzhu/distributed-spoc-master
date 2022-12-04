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

/**
 * @author HouEnZhu
 * @ClassName UserDto_1
 * @Description TODO
 * @date 2022/11/19 11:10
 * @Version 1.0
 */
@TableName("t_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto_1 implements Serializable {

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 真实姓名
     */
    @TableField("fullname")
    private String fullname;

    /**
     * 电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 头像
     */
    @TableField("photo")
    private String photo;

    /**
     * 学生积分
     */
    @TableField("stu_score")
    private Double stuScore;
}
