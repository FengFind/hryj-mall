package com.hryj;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 李道云
 * @className: ProfitApplication
 * @description: 分润服务启动入口
 * @create 2018-06-12 16:30
 **/
@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication
@EnableTransactionManagement(proxyTargetClass = true)
public class ProfitApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ProfitApplication.class).web(true).run(args);
    }
}
