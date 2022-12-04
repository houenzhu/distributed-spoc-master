package com.zhe.common.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author HouEnZhu
 * @ClassName CoursTypeEntity
 * @Description TODO
 * @date 2022/10/30 14:40
 * @Version 1.0
 */

@TableName("cours_type")
@Data
@Accessors(chain = true)
public class CoursTypeDTO implements Serializable {

    @TableField("cou_type")
    private String couType;

    @TableField("cou_parent_type")
    private Integer couParentType;

    /**
     * 课程父类图片
     */
    @TableField("photo")
    private String photo;
}
