package com.zhe.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.Year;

/**
 * @author HouEnZhu
 * @ClassName SignEntity
 * @Description TODO
 * @date 2022/11/26 12:22
 * @Version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("tb_sign")
public class SignEntity implements Serializable {

    /**
     * id
     */
    @TableField("id")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 年份
     */
    @TableField("year")
    private Integer year;

    /**
     * 月份
     */
    @TableField("month")
    private Integer month;

    /**
     * 签到日期
     */
    @TableField("date")
    private LocalDateTime date;

    /**
     * 是否补签
     */
    @TableField("is_backup")
    private boolean isBackup;
}
