package com.zhe.spoc.distributed.uaa.security.handler;

import com.zhe.common.api.CommonResult;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author HouEnZhu
 * @ClassName Oauth2ExceptionHandler
 * @Description TODO
 * @date 2022/10/18 19:13
 * @Version 1.0
 */

@ControllerAdvice
public class Oauth2ExceptionHandler {
    @ResponseBody
    @ExceptionHandler(value = OAuth2Exception.class)
    public CommonResult handleOauth2(OAuth2Exception e) {
        return CommonResult.failed(e.getMessage());
    }

}
