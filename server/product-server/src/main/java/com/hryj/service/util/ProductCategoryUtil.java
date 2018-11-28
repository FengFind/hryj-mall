package com.hryj.service.util;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hryj.cache.RedisLock;
import com.hryj.cache.RedisService;
import com.hryj.mapper.ProductCategoryMapper;
import com.hryj.service.prodcate.ProdCateTreeHandler;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author 王光银
 * @className: ProductCategoryUtil
 * @description:
 * @create 2018/7/23 0023 15:17
 **/
@Slf4j
@Component
public class ProductCategoryUtil {

    public static ProductCategoryUtil productCategoryUtil;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisLock redisLock;

    @PostConstruct
    public void init() {
        productCategoryUtil = this;
        productCategoryUtil.redisService = this.redisService;
        productCategoryUtil.redisLock = this.redisLock;
    }

    public static final String NEED_RELOAD = "need_reload";

    public static final String NEED_RELOAD_VALUE = "Y";

    public static final String PROD_CATE_CACHE_GROUP_NAME = "prod_cate_cache_group";

    public static final String PROD_CATE_CACHE_KEY = "prod_cate";

    public static final String PRODUCT_CATEGORY_CHANGED_FLAG = "productCategoryChangeFlag";


    public static boolean prodCateNameExists(Long categroy_id, String category_name, Long catalog_id, ProductCategoryMapper productCategoryMapper) {
        if (UtilValidate.isEmpty(category_name)) {
            throw new NullPointerException("parameter:[category_name] can't be null");
        }
        if (productCategoryMapper == null) {
            throw new NullPointerException("parameter:[productCategoryMapper] can't be null");
        }
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("category_name", category_name.trim());
        if (categroy_id != null && categroy_id > 0L) {
            wrapper.eq("id", categroy_id);
        }
        if (catalog_id != null && catalog_id > 0L) {
            wrapper.eq("catalog_id", catalog_id);
        }

        int compareTo = (categroy_id != null && categroy_id > 0L) ? 1 : 0;
        int count = productCategoryMapper.selectCount(wrapper);
        if (count > compareTo) {
            return true;
        }
        return false;
    }

    /**
     * 通知商品分类缓存数据已失效，需要重新从数据库加载
     */
    public static void notifyNeedToReload() {
        productCategoryUtil.redisService.put2(PRODUCT_CATEGORY_CHANGED_FLAG, NEED_RELOAD, NEED_RELOAD_VALUE, null);
    }

    /**
     * 通知商品分类缓存不需要重新加载
     */
    public static void notifyNotNeedToReload() {
        productCategoryUtil.redisService.delete2(PRODUCT_CATEGORY_CHANGED_FLAG, NEED_RELOAD);
    }

    /**
     * 缓存商品分类数据
     * @param treeHandler
     */
    public static void cacheProdCateData(ProdCateTreeHandler treeHandler) {
        if (treeHandler == null) {
            notifyNeedToReload();
            return;
        }
        try {
            productCategoryUtil.redisService.put2(PROD_CATE_CACHE_GROUP_NAME, PROD_CATE_CACHE_KEY, JSONObject.toJSONString(treeHandler), null);
        } catch (Exception e) {
            notifyNeedToReload();
        }
    }

    /**
     * 从缓存中获取商品分类数据
     * @return
     */
    public static ProdCateTreeHandler getProdCateTreeHandlerFromCache() {
        String cache_value = productCategoryUtil.redisService.get2(ProductCategoryUtil.PROD_CATE_CACHE_GROUP_NAME, ProductCategoryUtil.PROD_CATE_CACHE_KEY);
        if (UtilValidate.isEmpty(cache_value)) {
            notifyNeedToReload();
            return null;
        }

        try {
            return JSONObject.parseObject(cache_value, ProdCateTreeHandler.class);
        } catch (Exception e) {
            log.error("反序列化商品分类数据失败:", e);
            deleteProdCateTreeFromCache();
            ProductCategoryUtil.notifyNeedToReload();
            return null;
        }
    }

    /**
     * 删除商品分类缓存数据
     */
    public static void deleteProdCateTreeFromCache() {
        productCategoryUtil.redisService.delete2(PROD_CATE_CACHE_GROUP_NAME, PROD_CATE_CACHE_KEY);
    }

    /**
     * 返回是否需要重新加载商品分类数据
     * @return
     */
    public static boolean needToReload() {
        String cache_result = productCategoryUtil.redisService.get2(PRODUCT_CATEGORY_CHANGED_FLAG, ProductCategoryUtil.NEED_RELOAD);
        if (ProductCategoryUtil.NEED_RELOAD_VALUE.equals(cache_result)) {
            return true;
        }
        return false;
    }

}
