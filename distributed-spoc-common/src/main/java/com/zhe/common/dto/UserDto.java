package com.zhe.common.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author HouEnZhu
 * @ClassName UserDto
 * @Description TODO
 * @date 2022/11/4 14:46
 * @Version 1.0
 */

@TableName("t_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto implements Serializable {

    @TableField("fullname")
    private String fullname;
}
