package com.zhe.common.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhe.common.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author HouEnZhu
 * @ClassName InformationDTO
 * @Description TODO
 * @date 2022/11/17 15:47
 * @Version 1.0
 */

@TableName("information")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class InformationDTO implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    @TableField("info_id")
    private Long infoId;

    @TableField("info_title")
    private String infoTitle;

    @TableField("info_main")
    private String infoMain;

    @TableField("info_author")
    private String infoAuthor;

    @TableField("info_data")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime infoData;

    @TableField("info_like")
    private int infoLike;

    @TableField("tea_id")
    private Integer teaId;

    @TableField(exist = false)
    private UserDto_1 userDto_1;
}
