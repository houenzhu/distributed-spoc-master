package com.zhe.spoc.distributed;

import com.zhe.spoc.distributed.client.ServerCoursClient;
import com.zhe.spoc.distributed.client.ServerStuClient;
import com.zhe.spoc.distributed.client.ServerUserClient;
import com.zhe.spoc.distributed.config.DefaultFeignConfiguration;
import com.zhe.spoc.distributed.config.FeignConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author HouEnZhu
 * @ClassName ServerTeacherApplication
 * @Description TODO
 * @date 2022/10/14 19:13
 * @Version 1.0
 */

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(clients = {ServerUserClient.class, ServerCoursClient.class}, defaultConfiguration =
        {DefaultFeignConfiguration.class, FeignConfig.class})
public class ServerClassApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerClassApplication.class, args);
    }
}
