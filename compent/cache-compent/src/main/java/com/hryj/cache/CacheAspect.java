package com.hryj.cache;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.hryj.cache.annotation.CacheKey;
import com.hryj.cache.annotation.QueryCache;
import com.hryj.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author 李道云
 * @className: CacheAspect
 * @description: 缓存切面
 * @create 2018/8/4 13:59
 **/
@Slf4j
@Aspect
@Service
public class CacheAspect {

    @Autowired
    private RedisService redisService;

    @Pointcut("@annotation(com.hryj.cache.annotation.QueryCache)")
    public void queryCachePointcut(){
    }

    /**
     * 拦截器具体实现
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("queryCachePointcut()")
    public Object interceptor(ProceedingJoinPoint pjp) throws Throwable {
        long beginTime = System.currentTimeMillis();
        log.info("AOP缓存,切面处理 >>>> start ");
        String target = pjp.getTarget().toString();//目标方法所在类
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod(); //被拦截的方法
        Class returnType = signature.getReturnType();//返回值类型
        log.info("AOP缓存,目标方法所在类：{}，目标方法名：{}，返回值类型：{}", target, method.getName(), returnType);
        CacheGroup cacheGroup = method.getAnnotation(QueryCache.class).cacheGroup();
        log.info("AOP缓存,缓存分组名：" + cacheGroup.getValue());
        Integer expireTime = cacheGroup.getExpireTime();
        String key_name = null;
        int i = 0;
        //循环方法的所有参数
        for (Object value : pjp.getArgs()) {
            MethodParameter methodParam = new SynthesizingMethodParameter(method, i);
            Annotation[] paramAnns = methodParam.getParameterAnnotations();
            //循环参数上所有的注解
            for (Annotation paramAnn : paramAnns) {
                if (paramAnn instanceof CacheKey && value !=null) {
                    String value_str = value.toString();
                    log.info("分布式锁切面处理,value_str===" + value_str);
                    if(key_name ==null){
                        key_name = value_str;
                    }else{
                        key_name = key_name + "_" + value_str;
                    }
                }
            }
            i++;
        }
        //获取不到key值，抛异常
        log.info("AOP缓存,缓存key值 >>>> " + key_name);
        if(StrUtil.isBlank(key_name)){
            throw new BizException("缓存key值不存在");
        }
        String result = redisService.get2(cacheGroup.getValue(),key_name);
        log.info("AOP缓存,redis缓存获取到数据result===" + result);
        //缓存中没有数据，执行方法查询数据库
        if(StrUtil.isEmpty(result)){
            Object object = pjp.proceed();
            result = JSON.toJSONString(object);
            redisService.put2(cacheGroup.getValue(),key_name,result,expireTime);
            log.info("AOP缓存,DB取到数据并存入缓存result===" + result);
        }else{
            return JSON.parseObject(result,returnType);
        }
        log.info("AOP缓存,切面处理 >>>> end 耗时：" + (System.currentTimeMillis() - beginTime));
        return JSON.parseObject(result,returnType);
    }
}
