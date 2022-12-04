package com.zhe.spoc.distributed;

import com.zhe.spoc.distributed.client.ServerUserClient;
import com.zhe.spoc.distributed.config.DefaultFeignConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author HouEnZhu
 * @ClassName ServerCommentApplication
 * @Description TODO
 * @date 2022/10/24 14:18
 * @Version 1.0
 */

@SpringBootApplication
@EnableFeignClients(clients = ServerUserClient.class, defaultConfiguration = DefaultFeignConfiguration.class)
public class ServerCommentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerCommentApplication.class, args);
    }
}
