package com.hryj;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 李道云
 * @className: StaffApplication
 * @description: 员工服务启动入口
 * @create 2018-06-26 8:48
 **/
@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication
@EnableTransactionManagement(proxyTargetClass = true)
public class StaffApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(StaffApplication.class).web(true).run(args);
    }
}
