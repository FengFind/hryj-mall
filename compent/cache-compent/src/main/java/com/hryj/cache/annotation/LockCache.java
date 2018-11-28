package com.hryj.cache.annotation;

import com.hryj.cache.CacheGroup;

import java.lang.annotation.*;

/**
 * @author 李道云
 * @className: LockCache
 * @description: 分布式锁注解
 * @create 2018/8/6 10:42
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface LockCache {

    /**
     * 锁的缓存分组
     * @return
     */
    CacheGroup cacheGroup();

    /**
     * 是否阻塞锁；
     * 1. true：获取不到锁，阻塞一定时间；
     * 2. false：获取不到锁，立即返回
     */
    boolean isBlock() default true;

    /**
     * 锁的超时时间(秒)
     */
    int expireTime() default 10;

    /**
     * 获取锁的等待时间(毫秒)
     */
    long waitTime() default 100;

    /**
     * 获取不到锁的重试次数
     */
    int retryTimes() default 20;

}
