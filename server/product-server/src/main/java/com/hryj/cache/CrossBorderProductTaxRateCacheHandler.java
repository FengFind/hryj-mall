package com.hryj.cache;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hryj.entity.bo.product.TaxRate;
import com.hryj.mapper.TaxRateMapper;
import com.hryj.utils.SpringContextUtil;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 王光银
 * @className: CrossBorderProductTaxRateCacheHandler
 * @description: 跨境商品税率基础表缓存工具类,  税率基础数据不容易发生改变，数据缓存在应用中
 *               如果税率确实发生了变化，需要在合适的时候修改数据库配置，然后重启所有节点的商品管理服务应用
 * @create 2018/9/12 0012 15:42
 **/
@Slf4j
public class CrossBorderProductTaxRateCacheHandler {

    private static final Map<String, TaxRate> CROSS_BORDER_PROD_TAX_RATE_CACHE_MAP = new HashMap<>(15000);

    public static void refreshCache() {
        if (UtilValidate.isNotEmpty(CROSS_BORDER_PROD_TAX_RATE_CACHE_MAP)) {
            CROSS_BORDER_PROD_TAX_RATE_CACHE_MAP.clear();
        }

        cacheInit();
    }

    public static void cacheInit() {
        TaxRateMapper taxRateMapper = SpringContextUtil.getBean("taxRateMapper", TaxRateMapper.class);
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.orderBy("hs_code", true);
        List<TaxRate> data_list = taxRateMapper.selectList(wrapper);
        if (UtilValidate.isEmpty(data_list)) {
            return;
        }
        for (TaxRate item : data_list) {
            CROSS_BORDER_PROD_TAX_RATE_CACHE_MAP.put(item.getHs_code(), item);
        }

        log.info("------- 跨境商品基础税率配置数据缓存任务执行完成...");
    }

    public static TaxRate getTaxRate(String hs_code) {
        if (UtilValidate.isEmpty(hs_code)) {
            return null;
        }

        //为了防止外部程序修改缓存对象的值，将对象先序列化再反序列化返回
        TaxRate item = CROSS_BORDER_PROD_TAX_RATE_CACHE_MAP.get(hs_code);
        if (item == null) {
            return null;
        }

        try {
            String json = JSON.toJSONString(item);
            TaxRate taxRate = JSON.toJavaObject(JSON.parseObject(json), TaxRate.class);
            return taxRate;
        } catch (Exception e) {
            log.error("从缓存获取跨境商品税率配置数据反序列化失败:", e);
            return item;
        }
    }
}
