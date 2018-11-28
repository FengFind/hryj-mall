package com.hryj;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

/**
 * @author 王光银
 * @className: ProductApplication
 * @description: 
 * @create 2018-06-25 17:46
 **/
@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication
@EnableTransactionManagement(proxyTargetClass = true)
public class ProductApplication {

    @Autowired
    private RestTemplateBuilder builder;

    // 使用RestTemplateBuilder来实例化RestTemplate对象，spring默认已经注入了RestTemplateBuilder实例
    @Bean
    public RestTemplate restTemplate() {
        return builder.build();
    }

    /**
     * @author 王光银
     * @description:
     * @param: [args]
     * @return void
     * @create 2018-06-25 17:44
     **/
    public static void main(String[] args) {
        new SpringApplicationBuilder(ProductApplication.class).web(true).run(args);
    }
}
