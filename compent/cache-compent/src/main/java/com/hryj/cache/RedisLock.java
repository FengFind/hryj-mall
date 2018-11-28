package com.hryj.cache;

import com.hryj.exception.ServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * @author 李道云
 * @className: RedisLock
 * @description: redis分布式锁
 * @create 2018/8/6 9:06
 **/
@Slf4j
@Component
public class RedisLock {

    @Autowired
    private RedisService redisService;

    /**
     * 加锁
     * @param group_name
     * @param key_name
     * @param expireTime
     * @return
     */
    public boolean lock(String group_name, String key_name, Integer expireTime) {
        long now = Instant.now().toEpochMilli();
        long lockExpireTime = now + expireTime * 1000;
        Long status = redisService.setnx1(group_name,key_name,String.valueOf(lockExpireTime));
        if(status ==1){
            //获取锁成功，更新锁过期时间
            redisService.expire1(group_name,key_name,expireTime);
            return true;
        }else{
            Object value = this.getKeyWithRetry(group_name, key_name, 3);
            // 避免获取锁失败,同时对方释放锁后,造成NPE
            if (value != null) {
                //已存在的锁超时时间
                long oldExpireTime = Long.parseLong((String)value);
                //锁过期时间小于当前时间,锁已经超时,重新取锁，防止死锁问题
                if (oldExpireTime <= now) {
                    String value2 = redisService.put1(group_name,key_name,String.valueOf(lockExpireTime),expireTime);
                    long currentExpireTime = Long.parseLong(value2);
                    //判断currentExpireTime与oldExpireTime是否相等
                    if(currentExpireTime == oldExpireTime){
                        //相等,则取锁成功
                        redisService.expire1(group_name,key_name,expireTime);
                        return true;
                    }else{
                        //不相等,取锁失败
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 多次尝试获取锁，防止redis连接突然中断获取数据失败
     * @param group_name
     * @param key_name
     * @param retryTimes
     * @return
     */
    private Object getKeyWithRetry(String group_name, String key_name, int retryTimes) {
        int failTimes = 0;
        while (failTimes < retryTimes) {
            try {
                return redisService.get1(group_name,key_name);
            } catch (Exception e) {
                failTimes ++;
                if (failTimes >= retryTimes) {
                    throw new ServerException("redis连接异常，获取不到数据");
                }
            }
        }
        return null;
    }

    /**
     * 解锁
     * @param group_name
     * @param key_name
     * @return
     */
    public boolean unLock(String group_name, String key_name) {
        redisService.delete1(group_name,key_name);
        return true;
    }
}
