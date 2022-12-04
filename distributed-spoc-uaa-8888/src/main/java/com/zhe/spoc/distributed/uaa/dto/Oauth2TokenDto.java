package com.zhe.spoc.distributed.uaa.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author HouEnZhu
 * @ClassName Oauth2TokenDto
 * @Description TODO
 * @date 2022/10/18 19:07
 * Oauth2获取Token返回信息封装
 * @Version 1.0
 */

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class Oauth2TokenDto {
    /**
     * 访问令牌
     */
    private String access_token;
    /**
     * 刷新令牌
     */
    private String refreshToken;
    /**
     * 访问令牌头前缀
     */
    private String tokenHead;
    /**
     * 有效时间（秒）
     */
    private int expiresIn;
}
