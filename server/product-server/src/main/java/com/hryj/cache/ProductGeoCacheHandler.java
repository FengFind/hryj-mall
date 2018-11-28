package com.hryj.cache;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hryj.entity.bo.product.ProductGeo;
import com.hryj.mapper.ProductGeoMapper;
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
 * @className: ProductGeoCacheHandler
 * @description: 产地基础数据缓存工具类
 * @create 2018/9/12 0012 15:21
 **/
@Component
@Slf4j
@Data
public class ProductGeoCacheHandler {

    public static ProductGeoCacheHandler productGeoCacheHandler;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisLock redisLock;

    @PostConstruct
    public void init() {
        productGeoCacheHandler = this;
        productGeoCacheHandler.redisService = this.redisService;
        productGeoCacheHandler.redisLock = this.redisLock;
    }

    private static final String PRODUCT_GEO_CACHE_GROUP_NAME = "product_geo_cache_group_name";

    /**
     * 删除一个缓存内的品牌数据
     * @param made_where_id
     */
    public static void deleteBrand(Long made_where_id) {
        if (made_where_id == null || made_where_id <= 0L) {
            return;
        }
        productGeoCacheHandler.redisService.delete2(PRODUCT_GEO_CACHE_GROUP_NAME, made_where_id.toString());
    }

    /**
     * 刷新一个缓存内的品牌数据
     * @param productGeo
     */
    public static void resetProductGeo(ProductGeo productGeo) {
        if (productGeo == null || productGeo.getId() == null || productGeo.getId() <= 0L) {
            return;
        }

        try {
            //加锁
            boolean lock_result;
            while (true) {
                lock_result = productGeoCacheHandler.redisLock.lock(PRODUCT_GEO_CACHE_GROUP_NAME, productGeo.getId().toString(), 2);
                if (lock_result) {
                    break;
                }
                Thread.sleep(50);
            }
            productGeoCacheHandler.redisService.delete2(PRODUCT_GEO_CACHE_GROUP_NAME, productGeo.getId().toString());
            productGeoCacheHandler.redisService.put2(PRODUCT_GEO_CACHE_GROUP_NAME, productGeo.getId().toString(), JSON.toJSONString(productGeo), null);
        } catch (Exception e) {
            log.error("刷新产地缓存失败:", e);
        } finally {
            //释放锁
            productGeoCacheHandler.redisLock.unLock(PRODUCT_GEO_CACHE_GROUP_NAME, productGeo.getId().toString());
        }
    }

    /**
     * 从缓存中获取一个产地
     * @param made_where_id  品牌ID
     * @return
     */
    public static ProductGeo getProductGeo(Long made_where_id) {
        if (made_where_id == null || made_where_id <= 0L) {
            return null;
        }

        String json_str = productGeoCacheHandler.redisService.get2(PRODUCT_GEO_CACHE_GROUP_NAME, made_where_id.toString());
        if (UtilValidate.isEmpty(json_str)) {
            return null;
        }

        try {
            ProductGeo productGeo = JSON.toJavaObject(JSON.parseObject(json_str), ProductGeo.class);
            return productGeo;
        } catch (Exception e) {
            log.error("从缓存中获取产地反序列化失败:", e);
            return null;
        }
    }

    public static void cacheInit() {
        //加锁
        boolean lock_result = productGeoCacheHandler.redisLock.lock(PRODUCT_GEO_CACHE_GROUP_NAME, PRODUCT_GEO_CACHE_GROUP_NAME, 5);
        if (!lock_result) {
            return;
        }

        cleanCache();

        try {
            ProductGeoMapper productGeoMapper = SpringContextUtil.getBean("productGeoMapper", ProductGeoMapper.class);
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.orderBy("id", true);
            List<ProductGeo> data_list = productGeoMapper.selectList(wrapper);
            if (UtilValidate.isEmpty(data_list)) {
                return;
            }

            for (ProductGeo item : data_list) {
                productGeoCacheHandler.redisService.put2(PRODUCT_GEO_CACHE_GROUP_NAME, item.getId().toString(), JSON.toJSONString(item), null);
            }

            log.info("------- 产地基础数据缓存任务执行完成...");
        } catch (Exception e) {
            log.error("缓存产地基础数据失败:", e);
        } finally {
            productGeoCacheHandler.redisLock.unLock(PRODUCT_GEO_CACHE_GROUP_NAME, PRODUCT_GEO_CACHE_GROUP_NAME);
        }
    }

    private static void cleanCache() {
        Set<String> cache_keys = productGeoCacheHandler.redisService.getKeysByGroupName2(PRODUCT_GEO_CACHE_GROUP_NAME);
        if (UtilValidate.isNotEmpty(cache_keys)) {
            for (String key : cache_keys) {
                productGeoCacheHandler.redisService.delete2(PRODUCT_GEO_CACHE_GROUP_NAME, key);
            }
        }
    }
}
