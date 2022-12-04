package com.zhe.spoc.distributed.uaa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

/**
 * @author HouEnZhu
 * @ClassName RevokeTokenEndpoint
 * @Description TODO
 * @date 2022/10/20 22:15
 * @Version 1.0
 */

@FrameworkEndpoint
@RequestMapping("/oauth")
public class RevokeTokenEndpoint {
    @Autowired
    @Qualifier("consumerTokenServices")
    ConsumerTokenServices consumerTokenServices;

    @Autowired
    private TokenEndpoint tokenEndpoint;

    @DeleteMapping("/logout")
    @ResponseBody
    public String revokeToken(Principal principal, @RequestParam Map<String, String> parameters)
            throws HttpRequestMethodNotSupportedException {
        OAuth2AccessToken oAuth2AccessToken = tokenEndpoint.postAccessToken(principal, parameters).getBody();
        System.out.println(oAuth2AccessToken.getValue());
        if (consumerTokenServices.revokeToken(oAuth2AccessToken.getValue())) {
            return "注销成功";
        } else {
            return "注销失败";
        }

    }
}
