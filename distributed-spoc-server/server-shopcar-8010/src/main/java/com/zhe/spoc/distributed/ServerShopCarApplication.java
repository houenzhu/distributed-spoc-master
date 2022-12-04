package com.zhe.spoc.distributed;

import com.zhe.spoc.distributed.client.ServerCoursClient;
import com.zhe.spoc.distributed.client.ServerStuClient;
import com.zhe.spoc.distributed.client.ServerUserClient;
import com.zhe.spoc.distributed.config.DefaultFeignConfiguration;
import com.zhe.spoc.distributed.config.FeignConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author HouEnZhu
 * @ClassName ShopCarApplication
 * @Description TODO
 * @date 2022/10/26 10:23
 * @Version 1.0
 */

@SpringBootApplication
@EnableFeignClients(clients = {ServerStuClient.class, ServerCoursClient.class},
        defaultConfiguration = {DefaultFeignConfiguration.class, FeignConfig.class})
@EnableAspectJAutoProxy(exposeProxy = true)
public class ServerShopCarApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerShopCarApplication.class, args);
    }
}
