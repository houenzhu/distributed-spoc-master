package com.zhe.spoc.distributed;

import com.zhe.spoc.distributed.client.ServerUserClient;
import com.zhe.spoc.distributed.config.DefaultFeignConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author HouEnZhu
 * @ClassName ServerCoursApplication
 * @Description TODO
 * @date 2022/10/18 16:24
 * @Version 1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(clients = ServerUserClient.class, defaultConfiguration = DefaultFeignConfiguration.class)
public class ServerCoursApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerCoursApplication.class, args);
    }
}
