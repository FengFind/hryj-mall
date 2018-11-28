package com.hryj.threadpool;

import lombok.Data;

/**
 * @author 王光银
 * @className: ThreadPoolConfig
 * @description:
 * @create 2018/8/24 0024 15:50
 **/
@Data
public class ThreadPoolConfig {

    /**
     * 核心线程池大小
     */
    private int corePoolSize;

    /**
     * 最大允许的线程时大小
     */
    private int maximumPoolSize;

    /**
     * 空闲线程允许存活的时间, 单位：秒
     */
    private long keepAliveTimeInSeconds;

    /**
     * 任务等待队列的大小
     */
    private int workQueueSize;

    public static ThreadPoolConfig getDefault() {
        ThreadPoolConfig config = new ThreadPoolConfig();
        config.setCorePoolSize(5);
        config.setMaximumPoolSize(25);
        config.setKeepAliveTimeInSeconds(60);
        config.setWorkQueueSize(500);
        return config;
    }
}
