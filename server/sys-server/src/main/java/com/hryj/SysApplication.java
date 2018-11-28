package com.hryj;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 李道云
 * @className: SysApplication
 * @description: 系统服务启动入口
 * @create 2018-06-12 16:30
 **/
@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication
@EnableTransactionManagement(proxyTargetClass = true)
public class SysApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SysApplication.class).web(true).run(args);
    }
}
