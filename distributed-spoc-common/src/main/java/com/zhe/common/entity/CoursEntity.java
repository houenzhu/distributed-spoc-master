package com.zhe.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhe.common.dto.CoursTypeListDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author HouEnZhu
 * @ClassName CoursEntity
 * @Description 课程表
 * @date 2022/10/18 15:58
 * @Version 1.0
 */

@TableName("cours")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoursEntity implements Serializable {

    /**
     * 课程id
     */
    @TableField("cou_id")
    @TableId(type = IdType.ASSIGN_ID)
    private Long couId;

    /**
     * 课程名称
     */
    @TableField("cou_name")
    private String couName;

    /**
     * 课程介绍
     */
    @TableField("cou_introduction")
    private String couIntroduction;

    /**
     * 课程类别
     */
    @TableField("cou_type")
    private Integer couType;

    /**
     * 子类
     */
    @TableField("cou_chile_type")
    private Integer couChileType;

    /**
     * 课程价格
     */
    @TableField("cou_price")
    private Double couPrice;

    /**
     * 课程章节数
     */
    @TableField("cou_cata_num")
    private Integer couCataNum;

    /**
     *  课程收藏数
     */
    @TableField("cou_coll_num")
    private Integer couCollNum;

    /**
     * 课程推荐数
     */
    @TableField("cou_reco_num")
    private Integer couRecoNum;

    /**
     * 是否删除
     */
    @TableField("deleted")
    private Boolean deleted;

    /**
     * 课程作者
     */
    @TableField("tea_id")
    private Integer teaId;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 封面
     */
    @TableField("photo")
    private String photo;

    @TableField(exist = false)
    private UserEntity user;

    @TableField(exist = false)
    private CoursTypeEntity coursTypeEntity;

    @TableField(exist = false)
    private CouTypeSonEntity couTypeSonEntity;

    @TableField(exist = false)
    private List<CoursTypeListDTO> couTypeToList;

}
