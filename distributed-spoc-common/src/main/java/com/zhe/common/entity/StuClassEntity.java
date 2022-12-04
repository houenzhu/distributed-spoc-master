package com.zhe.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDateTime;

/**
 * @author HouEnZhu
 * @ClassName 学生班级中间表
 * @Description TODO
 * @date 2022/10/17 12:36
 * @Version 1.0
 */

@TableName("stu_class")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StuClassEntity implements Serializable {

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
     * 班级id
     */
    @TableField("class_id")
    private Long classId;

    /**
     * 加入时间
     */
    @TableField("join_date")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime joinDate;
}
