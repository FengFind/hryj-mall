package com.hryj.threadpool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author 王光银
 * @className: ThreadPoolUtil
 * @description:
 * @create 2018/8/24 0024 15:46
 **/
public class ThreadPoolUtil {

    private static ThreadPoolExecutor THREAD_POOL_EXECUTOR;

    private static final ReentrantLock LOCK = new ReentrantLock();

    private static void initThreadPool() {
        if (THREAD_POOL_EXECUTOR != null) {
            return;
        }
        LOCK.lock();
        if (THREAD_POOL_EXECUTOR == null) {
            ThreadPoolConfig config = ThreadPoolConfig.getDefault();
            THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(config.getCorePoolSize(),
                    config.getMaximumPoolSize(),
                    config.getKeepAliveTimeInSeconds(),
                    TimeUnit.SECONDS, new LinkedBlockingQueue<>(config.getWorkQueueSize()));
        }
        LOCK.unlock();
    }

    public static void submitTask(Runnable task) {
        try {
            THREAD_POOL_EXECUTOR.submit(task);
        } catch (NullPointerException e) {
            initThreadPool();
            THREAD_POOL_EXECUTOR.submit(task);
        } catch (Exception e) {
            while (true) {
                try {
                    THREAD_POOL_EXECUTOR.submit(task);
                    break;
                } catch (Exception e1) {
                    try {
                        Thread.sleep(20L);
                    } catch (Exception e2) {}
                }
            }
        }
    }

}
