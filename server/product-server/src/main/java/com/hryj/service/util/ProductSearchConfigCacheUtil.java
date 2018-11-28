package com.hryj.service.util;

import com.hryj.cache.CodeCache;
import com.hryj.cache.RedisLock;
import com.hryj.cache.RedisService;
import com.hryj.constant.CommonConstantPool;
import com.hryj.entity.vo.sys.response.CodeInfoVO;
import com.hryj.utils.UtilValidate;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author 王光银
 * @className: ProductSearchConfigCacheUtil
 * @description:  用户端（APP端、门店端、WEB端）商品搜索配置 - 缓存工具类
 *                 基础条件包含： 上下架，禁售，开始销售时间，终止销售时间，库存检查
 * @create 2018/9/19 0019 11:38
 **/
public class ProductSearchConfigCacheUtil {

    private static final AtomicReference<ProdSearchConfigLocalCacheHandler> local_cache = new AtomicReference<>();

    public static ProductSearchConfigCacheUtil productSearchConfigCacheUtil;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisLock redisLock;

    @PostConstruct
    public void init() {
        productSearchConfigCacheUtil = this;
        productSearchConfigCacheUtil.redisService = this.redisService;
        productSearchConfigCacheUtil.redisLock = this.redisLock;
    }

    /**
     * 端口搜索配置组 codeType (在数据字典中使用)
     */
    private static final String UserProductSearchConfigGroup = "UserProductSearchConfigGroup";

    /**
     * 返回是否过滤掉禁售商品， true过滤， false不过滤
     * @return
     */
    public static boolean filterForbidSale() {
        return CommonConstantPool.UPPER_Y.equals(getProductSearchConfigGroupValue(CommonConstantPool.S_ZERO_ONE));
    }

    /**
     * 返回是否过滤掉下架商品， true过滤， false不过滤
     * @return
     */
    public static boolean filterDownSale() {
        return CommonConstantPool.UPPER_Y.equals(getProductSearchConfigGroupValue(CommonConstantPool.S_ZERO_TWO));
    }

    /**
     * 返回是否过滤掉未开始销售商品， true过滤， false不过滤
     * @return
     */
    public static boolean filterNotStartSale() {
        return CommonConstantPool.UPPER_Y.equals(getProductSearchConfigGroupValue(CommonConstantPool.S_ZERO_THREE));
    }

    /**
     * 返回是否过滤掉已结束销售商品， true过滤， false不过滤
     * @return
     */
    public static boolean filterEndSale() {
        return CommonConstantPool.UPPER_Y.equals(getProductSearchConfigGroupValue(CommonConstantPool.S_ZERO_FOUR));
    }

    /**
     * 返回是否过滤掉无库存商品， true过滤， false不过滤
     * @return
     */
    public static boolean filterNoInventory() {
        return CommonConstantPool.UPPER_Y.equals(getProductSearchConfigGroupValue(CommonConstantPool.S_ZERO_FIVE));
    }

    private static String getProductSearchConfigGroupValue(String key) {
        if (local_cache.get() != null && local_cache.get().get() != null) {
            return local_cache.get().getValue(key);
        }
        List<CodeInfoVO> config_list = CodeCache.getCodeList(UserProductSearchConfigGroup);
        ProdSearchConfigLocalCacheHandler localCacheHandler = new ProdSearchConfigLocalCacheHandler(config_list);
        local_cache.set(localCacheHandler);
        return localCacheHandler.getValue(key);
    }

    /**
     * 返回
     * @return
     */
    public static Map<String, Object> generateSearchCondition() {
        Map<String, Object> condition = new LinkedHashMap<>();
        Date curr = new Date();
        if (filterForbidSale()) {
            condition.put("forbid_sale_flag", ProductUtil.UP_STATUS);
        }
        if (filterDownSale()) {
            condition.put("up_down_status", ProductUtil.UP_STATUS);
        }
        if (filterNotStartSale()) {
            condition.put("introduction_date", curr);
            condition.put("start_date", curr);
        }
        if (filterEndSale()) {
            condition.put("sales_end_date", curr);
            condition.put("end_date", curr);
        }
        if (filterNoInventory()) {
            condition.put("inventory_quantity", CommonConstantPool.NUMBER_ZERO);
        }
        return condition;
    }

    @Data
    static class ProdSearchConfigLocalCacheHandler {

        private Map<String, CodeInfoVO> config_map;

        private long cache_start_time;

        private long expire_time;

        private long cache_expire_time;

        public ProdSearchConfigLocalCacheHandler(List<CodeInfoVO> config_list) {
            this.cache_start_time = System.currentTimeMillis();
            /**
             * 本地缓存有效时间设置为 4 个小时
             */
            this.expire_time = 4 * 60 * 60 * 1000L;
            this.cache_expire_time = this.cache_start_time + this.expire_time;

            if (UtilValidate.isEmpty(config_list)) {
                return;
            }
            this.config_map = new HashMap<>(config_list.size());
            for (CodeInfoVO item : config_list) {
                this.config_map.put(item.getCode_key(), item);
            }
        }

        public String getValue(String key) {
            if (UtilValidate.isEmpty(this.config_map) || !this.config_map.containsKey(key)) {
                return CommonConstantPool.UPPER_Y;
            }
            return this.config_map.get(key).getCode_value();
        }

        public ProdSearchConfigLocalCacheHandler get() {
            long curr = System.currentTimeMillis();
            if (curr > this.cache_expire_time) {
                return null;
            }
            return this;
        }
    }
}
