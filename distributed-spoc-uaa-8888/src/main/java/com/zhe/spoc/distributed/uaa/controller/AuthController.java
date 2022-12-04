package com.zhe.spoc.distributed.uaa.controller;

import com.zhe.common.api.CommonResult;
import com.zhe.common.api.ResultCode;
import com.zhe.spoc.distributed.uaa.dto.Oauth2TokenDto;
import com.zhe.spoc.distributed.uaa.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

/**
 * @author HouEnZhu
 * @ClassName AuthController
 * @Description TODO
 * @date 2022/10/18 19:08
 * @Version 1.0
 */
@RestController
@RequestMapping("/oauth")
public class AuthController {
    @Autowired
    private TokenEndpoint tokenEndpoint;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ConsumerTokenServices consumerTokenServices;
    /**
     * Oauth2登录认证
     */
    @PostMapping("/token")
    public CommonResult<Oauth2TokenDto> postAccessToken(Principal principal, @RequestParam Map<String, String> parameters)
            throws HttpRequestMethodNotSupportedException {
        OAuth2AccessToken oAuth2AccessToken = tokenEndpoint.postAccessToken(principal, parameters).getBody();
        Oauth2TokenDto oauth2TokenDto = Oauth2TokenDto.builder()
                .access_token(oAuth2AccessToken.getValue())
                .refreshToken(oAuth2AccessToken.getRefreshToken().getValue())
                .expiresIn(oAuth2AccessToken.getExpiresIn())
                .tokenHead("Bearer ").build();
        redisUtil.set("access_token", oAuth2AccessToken.getValue());
        return CommonResult.success(oauth2TokenDto);
    }

}
