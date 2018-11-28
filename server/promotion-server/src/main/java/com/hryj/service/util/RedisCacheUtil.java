package com.hryj.service.util;

import com.alibaba.fastjson.JSON;
import com.hryj.cache.CodeCache;
import com.hryj.cache.RedisLock;
import com.hryj.cache.RedisService;
import com.hryj.constant.CommonConstantPool;
import com.hryj.entity.vo.product.response.app.AppProdListItemResponseVO;
import com.hryj.threadpool.ThreadPoolUtil;
import com.hryj.utils.UtilValidate;
import com.hryj.worktask.ProductSearchCacheKeyManageTask;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author 王光银
 * @className: RedisCacheUtil
 * @description:
 * @create 2018/8/16 0016 14:59
 **/

@Slf4j
@Component
@Data
public class RedisCacheUtil {

    public static RedisCacheUtil redisCacheUtil;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisLock redisLock;

    @PostConstruct
    public void init() {
        redisCacheUtil = this;
        redisCacheUtil.redisService = this.redisService;
        redisCacheUtil.redisLock = this.redisLock;
    }

    public enum RedisCacheKey {

        PROD_SEARCH_USE_CACHE("ProductSearchUseCache", "商品搜索是否使用缓存"),
        PROD_SEARCH_RESULT_EXPIRE_LIMIT_IN_SECOND("ProductSearchCacheExpireTime", "商品搜索结果数据缓存时效限制:单位秒"),
        PROD_SEARCH_RESULT_CACHE_KEY("prod_search_result_cache_key", "商品搜索结果缓存组KEY");

        private String cacheName;
        private String desc;

        RedisCacheKey(String cacheName, String desc) {
            this.cacheName = cacheName;
            this.desc = desc;
        }

        public String get() {
            return this.cacheName;
        }

        public String getDesc() {
            return this.desc;
        }
    }

    /**
     * @author 王光银
     * @className: ProductSearchCacheUtil
     * @description: 商品搜索结果缓存工具类
     * @create 2018-08-16 17:21
     **/
    public static class ProductSearchCacheUtil {
        private static final Integer DEFAULT_EXPIRE_LIMIT_IN_SECOND = new Integer(3600 * 2);

        /**
         * 获取缓存时效
         * @param default_expire_in_second
         * @return
         */
        public static Integer getExpireLimit(Integer default_expire_in_second) {
            Integer expire_time;
            String config_value = CodeCache.getValueByKey(RedisCacheKey.PROD_SEARCH_RESULT_EXPIRE_LIMIT_IN_SECOND.get(), CommonConstantPool.S_ZERO_ONE);
            if (UtilValidate.isEmpty(config_value)) {
                expire_time = DEFAULT_EXPIRE_LIMIT_IN_SECOND;
            } else {
                try {
                    expire_time = Integer.valueOf(config_value);
                } catch (NumberFormatException e) {
                    expire_time = DEFAULT_EXPIRE_LIMIT_IN_SECOND;
                }
            }
            if (default_expire_in_second != null && default_expire_in_second.intValue() > 0 && default_expire_in_second.intValue() < expire_time.intValue()) {
                return default_expire_in_second;
            }
            return expire_time;
        }

        /**
         * 返回是否允许使用缓存
         * @return
         */
        public static boolean allowUseCache() {
            String config_value = CodeCache.getValueByKey(RedisCacheKey.PROD_SEARCH_USE_CACHE.get(), CommonConstantPool.S_ZERO_ONE);
            return !CommonConstantPool.UPPER_N.equals(config_value);
        }

        /**
         * 返回缓存数据
         * @param party_id
         * @param cache_key
         * @return
         */
        public static List<AppProdListItemResponseVO> getCacheData(Long party_id, String cache_key) {
            if (party_id == null || party_id <= 0L || UtilValidate.isEmpty(cache_key)) {
                return null;
            }

            if (!allowUseCache()) {
                return null;
            }

            String group_name = generateSearchResultGroupName(party_id);

            String cache_data_value = redisCacheUtil.redisService.get2(group_name, cache_key);
            if (UtilValidate.isEmpty(cache_data_value)) {
                removeCacheKey(party_id, cache_key, false);
                return null;
            }
            try {
                return JSON.parseArray(cache_data_value, AppProdListItemResponseVO.class);
            } catch (Exception e) {
                redisCacheUtil.redisService.delete2(group_name, cache_key);
                removeCacheKey(party_id, cache_key, false);
                return null;
            }
        }

        /**
         * 设置缓存数据
         * @param party_id
         * @param cache_key
         * @param data_list
         */
        public static void setCacheData(Long party_id, String cache_key, List<AppProdListItemResponseVO> data_list) {
            setCacheData(party_id, cache_key, data_list, 0L);
        }

        /**
         * 设置缓存数据
         * @param party_id
         * @param cache_key
         * @param data_list
         * @param expire_time_in_second
         */
        public static void setCacheData(Long party_id, String cache_key, List<AppProdListItemResponseVO> data_list, Long expire_time_in_second) {
            if (party_id == null || party_id <= 0L || UtilValidate.isEmpty(cache_key) || UtilValidate.isEmpty(data_list)) {
                return;
            }
            if (!allowUseCache()) {
                return;
            }
            if (expire_time_in_second == null) {
                expire_time_in_second = 0L;
            }
            redisCacheUtil.redisService.put2(generateSearchResultGroupName(party_id), cache_key, JSON.toJSONString(data_list), getExpireLimit(expire_time_in_second.intValue()));
            addCacheKey(party_id, cache_key);
        }

        public static String generateSearchResultGroupName(Long party_id) {
            return RedisCacheKey.PROD_SEARCH_RESULT_CACHE_KEY.get() + "_" + party_id;
        }

        /**
         * 商品搜索结果对 搜索条件的KEY也要缓存，便于对缓存进行有效管理
         */

        public static void addCacheKey(Long party_id, String cache_key) {
            ThreadPoolUtil.submitTask(new ProductSearchCacheKeyManageTask(party_id, cache_key, CommonConstantPool.NUMBER_ONE));
        }

        public static void removeCacheKey(Long party_id, String cache_key, boolean all_remove) {
            if (all_remove) {
                ThreadPoolUtil.submitTask(new ProductSearchCacheKeyManageTask(party_id, cache_key, CommonConstantPool.NUMBER_THREE));
            } else {
                ThreadPoolUtil.submitTask(new ProductSearchCacheKeyManageTask(party_id, cache_key, CommonConstantPool.NUMBER_TWO));
            }

        }

    }

}
