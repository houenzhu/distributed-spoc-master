package com.zhe.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author HouEnZhu
 * @ClassName CouTypeSon
 * @Description 课程子类
 * @date 2022/11/8 10:18
 * @Version 1.0
 */
@TableName("cours_type")
@Data
@Accessors(chain = true)
public class CouTypeSonEntity implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Integer id;

    /**
     * 课程父类
     */
    @TableField("cou_parent_type")
    private Integer couParentType;

    /**
     * 课程子类
     */
    @TableField("cou_son_type")
    private String couSonType;
}
