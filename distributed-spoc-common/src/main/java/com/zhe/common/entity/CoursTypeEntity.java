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
 * @ClassName CoursTypeEntity
 * @Description 课程类型中间表
 * @date 2022/10/30 14:40
 * @Version 1.0
 */

@TableName("cours_type")
@Data
@Accessors(chain = true)
public class CoursTypeEntity implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Integer id;

    /**
     * 课程类型
     */
    @TableField("cou_type")
    private String couType;

    /**
     * 课程父类
     */
    @TableField("cou_parent_type")
    private Integer couParentType;

    /**
     * 课程父类图片
     */
    @TableField("photo")
    private String photo;
}
