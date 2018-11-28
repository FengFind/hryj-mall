package com.hryj.cache.annotation;

import java.lang.annotation.*;

/**
 * @author 李道云
 * @className: CacheKey
 * @description: 缓存key注解
 * @create 2018/8/4 13:39
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
@Documented
public @interface CacheKey {
}
