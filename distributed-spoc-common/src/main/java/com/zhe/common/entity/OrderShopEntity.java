package com.zhe.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author HouEnZhu
 * @ClassName OrderShopEntity
 * @Description 订单表
 * @date 2022/10/29 10:22
 * @Version 1.0
 */

@TableName("order_shop")
@Data
@Accessors(chain = true)
public class OrderShopEntity implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @TableField("id")
    private Long id;

    /**
     * 学生id
     */
    @TableField("stu_id")
    private Integer stuId;

    /**
     * 购买时间
     */
    @TableField("buy_time")
    @JsonFormat(pattern="yyyy年MM月dd日 HH时mm分ss",timezone = "GMT+8")
    private LocalDateTime buyTime;

    /**
     * 花费
     */
    @TableField("cost")
    private Double cost;

    /**
     * 课程id
     */
    @TableField("cou_id")
    private Long couId;

    private CoursEntity coursEntity;
}
