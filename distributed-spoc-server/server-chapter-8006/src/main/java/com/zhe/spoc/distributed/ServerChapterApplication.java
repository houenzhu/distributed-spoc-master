package com.zhe.spoc.distributed;

import com.zhe.spoc.distributed.client.ServerCoursClient;
import com.zhe.spoc.distributed.client.ServerStuClient;
import com.zhe.spoc.distributed.config.DefaultFeignConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author HouEnZhu
 * @ClassName ServerChapterApplication
 * @Description TODO
 * @date 2022/10/22 15:21
 * @Version 1.0
 */

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(clients = {ServerCoursClient.class, ServerStuClient.class}, defaultConfiguration = DefaultFeignConfiguration.class)
@EnableAspectJAutoProxy(exposeProxy = true)
public class ServerChapterApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerChapterApplication.class, args);
    }
}
