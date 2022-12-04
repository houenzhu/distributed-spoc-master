package com.zhe.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Time;
import java.time.LocalDateTime;

/**
 * @author HouEnZhu
 * @ClassName 学生课程进度表
 * @Description TODO
 * @date 2022/11/7 19:01
 * @Version 1.0
 */

@Data
@Accessors(chain = true)
public class StuCouProgress implements Serializable {

    /**
     * id
     */
    @TableField("id")
    @TableId(type = IdType.AUTO)
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
     * 章节id
     */
    @TableField("cha_id")
    private Long chaId;

    /**
     * 时长
     */
    @TableField("time")
    private String time;

    @TableField("update_time")
    private LocalDateTime updateTime;

}
