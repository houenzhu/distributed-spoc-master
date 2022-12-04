package com.zhe.spoc.distributed.filter;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author HouEnZhu
 * @ClassName FeignOauth2RequestInterceptor
 * @Description TODO
 * @date 2022/10/15 9:44
 * @Version 1.0
 */

@Component
public class FeignOauth2RequestInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TOKEN_TYPE = "Bearer";

    @Override
    public void apply(RequestTemplate requestTemplate) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
       if (requestAttributes != null){
           HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
           String authorization = request.getHeader(AUTHORIZATION_HEADER);
           requestTemplate.header(AUTHORIZATION_HEADER, authorization);
       }
    }
}
