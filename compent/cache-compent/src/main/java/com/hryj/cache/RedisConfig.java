package com.hryj.cache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


/**
 * @author 李道云
 * @className: RedisConfig
 * @description: redis配置类
 * @create 2018/6/22 21:21
 **/
@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.timeout}")
    private int timeout;

    @Bean
    public JedisPoolConfig getRedisConfig(){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(500);//资源池中最大连接数
        config.setMaxIdle(500);//资源池允许最大空闲的连接数，连接池的最佳性能是maxTotal = maxIdle
        config.setMinIdle(50);//资源池确保最少空闲的连接数
        config.setTestWhileIdle(true);//是否开启空闲资源监测
        return config;
    }

    @Bean(name = "jedisPool")
    public JedisPool getJedisPool(){
        JedisPoolConfig config = getRedisConfig();
        JedisPool pool = new JedisPool(config,host,port,timeout,password,0);
        return pool;
    }

    @Bean(name = "jedisPool1")
    public JedisPool getJedisPool1(){
        JedisPoolConfig config = getRedisConfig();
        JedisPool pool = new JedisPool(config,host,port,timeout,password,1);
        return pool;
    }


    @Bean(name = "jedisPool2")
    public JedisPool getJedisPool2(){
        JedisPoolConfig config = getRedisConfig();
        JedisPool pool = new JedisPool(config,host,port,timeout,password,2);
        return pool;
    }
}
