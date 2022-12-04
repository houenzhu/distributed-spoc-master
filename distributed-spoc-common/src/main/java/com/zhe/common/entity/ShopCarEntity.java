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
import java.time.LocalDateTime;

/**
 * @author HouEnZhu
 * @ClassName 购物车表
 * @Description TODO
 * @date 2022/10/26 10:27
 * @Version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("stu_cou_cart")
public class ShopCarEntity implements Serializable {

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
     * 添加物品时间
     */
    @TableField("add_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime addTime;

    private CoursEntity coursEntity;
}
