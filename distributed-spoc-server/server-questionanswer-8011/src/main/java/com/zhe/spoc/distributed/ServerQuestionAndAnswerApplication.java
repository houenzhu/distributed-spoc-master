package com.zhe.spoc.distributed;

import com.zhe.spoc.distributed.client.ServerStuClient;
import com.zhe.spoc.distributed.client.ServerUserClient;
import com.zhe.spoc.distributed.config.DefaultFeignConfiguration;
import com.zhe.spoc.distributed.config.FeignConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author HouEnZhu
 * @ClassName ServerQuestionAndAnswerApplication
 * @Description TODO
 * @date 2022/10/28 15:57
 * @Version 1.0
 */

@SpringBootApplication
@EnableFeignClients(clients = {ServerUserClient.class},
        defaultConfiguration = {DefaultFeignConfiguration.class, FeignConfig.class})
public class ServerQuestionAndAnswerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerQuestionAndAnswerApplication.class, args);
    }
}
