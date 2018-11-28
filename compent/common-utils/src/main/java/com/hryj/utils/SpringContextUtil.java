package com.hryj.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author 李道云
 * @className: SpringContextUtil
 * @description: Spring容器上下文
 * @create 2018/8/27 12:19
 **/
@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext context = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static Object getBean(String name) throws BeansException {
        return context.getBean(name);
    }

    public static <T> T getBean(Class<T> requiredType){
        return context.getBean(requiredType);
    }

    public static <T> T getBean(String beanName, Class<T> requiredType){
        return context.getBean(beanName,requiredType);
    }
}
