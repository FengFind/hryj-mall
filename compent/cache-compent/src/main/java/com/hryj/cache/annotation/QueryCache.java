package com.hryj.cache.annotation;

import com.hryj.cache.CacheGroup;

import java.lang.annotation.*;

/**
 * @author 李道云
 * @className: QueryCache
 * @description: 查询缓存注解
 * @create 2018/8/4 13:07
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface QueryCache {

    CacheGroup cacheGroup();
}
