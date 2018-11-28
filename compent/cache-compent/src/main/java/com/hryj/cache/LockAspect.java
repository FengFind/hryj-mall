package com.hryj.cache;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.hryj.cache.annotation.LockCache;
import com.hryj.cache.annotation.LockKey;
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
 * @className: LockAspect
 * @description: 分布式锁切面
 * @create 2018/8/6 10:44
 **/
@Slf4j
@Aspect
@Service
public class LockAspect {

    @Autowired
    private RedisLock redisLock;

    @Pointcut("@annotation(com.hryj.cache.annotation.LockCache)")
    public void lockCachePointcut(){
    }

    /**
     * 拦截器具体实现
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("lockCachePointcut()")
    public Object interceptor(ProceedingJoinPoint pjp) throws Throwable {
        log.info("分布式锁切面处理 >>>> start ");
        String target = pjp.getTarget().toString();//目标方法所在类
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod(); //被拦截的方法
        log.info("分布式锁切面处理,目标方法所在类：{}，目标方法名：{}，", target, method.getName());
        LockCache lockCache = method.getAnnotation(LockCache.class);
        int expireTime = lockCache.expireTime();
        int retryTimes = lockCache.retryTimes();
        long waitTime = lockCache.waitTime();
        String group_name = lockCache.cacheGroup().getValue();
        //分布式锁的key
        String key_name = null;
        int i = 0;
        //循环方法的所有参数
        for (Object value : pjp.getArgs()) {
            MethodParameter methodParam = new SynthesizingMethodParameter(method, i);
            Annotation[] paramAnns = methodParam.getParameterAnnotations();
            //循环参数上所有的注解
            for (Annotation paramAnn : paramAnns) {
                if (paramAnn instanceof LockKey && value !=null) {
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
        log.info("分布式锁切面处理,分布式锁key值 >>>> " + key_name);
        if(StrUtil.isBlank(key_name)){
            throw new BizException("分布式锁key值不存在");
        }
        //执行分布式锁的逻辑
        if (lockCache.isBlock()) {//阻塞锁
            int lockRetryTime = 0;
            try {
                while (!redisLock.lock(group_name, key_name, expireTime)) {
                    if (lockRetryTime++ > retryTimes) {
                        log.error("分布式锁切面处理,获取锁失败：key:{}, lockRetryTime:{}", key_name, lockRetryTime);
                        throw new BizException("系统太繁忙，请稍后再试");
                    }
                    ThreadUtil.safeSleep(waitTime);//线程等待
                }
                return pjp.proceed();
            } finally {
                redisLock.unLock(group_name,key_name);
            }
        } else {//非阻塞锁
            try {
                if (!redisLock.lock(group_name,key_name,expireTime)) {
                    throw new BizException("系统太繁忙，请稍后再试");
                }
                return pjp.proceed();
            } finally {
                redisLock.unLock(group_name,key_name);
            }
        }
    }
}
