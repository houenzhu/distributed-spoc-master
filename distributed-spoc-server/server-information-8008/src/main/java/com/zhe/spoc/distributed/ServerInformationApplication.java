package com.zhe.spoc.distributed;

import com.zhe.spoc.distributed.client.ServerUserClient;
import com.zhe.spoc.distributed.config.DefaultFeignConfiguration;
import com.zhe.spoc.distributed.config.FeignConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author HouEnZhu
 * @ClassName ServerInformationApplication
 * @Description TODO
 * @date 2022/10/24 10:55
 * @Version 1.0
 */

@SpringBootApplication
@EnableFeignClients(clients = ServerUserClient.class, defaultConfiguration = {DefaultFeignConfiguration.class, FeignConfig.class})
public class ServerInformationApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerInformationApplication.class, args);
    }
}
