package com.hryj.cache;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hryj.entity.bo.product.Brand;
import com.hryj.mapper.BrandMapper;
import com.hryj.utils.SpringContextUtil;
import com.hryj.utils.UtilValidate;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

/**
 * @author 王光银
 * @className: ProductBrandCacheHandler
 * @description: 商品品牌基础数据缓存类
 * @create 2018/9/12 0012 15:20
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
     * 删除一个缓存内的品牌数据
     * @param brand_id
     */
    public static void deleteBrand(Long brand_id) {
        if (brand_id == null || brand_id <= 0L) {
            return;
        }
        productBrandCacheHandler.redisService.delete2(PRODUCT_BRAND_CACHE_GROUP_NAME, brand_id.toString());
    }

    /**
     * 刷新一个缓存内的品牌数据
     * @param brand
     */
    public static void resetBrand(Brand brand) {
        if (brand == null || brand.getId() == null || brand.getId() <= 0L) {
            return;
        }

        try {
            //加锁
            boolean lock_result;
            while (true) {
                lock_result = productBrandCacheHandler.redisLock.lock(PRODUCT_BRAND_CACHE_GROUP_NAME, brand.getId().toString(), 2);
                if (lock_result) {
                    break;
                }
                Thread.sleep(50);
            }
            productBrandCacheHandler.redisService.delete2(PRODUCT_BRAND_CACHE_GROUP_NAME, brand.getId().toString());
            productBrandCacheHandler.redisService.put2(PRODUCT_BRAND_CACHE_GROUP_NAME, brand.getId().toString(), JSON.toJSONString(brand), null);
        } catch (Exception e) {
            log.error("刷新品牌缓存失败:", e);
        } finally {
            //释放锁
            productBrandCacheHandler.redisLock.unLock(PRODUCT_BRAND_CACHE_GROUP_NAME, brand.getId().toString());
        }
    }


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


    public static void cacheInit() {
        //加锁
        boolean lock_result = productBrandCacheHandler.redisLock.lock(PRODUCT_BRAND_CACHE_GROUP_NAME, PRODUCT_BRAND_CACHE_GROUP_NAME, 60);
        if (!lock_result) {
            return;
        }

        cleanCache();

        try {
            BrandMapper brandMapper = SpringContextUtil.getBean("brandMapper", BrandMapper.class);
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.orderBy("id", true);
            List<Brand> data_list = brandMapper.selectList(wrapper);
            if (UtilValidate.isEmpty(data_list)) {
                return;
            }

            for (Brand item : data_list) {
                productBrandCacheHandler.redisService.put2(PRODUCT_BRAND_CACHE_GROUP_NAME, item.getId().toString(), JSON.toJSONString(item), null);
            }

            log.info("------- 品牌基础数据缓存任务执行完成...");
        } catch (Exception e) {
            log.error("缓存品牌基础数据失败:", e);
        } finally {
            productBrandCacheHandler.redisLock.unLock(PRODUCT_BRAND_CACHE_GROUP_NAME, PRODUCT_BRAND_CACHE_GROUP_NAME);
        }
    }

    private static void cleanCache() {
        Set<String> cache_keys = productBrandCacheHandler.redisService.getKeysByGroupName2(PRODUCT_BRAND_CACHE_GROUP_NAME);
        if (UtilValidate.isNotEmpty(cache_keys)) {
            for (String key : cache_keys) {
                productBrandCacheHandler.redisService.delete2(PRODUCT_BRAND_CACHE_GROUP_NAME, key);
            }
        }
    }
}
