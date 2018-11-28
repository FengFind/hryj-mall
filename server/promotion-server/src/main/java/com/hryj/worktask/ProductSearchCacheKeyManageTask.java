package com.hryj.worktask;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.hryj.cache.RedisLock;
import com.hryj.cache.RedisService;
import com.hryj.constant.CommonConstantPool;
import com.hryj.service.util.RedisCacheUtil;
import com.hryj.utils.UtilMisc;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

/**
 * @author 王光银
 * @className: ProductSearchCacheKeyManageTask
 * @description: 商品搜索条件 KEY的缓存管理,  NUMBER_ONE 新增KEY， NUMBER_TWO 删除指定KEY， NUMBER_THREE 删除所有KEY
 * @create 2018/8/24 0024 16:43
 **/
@Slf4j
public class ProductSearchCacheKeyManageTask implements Runnable {

    private Long party_id;
    private String cache_key;
    private int type;

    public ProductSearchCacheKeyManageTask(Long party_id, String cache_key, int type) {
        if (party_id == null || party_id <= 0L) {
            throw new NullPointerException("party_id不能是空值");
        }

        if (CommonConstantPool.NUMBER_ONE != type && CommonConstantPool.NUMBER_TWO != type && CommonConstantPool.NUMBER_THREE != type) {
            throw new IllegalArgumentException("无法识别的操作类型: type=" + type);
        }

        if (CommonConstantPool.NUMBER_ONE == type || CommonConstantPool.NUMBER_TWO == type) {
            if (UtilValidate.isEmpty(cache_key)) {
                throw new NullPointerException("新增或删除商品搜索缓存键时: cache_key不能是空值");
            }
        }

        this.party_id = party_id;
        this.cache_key = cache_key;
        this.type = type;
    }

    @Override
    public void run() {
        RedisLock redisLock = RedisCacheUtil.redisCacheUtil.getRedisLock();
        RedisService redisService = RedisCacheUtil.redisCacheUtil.getRedisService();
        
        boolean lock_result = false;
        try {

            /**
             * 加锁，锁的范围在一个组织范围内
             */
            while (!(lock_result = redisLock.lock(RedisCacheUtil.RedisCacheKey.PROD_SEARCH_RESULT_CACHE_KEY.get(), party_id.toString(), 1))) {
                Thread.sleep(50);
            }

            //NUMBER_THREE 表示清除当前组织范围内的所有商品搜索条件缓存
            if (CommonConstantPool.NUMBER_THREE == type) {
                redisService.delete2(RedisCacheUtil.RedisCacheKey.PROD_SEARCH_RESULT_CACHE_KEY.get(), party_id.toString());
                return;
            }

            //从缓存中获取 当前组织下的所有商品搜索条件KEY
            String cache_data = redisService.get2(RedisCacheUtil.RedisCacheKey.PROD_SEARCH_RESULT_CACHE_KEY.get(), party_id.toString());

            //缓存数据为空并且操作类型为删除时，直接返回
            if (UtilValidate.isEmpty(cache_data) && type == CommonConstantPool.NUMBER_TWO) {
                return;
            }

            Set<String> cache_key_set;
            if (UtilValidate.isEmpty(cache_data)) {
                cache_key_set = new HashSet<>(1);
            } else {
                cache_key_set = UtilMisc.toSet(JSONArray.parseArray(cache_data).toJavaList(String.class));
            }

            if (CommonConstantPool.NUMBER_ONE == type) {
                cache_key_set.add(cache_key);
            } else {
                cache_key_set.remove(cache_key);
            }
            redisService.put2(RedisCacheUtil.RedisCacheKey.PROD_SEARCH_RESULT_CACHE_KEY.get(), party_id.toString(), JSON.toJSONString(cache_key_set), RedisCacheUtil.ProductSearchCacheUtil.getExpireLimit(null));
        } catch (Exception e) {
            log.error("商品搜索缓存处理失败:", e);
        } finally {
            if (lock_result) {
                redisLock.unLock(RedisCacheUtil.RedisCacheKey.PROD_SEARCH_RESULT_CACHE_KEY.get(), party_id.toString());
            }
        }
    }

}
