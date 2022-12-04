package com.zhe.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author HouEnZhu
 * @ClassName 角色表
 * @Description TODO
 * @date 2022/10/12 20:36
 * @Version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_role")
public class RoleEntity implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Integer id;

    /**
     * 角色名称
     */
    @TableField("roleName")
    private String roleName;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 状态
     */
    @TableField("status")
    private Integer status;
}
