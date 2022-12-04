package com.zhe.spoc.distributed.userserver.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author HouEnZhu
 * @ClassName DruidConfig
 * @Description TODO
 * @date 2022/10/28 10:05
 * @Version 1.0
 */

@Configuration
public class DruidConfig {
    /**
     * 只是将DataSource对象的实现类变为了DruidDataSource对象
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource druidDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        return druidDataSource;
    }

}
