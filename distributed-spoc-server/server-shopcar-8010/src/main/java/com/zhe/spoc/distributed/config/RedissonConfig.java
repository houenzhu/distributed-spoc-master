package com.zhe.spoc.distributed.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author HouEnZhu
 * @ClassName RedissonConfig
 * @Description TODO
 * @date 2022/11/9 10:49
 * @Version 1.0
 */

@Configuration
public class RedissonConfig {
    @Bean
    public RedissonClient redissonClient() {
        // 配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        // 创建redisson对象
        return Redisson.create(config);
    }
}
