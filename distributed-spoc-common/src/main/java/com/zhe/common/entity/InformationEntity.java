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
import java.time.LocalDateTime;

/**
 * @author HouEnZhu
 * @ClassName InformationEntity
 * @Description 资讯表
 * @date 2022/10/24 10:59
 * @Version 1.0
 */

@TableName("information")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class InformationEntity implements Serializable {

    /**
     * 资讯id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @TableField("info_id")
    private Long infoId;

    /**
     * 资讯标题
     */
    @TableField("info_title")
    private String infoTitle;

    /**
     * 资讯内容
     */
    @TableField("info_main")
    private String infoMain;

    /**
     * 资讯作者
     */
    @TableField("info_author")
    private String infoAuthor;

    /**
     * 发布日期
     */
    @TableField("info_data")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime infoData;

    /**
     * 点赞数
     */
    @TableField("info_like")
    private int infoLike;

    /**
     * 教师id
     */
    @TableField("tea_id")
    private Integer teaId;

    /**
     * 图片
     */
    @TableField("photo")
    private String photo;

    /**
     * 全部点赞
     */
    @TableField(exist = false)
    private int likes;
}
