package com.hryj.cacheutil;

import com.alibaba.fastjson.JSON;
import com.hryj.cache.RedisLock;
import com.hryj.cache.RedisService;
import com.hryj.entity.bo.product.Brand;
import com.hryj.utils.UtilValidate;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author 汪豪
 * @className: ProductBrandCacheHandler
 * @description:
 * @create 2018/10/10 0010 11:43
 **/
@Component
@Slf4j
@Data
public class ProductBrandCacheHandler {
    public static ProductBrandCacheHandler productBrandCacheHandler;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisLock redisLock;

    @PostConstruct
    public void init() {
        productBrandCacheHandler = this;
        productBrandCacheHandler.redisService = this.redisService;
        productBrandCacheHandler.redisLock = this.redisLock;
    }


    private static final String PRODUCT_BRAND_CACHE_GROUP_NAME = "product_brand_cache_group_name";

    /**
     * 从缓存中获取一个品牌数据
     * @param brand_id  品牌ID
     * @return
     */
    public static Brand getBrand(Long brand_id) {
        if (brand_id == null || brand_id <= 0L) {
            return null;
        }

        String json_str = productBrandCacheHandler.redisService.get2(PRODUCT_BRAND_CACHE_GROUP_NAME, brand_id.toString());
        if (UtilValidate.isEmpty(json_str)) {
            return null;
        }

        try {
            Brand brand = JSON.toJavaObject(JSON.parseObject(json_str), Brand.class);
            return brand;
        } catch (Exception e) {
            log.error("从缓存中获取品牌反序列化失败:", e);
            return null;
        }
    }


}
