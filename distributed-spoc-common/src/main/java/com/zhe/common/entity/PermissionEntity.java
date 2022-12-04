package com.zhe.common.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:权限表
 * @Author: zhurongsheng
 * @Date: 2020/7/11 16:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_permission")
public class PermissionEntity {

    /**
     * id
     */
    @TableId
    private Integer id;

    /**
     * 授权码
     */
    private String code;

    /**
     * 描述
     */
    private String description;

    /**
     * 可用url
     */
    private String url;

}
