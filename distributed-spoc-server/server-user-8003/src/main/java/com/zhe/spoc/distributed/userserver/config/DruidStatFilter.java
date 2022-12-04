package com.zhe.spoc.distributed.userserver.config;

/**
 * @author HouEnZhu
 * @ClassName DruidStatFilter
 * @Description TODO
 * @date 2022/10/28 10:05
 * @Version 1.0
 */


import com.alibaba.druid.support.http.WebStatFilter;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

/**
 * 配置druid过滤规则
 */

@WebFilter(filterName = "druidWebStatFilter", urlPatterns = "/*",
        initParams = {
                @WebInitParam(name = "exclusions", value = "*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*")// 忽略资源
        })
public class DruidStatFilter extends WebStatFilter {
}
