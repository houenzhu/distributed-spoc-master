package com.zhe.spoc.distributed.userserver;

import com.zhe.spoc.distributed.client.ServerClassClient;
import com.zhe.spoc.distributed.config.DefaultFeignConfiguration;
import com.zhe.spoc.distributed.config.FeignConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author HouEnZhu
 * @ClassName ServerUserApplication
 * @Description TODO
 * @date 2022/10/13 10:38
 * @Version 1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(clients = ServerClassClient.class, defaultConfiguration = {DefaultFeignConfiguration.class,
        FeignConfig.class})
public class ServerUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerUserApplication.class,args);
    }
}
