package com.zhe.common.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author HouEnZhu
 * @ClassName CoursTypeListDTO
 * @Description TODO
 * @date 2022/11/10 12:19
 * @Version 1.0
 */
@TableName("cours")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoursTypeListDTO implements Serializable {
    @TableField("cou_type")
    private Integer couType; // 课程类别

    @TableField("cou_chile_type")
    private Integer couChileType; // 子类

}
