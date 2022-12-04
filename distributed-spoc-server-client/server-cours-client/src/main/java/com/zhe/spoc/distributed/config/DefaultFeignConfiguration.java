package com.zhe.spoc.distributed.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

public class DefaultFeignConfiguration {
    @Bean
    public Logger.Level level(){
        return Logger.Level.BASIC;
    }
}
