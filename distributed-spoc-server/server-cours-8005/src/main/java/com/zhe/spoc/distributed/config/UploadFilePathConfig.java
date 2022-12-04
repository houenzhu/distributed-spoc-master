package com.zhe.spoc.distributed.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author HouEnZhu
 * @ClassName UploadFilePathConfig
 * @Description TODO
 * @date 2022/11/12 20:01
 * @Version 1.0
 */

@Configuration
public class UploadFilePathConfig implements WebMvcConfigurer {
    @Value("${files.upload.savePath}")
    private String savePath;

    @Value("${files.upload.path}")
    private String location;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //  /gbx/file/**为前端URL访问路径  后面 file:xxxx为本地磁盘映射
        registry.addResourceHandler(savePath)
                .addResourceLocations("file:///" + location);
    }
}
