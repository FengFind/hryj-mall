package com.hryj;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 王光银
 * @className: PromotionApplication
 * @description:
 * @create 2018-06-25 17:48
 **/
@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication
@EnableTransactionManagement(proxyTargetClass = true)
public class PromotionApplication {

    /**
     * @author 王光银
     * @description:
     * @param: [args]
     * @return void
     * @create 2018-06-25 17:48
     **/
    public static void main(String[] args) {
        new SpringApplicationBuilder(PromotionApplication.class).web(true).run(args);
    }
}
