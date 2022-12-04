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
import java.sql.Time;
import java.time.LocalDateTime;

/**
 * @author HouEnZhu
 * @ClassName CouClass
 * @Description 课程与教室的中间表
 * @date 2022/10/19 16:32
 * @Version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("cou_class")
public class CouClassEntity implements Serializable {

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
