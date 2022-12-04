package com.zhe.spoc.distributed.uaa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author HouEnZhu
 * @ClassName UaaApplication
 * @Description TODO
 * @date 2022/10/13 9:58
 * @Version 1.0
 */

@SpringBootApplication
@EnableDiscoveryClient
public class UaaApplication {
    public static void main(String[] args) {
        SpringApplication.run(UaaApplication.class, args);
    }
}
