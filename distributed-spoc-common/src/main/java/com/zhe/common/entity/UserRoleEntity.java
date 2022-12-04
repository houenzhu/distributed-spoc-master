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
 * @ClassName UserRoleEntity
 * @Description TODO
 * @date 2022/11/24 10:14
 * @Version 1.0
 */

@TableName("t_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserRoleEntity implements Serializable {

    @TableField("id")
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("userId")
    private Integer user_id;

    @TableField("role_id")
    private Integer roleId;

}
