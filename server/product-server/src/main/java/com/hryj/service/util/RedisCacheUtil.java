package com.hryj.service.util;

import com.alibaba.fastjson.JSON;
import com.hryj.cache.CodeCache;
import com.hryj.cache.RedisLock;
import com.hryj.cache.RedisService;
import com.hryj.constant.CommonConstantPool;
import com.hryj.entity.vo.product.response.app.AppProdListItemResponseVO;
import com.hryj.service.inventory.ProductInventoryService;
import com.hryj.service.worktask.CleanProductSearchResultByPartyIdTask;
import com.hryj.service.worktask.ProductSearchCacheKeyManageTask;
import com.hryj.threadpool.ThreadPoolUtil;
import com.hryj.utils.UtilValidate;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

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

        PARTY_PRODUCT_QUANTITY_STATISTICS_CACHE_GROUP("party_product_quantity_statistics_cache_group", "门店仓库在售商品数量统计缓存组"),
        PARTY_PRODUCT_QUANTITY_STATISTICS_RESULT_EXPIRE_IN_SECOND("party_product_quantity_statistics_result_expire_in_second", "门店仓库在售商品数量统计缓存数据有效时限配置"),
        PARTY_PRODUCT_QUANTITY_STATISTICS_USE_CACHE("party_product_quantity_statistics_use_cache", "门店仓库在售商品数量统计是否允许使用缓存"),

        PARTY_RECOMMEND_PROD_CACHE_KEY("party_recommend_prod_cache_key", "门店推荐位商品数据缓存组KEY"),
        PARTY_RECOMMEND_PROD_EXPIRE_LIMIT_IN_SECOND("RecommendProductCacheExpireTime", "门店推荐位商品数据缓存时效限制:单位秒"),
        PARTY_RECOMMEND_PROD_USE_CACHE("RecommendProductUseCache", "推荐商品搜索是否使用缓存"),

        PROD_SEARCH_USE_CACHE("ProductSearchUseCache", "商品搜索是否使用缓存"),
        PROD_SEARCH_RESULT_EXPIRE_LIMIT_IN_SECOND("ProductSearchCacheExpireTime", "商品搜索结果数据缓存时效限制:单位秒"),
        PROD_SEARCH_RESULT_CACHE_KEY("prod_search_result_cache_key", "商品搜索结果缓存组KEY"),

        PARTY_PROD_INVENTORY_LOCK_EXPIRE_IN_SECOND("party_prod_inventory_lock_expire_in_second", "商品库存锁定数据缓存失效时间限制：单位秒"),
        PARTY_PROD_INVENTORY_LOCK_CACHE_KEY("party_prod_inventory_lock_cache_key", "商品库存锁定数据缓存组KEY"),
        PARTY_PROD_INVENTORY_TRY_LOCK_MAX_TIMES("party_prod_inventory_try_lock_max_times", "库存锁定的最大尝试次数"),
        PARTY_PROD_INVENTORY_LOCKED_EXPIRE_TIME("party_prod_inventory_locked_expire_time", "库存锁定成功后,自动释放锁的时间，单位：秒"),
        PARTY_PROD_INVENTORY_LOCK_FAILED_SLEEP_TIME("party_prod_inventory_lock_failed_sleep_time", "获取锁失败时的暂停时间，单位毫秒");

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
     * @className: RedisCacheUtil
     * @description: 门店商品库存锁定工具类
     * @create 2018-08-29 10:01
     **/
    public static class PartyProductInventoryLockCacheUtil {

        private static final Integer PARTY_PROD_INVENTORY_TRY_LOCK_MAX_TIMES = 20;

        private static final Integer PARTY_PROD_INVENTORY_LOCK_FAILED_SLEEP_TIME = 50;

        private static final Integer LOCK_EXPIRE_TIME = 60;

        private static Integer getMaxTryLockTimes() {
            String config_value = CodeCache.getValueByKey(RedisCacheKey.PARTY_PROD_INVENTORY_TRY_LOCK_MAX_TIMES.get(), CommonConstantPool.S_ZERO_ONE);
            if (UtilValidate.isEmpty(config_value)) {
                return PARTY_PROD_INVENTORY_TRY_LOCK_MAX_TIMES;
            }
            try {
                return Integer.valueOf(config_value);
            } catch (NumberFormatException e) {
                return PARTY_PROD_INVENTORY_TRY_LOCK_MAX_TIMES;
            }
        }

        private static Integer getSleepTime() {
            String config_value = CodeCache.getValueByKey(RedisCacheKey.PARTY_PROD_INVENTORY_LOCK_FAILED_SLEEP_TIME.get(), CommonConstantPool.S_ZERO_ONE);
            if (UtilValidate.isEmpty(config_value)) {
                return PARTY_PROD_INVENTORY_LOCK_FAILED_SLEEP_TIME;
            }
            try {
                return Integer.valueOf(config_value);
            } catch (NumberFormatException e) {
                return PARTY_PROD_INVENTORY_LOCK_FAILED_SLEEP_TIME;
            }
        }

        private static Integer getLockExpireTime() {
            String config_value = CodeCache.getValueByKey(RedisCacheKey.PARTY_PROD_INVENTORY_LOCKED_EXPIRE_TIME.get(), CommonConstantPool.S_ZERO_ONE);
            if (UtilValidate.isEmpty(config_value)) {
                return LOCK_EXPIRE_TIME;
            }
            try {
                return Integer.valueOf(config_value);
            } catch (NumberFormatException e) {
                return LOCK_EXPIRE_TIME;
            }
        }

        public static boolean addLock(Collection<ProductInventoryService.InventoryUniqueItem> list) {
            List<String> lock_key_list = generateLockKeyList(list);
            if (UtilValidate.isEmpty(lock_key_list)) {
                return false;
            }

            Map<String, Integer> try_lock_count_map = new HashMap<>(lock_key_list.size());

            Set<String> lock_success_set = new HashSet<>(lock_key_list.size());

            boolean need_to_suspend = false;

            Integer max_times = getMaxTryLockTimes();

            Integer max_sleep_times = getSleepTime();


            /**锁的失效时间根据：最大尝试次数、失败后的暂停时间以及请求的库存条目数量进行计算，
             * 原则是必须保证：一但锁定成功后，锁不会达到失效时间自动释放，而是由程序主动释放
             */
            Integer min_expire_time = ((max_times * max_sleep_times) + (10000)) * list.size();

            while (lock_key_list.size() > 0) {

                if (need_to_suspend) {
                    try {
                        Thread.sleep(max_sleep_times);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Iterator<String> it = lock_key_list.iterator();
                while (it.hasNext()) {
                    String lock_key = it.next();
                    boolean lock_result = redisCacheUtil.redisLock.lock(RedisCacheKey.PARTY_PROD_INVENTORY_LOCK_CACHE_KEY.get(), lock_key, min_expire_time);
                    if (lock_result) {
                        it.remove();
                        lock_success_set.add(lock_key);
                        if (try_lock_count_map.containsKey(lock_key)) {
                            try_lock_count_map.remove(lock_key);
                        }
                        continue;
                    }
                    try_lock_count_map.put(lock_key, try_lock_count_map.containsKey(lock_key) ? try_lock_count_map.get(lock_key) + 1 : 1);
                }

                //尝试锁定达到限制后，返回失败
                if (try_lock_count_map.containsValue(max_times)) {
                    if (UtilValidate.isNotEmpty(lock_success_set)) {
                        for (String lock_key : lock_success_set) {
                            redisCacheUtil.redisLock.unLock(RedisCacheKey.PARTY_PROD_INVENTORY_LOCK_CACHE_KEY.get(), lock_key);
                        }
                    }
                    return false;
                }

                need_to_suspend = true;
            }

            return true;
        }

        public static void releaseLock(Collection<ProductInventoryService.InventoryUniqueItem> list) {
            List<String> lock_key_list = generateLockKeyList(list);
            if (UtilValidate.isEmpty(lock_key_list)) {
                return;
            }
            for (String lock_key : lock_key_list) {
                redisCacheUtil.redisLock.unLock(RedisCacheKey.PARTY_PROD_INVENTORY_LOCK_CACHE_KEY.get(), lock_key);
            }
        }

        public static List<String> generateLockKeyList(Collection<ProductInventoryService.InventoryUniqueItem> list) {
            if (UtilValidate.isEmpty(list)) {
                return null;
            }
            List<String> lock_key_list = new ArrayList<>(list.size());
            for (ProductInventoryService.InventoryUniqueItem item : list) {
                lock_key_list.add(String.valueOf(item.hashCode()));
            }
            return lock_key_list;
        }


        private static final Integer DEFAULT_EXPIRE_LIMIT_IN_SECOND = new Integer(300);
        //private static final Integer DEFAULT_EXPIRE_LIMIT_IN_SECOND = new Integer(5);

        private static Integer getExpireLimit() {
            String config_value = CodeCache.getValueByKey(RedisCacheKey.PARTY_PROD_INVENTORY_LOCK_EXPIRE_IN_SECOND.get(), CommonConstantPool.S_ZERO_ONE);
            if (UtilValidate.isEmpty(config_value)) {
                return DEFAULT_EXPIRE_LIMIT_IN_SECOND;
            }
            try {
                return Integer.valueOf(config_value);
            } catch (NumberFormatException e) {
                return DEFAULT_EXPIRE_LIMIT_IN_SECOND;
            }
        }

        public static boolean allowUseCache() {
            return true;
        }

        public static String getCacheData(String cache_key) {
            if (UtilValidate.isEmpty(cache_key)) {
                return null;
            }

            if (!allowUseCache()) {
                return null;
            }

            String cache_data_value = redisCacheUtil.redisService.get2(RedisCacheKey.PARTY_PROD_INVENTORY_LOCK_CACHE_KEY.get(), cache_key);
            if (UtilValidate.isEmpty(cache_data_value)) {
                return null;
            }
            return cache_data_value;
        }

        public static void setCacheData(String cache_key, String cache_value) {
            if (UtilValidate.isEmpty(cache_key) || UtilValidate.isEmpty(cache_value)) {
                return;
            }
            if (!allowUseCache()) {
                return;
            }
            redisCacheUtil.redisService.put2(RedisCacheKey.PARTY_PROD_INVENTORY_LOCK_CACHE_KEY.get(), cache_key, cache_value, getExpireLimit());
        }

        public static void cleanByCacheKey(String cache_key) {
            if (UtilValidate.isEmpty(cache_key)) {
                return;
            }
            redisCacheUtil.redisService.delete2(RedisCacheKey.PARTY_PROD_INVENTORY_LOCK_CACHE_KEY.get(), cache_key);
        }
    }

    /**
     * @author 王光银
     * @className: PartyProductStatisticsCacheUtil
     * @description: 门店商品基于分类的统计数量数据缓存
     * @create 2018-08-29 10:02
     **/
    public static class PartyProductStatisticsCacheUtil {
        private static final Integer DEFAULT_EXPIRE_LIMIT_IN_SECOND = new Integer(3600 * 10);

        private static Integer getExpireLimit() {
            String config_value = CodeCache.getValueByKey(RedisCacheKey.PARTY_PRODUCT_QUANTITY_STATISTICS_RESULT_EXPIRE_IN_SECOND.get(), CommonConstantPool.S_ZERO_ONE);
            if (UtilValidate.isEmpty(config_value)) {
                return DEFAULT_EXPIRE_LIMIT_IN_SECOND;
            }
            try {
                return Integer.valueOf(config_value);
            } catch (NumberFormatException e) {
                return DEFAULT_EXPIRE_LIMIT_IN_SECOND;
            }
        }

        public static boolean allowUseCache() {
            String config_value = CodeCache.getValueByKey(RedisCacheKey.PARTY_PRODUCT_QUANTITY_STATISTICS_USE_CACHE.get(), CommonConstantPool.S_ZERO_ONE);
            return !CommonConstantPool.UPPER_N.equals(config_value);
        }

        public static Integer getCacheData(Long party_id) {
            if (party_id == null || party_id <= 0L) {
                return null;
            }

            if (!allowUseCache()) {
                return null;
            }

            String cache_data_value = redisCacheUtil.redisService.get2(RedisCacheKey.PARTY_PRODUCT_QUANTITY_STATISTICS_CACHE_GROUP.get(), party_id.toString());
            if (UtilValidate.isEmpty(cache_data_value)) {
                return null;
            }
            try {
                return Integer.valueOf(cache_data_value);
            } catch (Exception e) {
                redisCacheUtil.redisService.delete2(RedisCacheKey.PARTY_PRODUCT_QUANTITY_STATISTICS_CACHE_GROUP.get(), party_id.toString());
                return null;
            }
        }

        public static void setCacheData(Long party_id, Integer prod_quantity) {
            if (party_id == null || party_id <= 0L || prod_quantity == null) {
                return;
            }
            if (!allowUseCache()) {
                return;
            }
            redisCacheUtil.redisService.put2(RedisCacheKey.PARTY_PRODUCT_QUANTITY_STATISTICS_CACHE_GROUP.get(), party_id.toString(), prod_quantity.toString(), getExpireLimit());
        }

        public static void cleanByPartyId(Long party_id) {
            if (party_id == null || party_id <= 0L) {
                return;
            }
            redisCacheUtil.redisService.delete2(RedisCacheKey.PARTY_PRODUCT_QUANTITY_STATISTICS_CACHE_GROUP.get(), party_id.toString());
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
            if (party_id == null || party_id <= 0L) {
                return;
            }
            if (!allowUseCache()) {
                return;
            }
            if (expire_time_in_second == null) {
                expire_time_in_second = 0L;
            }

            if (data_list == null) {
                data_list = new ArrayList<>();
            }
            redisCacheUtil.redisService.put2(generateSearchResultGroupName(party_id), cache_key, JSON.toJSONString(data_list), getExpireLimit(expire_time_in_second.intValue()));
            addCacheKey(party_id, cache_key);
        }

        /**
         * 清除某个组织（门店或仓库）下的所有商品搜索结果缓存数据
         * @param party_id
         */
        public static void cleanByPartyId(Long party_id) {
            if (party_id == null || party_id <= 0L) {
                return;
            }
            //异步处理缓存数据的清除
            ThreadPoolUtil.submitTask(new CleanProductSearchResultByPartyIdTask(party_id));
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

    /**
     * @author 王光银
     * @className: PartyRecommendProdCacheUtil
     * @description: 推荐商品缓存工具类
     * @create 2018-08-16 15:03
     **/
    public static class PartyRecommendProdCacheUtil {

        private static final Integer DEFAULT_EXPIRE_LIMIT_IN_SECOND = new Integer(3600);

        private static Integer getExpireLimit() {
            String config_value = CodeCache.getValueByKey(RedisCacheKey.PARTY_RECOMMEND_PROD_EXPIRE_LIMIT_IN_SECOND.get(), CommonConstantPool.S_ZERO_ONE);
            if (UtilValidate.isEmpty(config_value)) {
                return DEFAULT_EXPIRE_LIMIT_IN_SECOND;
            }
            try {
                return Integer.valueOf(config_value);
            } catch (NumberFormatException e) {
                return DEFAULT_EXPIRE_LIMIT_IN_SECOND;
            }
        }

        public static boolean allowUseCache() {
            String config_value = CodeCache.getValueByKey(RedisCacheKey.PARTY_RECOMMEND_PROD_USE_CACHE.get(), CommonConstantPool.S_ZERO_ONE);
            return !CommonConstantPool.UPPER_N.equals(config_value);
        }

        public static List<AppProdListItemResponseVO> getCacheData(Long target_party_id) {
            if (target_party_id == null || target_party_id <= 0L) {
                return null;
            }

            if (!allowUseCache()) {
                return null;
            }

            String cache_data_value = redisCacheUtil.redisService.get2(RedisCacheKey.PARTY_RECOMMEND_PROD_CACHE_KEY.get(), target_party_id.toString());
            if (UtilValidate.isEmpty(cache_data_value)) {
                return null;
            }
            try {
                return JSON.parseArray(cache_data_value, AppProdListItemResponseVO.class);
            } catch (Exception e) {
                redisCacheUtil.redisService.delete2(RedisCacheKey.PARTY_RECOMMEND_PROD_CACHE_KEY.get(), target_party_id.toString());
                return null;
            }
        }

        public static void setCacheData(Long target_party_id, List<AppProdListItemResponseVO> data_list) {
            if (target_party_id == null || target_party_id <= 0L) {
                return;
            }

            if (!allowUseCache()) {
                return;
            }

            if (data_list == null) {
                redisCacheUtil.redisService.put2(RedisCacheKey.PARTY_RECOMMEND_PROD_CACHE_KEY.get(), target_party_id.toString(), JSON.toJSONString(new ArrayList<>()), getExpireLimit());
            }
            redisCacheUtil.redisService.put2(RedisCacheKey.PARTY_RECOMMEND_PROD_CACHE_KEY.get(), target_party_id.toString(), JSON.toJSONString(data_list), getExpireLimit());
        }

        public static void cleanByPartyId(Long target_party_id) {
            if (target_party_id == null || target_party_id <= 0L) {
                return;
            }
            redisCacheUtil.redisService.delete2(RedisCacheKey.PARTY_RECOMMEND_PROD_CACHE_KEY.get(), target_party_id.toString());
        }
    }
}
