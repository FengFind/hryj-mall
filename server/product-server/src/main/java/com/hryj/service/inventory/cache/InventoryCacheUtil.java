package com.hryj.service.inventory.cache;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.SqlHelper;
import com.hryj.cache.ProductTypeCacheHandler;
import com.hryj.cache.RedisLock;
import com.hryj.cache.RedisService;
import com.hryj.entity.bo.product.PartyProduct;
import com.hryj.entity.bo.product.crossborder.CrossBorderProduct;
import com.hryj.mapper.CrossBorderProductMapper;
import com.hryj.service.PartyProductService;
import com.hryj.service.inventory.ProductInventoryService;
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
 * @className: InventoryCacheUtil
 * @description: 商品库存缓存工具类
 * @create 2018/10/8 0008 15:35
 **/
@Slf4j
@Component
@Data
public class InventoryCacheUtil {

    public static InventoryCacheUtil inventoryCacheUtil;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisLock redisLock;

    @PostConstruct
    public void init() {
        inventoryCacheUtil = this;
        inventoryCacheUtil.redisService = this.redisService;
        inventoryCacheUtil.redisLock = this.redisLock;
    }

    private static final String PROD_INVENTORY_CACHE_GROUP_NAME = "product.inventory.quantity.cache.group";

    public static void addCache(ProductInventorySummary productInventorySummary) {
        if (productInventorySummary == null) {
            return;
        }
        if (productInventorySummary.getInventory_quantity() == null) {
            log.warn("product inventory quantity war null-value, skip the cache-operation and return...");
            return;
        }
        String cache_key = String.valueOf(productInventorySummary.hashCode());
        inventoryCacheUtil.redisService.put2(PROD_INVENTORY_CACHE_GROUP_NAME, cache_key, productInventorySummary.getInventory_quantity().toString(), null);
    }

    public static void addCacheWithLock(ProductInventorySummary productInventorySummary) {
        if (productInventorySummary == null) {
            return;
        }
        if (productInventorySummary.getInventory_quantity() == null) {
            log.warn("product inventory quantity war null-value, skip the cache-operation and return...");
            return;
        }
        String cache_key = String.valueOf(productInventorySummary.hashCode());

        while (inventoryCacheUtil.redisLock.lock(PROD_INVENTORY_CACHE_GROUP_NAME, cache_key, 3)) {
            addCache(productInventorySummary);
            break;
        }
        inventoryCacheUtil.redisLock.unLock(PROD_INVENTORY_CACHE_GROUP_NAME, cache_key);
    }

    public static void cleanAll() {
        Set<String> keys = inventoryCacheUtil.redisService.getKeysByGroupName2(PROD_INVENTORY_CACHE_GROUP_NAME);
        if (UtilValidate.isNotEmpty(keys)) {
            for (String key : keys) {
                inventoryCacheUtil.redisService.delete2(PROD_INVENTORY_CACHE_GROUP_NAME, key);
            }
        }
    }

    public static Integer adjustment(ProductInventoryService.InventoryUniqueItem item) {
        if (item == null) {
            throw new NullPointerException("parameter: item is null");
        }
        ProductInventorySummary pis = item.getProductInventorySummary();
        Integer inventory_quantity = get(pis);
        pis.setInventory_quantity(inventory_quantity + item.getLock_num());
        addCache(pis);
        return inventory_quantity;
    }

    public static void adjustmentWithLock(ProductInventoryService.InventoryUniqueItem item) {
        if (item == null) {
            throw new NullPointerException("parameter: item is null");
        }
        String cache_key = String.valueOf(item.hashCode());
        while (inventoryCacheUtil.redisLock.lock(PROD_INVENTORY_CACHE_GROUP_NAME, cache_key, 3)) {
            adjustment(item);
            break;
        }
        inventoryCacheUtil.redisLock.unLock(PROD_INVENTORY_CACHE_GROUP_NAME, cache_key);
    }

    public static Integer get(ProductInventorySummary productInventorySummary) {
        if (productInventorySummary == null) {
            return -1;
        }
        String cache_key = String.valueOf(productInventorySummary.hashCode());

        String cache_value;
        while (true) {
            cache_value = inventoryCacheUtil.redisService.get2(PROD_INVENTORY_CACHE_GROUP_NAME, cache_key);
            if (UtilValidate.isEmpty(cache_value)) {
                Integer inventory_quantity = loadInventoryQuantityFromDb(productInventorySummary);
                productInventorySummary.setInventory_quantity(inventory_quantity);
                addCache(productInventorySummary);
                continue;
            }
            break;
        }
        try {
            return Integer.valueOf(cache_value);
        } catch (Exception e) {
            return -1;
        }
    }

    public static boolean check(ProductInventorySummary productInventorySummary) {
        Integer inventory_quantity = get(productInventorySummary);
        return inventory_quantity.intValue() > 0;
    }

    public static boolean check(ProductInventoryService.InventoryUniqueItem inventoryUniqueItem) {
        Integer inventory_quantity = get(inventoryUniqueItem.getProductInventorySummary());
        return inventory_quantity.intValue() >= Math.abs(inventoryUniqueItem.getLock_num().intValue());
    }

    private static Integer loadInventoryQuantityFromDb(ProductInventorySummary productInventorySummary) {
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.setSqlSelect("inventory_quantity");
        if (ProductTypeCacheHandler.isCrossBorder(productInventorySummary.getProduct_type_id())) {
            wrapper.eq("id", productInventorySummary.getProduct_id());
            CrossBorderProductMapper crossBorderProductMapper = SpringContextUtil.getBean("crossBorderProductMapper", CrossBorderProductMapper.class);
            List<CrossBorderProduct> list = crossBorderProductMapper.selectList(wrapper);
            if (UtilValidate.isEmpty(list)) {
                return 0;
            }
            CrossBorderProduct crossBorderProduct = SqlHelper.getObject(list);
            return crossBorderProduct.getInventory_quantity() == null || crossBorderProduct.getInventory_quantity() <= 0 ? 0 : crossBorderProduct.getInventory_quantity();
        }
        wrapper.eq("product_id", productInventorySummary.getProduct_id());
        wrapper.eq("party_id", productInventorySummary.getParty_id());
        PartyProductService partyProductService = SpringContextUtil.getBean("partyProductService", PartyProductService.class);
        PartyProduct partyProduct = partyProductService.selectOne(wrapper);
        return partyProduct == null || partyProduct.getInventory_quantity() == null || partyProduct.getInventory_quantity().intValue() <= 0 ? 0 : partyProduct.getInventory_quantity();
    }
}
