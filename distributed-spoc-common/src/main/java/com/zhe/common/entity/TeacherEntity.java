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

/**
 * @author HouEnZhu
 * @ClassName TeacherEntity
 * @Description TODO
 * @date 2022/11/22 12:45
 * @Version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("teacher")
public class TeacherEntity implements Serializable {

    /**
     * 编号
     */
    @TableField("id")
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 教师id
     */
    @TableField("tea_id")
    private Integer teaId;

    /**
     * 教师名称
     */
    @TableField("tea_name")
    private String teaName;
}
