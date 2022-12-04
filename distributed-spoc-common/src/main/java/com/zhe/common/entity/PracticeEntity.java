package com.zhe.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author HouEnZhu
 * @ClassName PracticeEntity
 * @Description TODO
 * @date 2022/10/23 16:30
 * @Version 1.0
 */

@TableName("practice")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PracticeEntity implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @TableField("pra_id")
    private Long praId;

    /**
     * 标题
     */
    @TableField("pra_name")
    private String praName;

    /**
     * 实践开始时间
     */
    @TableField("pra_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime praTime;

    /**
     * 实践结束时间
     */
    @TableField("end_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime endTime;

    /**
     * 实践地点
     */
    @TableField("pra_site")
    private String praSite;

    /**
     * 实践内容
     */
    @TableField("pra_main")
    private String praMain;


    /**
     * 教师id
     */
    @TableField("tea_id")
    private Integer teaId;

    /**
     * 是否删除
     */
    @TableField("is_deleted")
    private Boolean isDeleted;

    /**
     * 是否开始
     */
    @TableField("is_begin")
    private Boolean isBegin;

    /**
     * 课程id
     */
    @TableField("cou_id")
    private Long couId;

    /**
     * 图片
     */
    @TableField("photo")
    private String photo;

    @TableField(exist = false)
    private CoursEntity coursEntity;

    @TableField(exist = false)
    private UserEntity user;
}
