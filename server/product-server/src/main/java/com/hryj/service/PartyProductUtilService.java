package com.hryj.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hryj.cache.ProductTypeCacheHandler;
import com.hryj.cache.RedisLock;
import com.hryj.entity.bo.product.PartyProduct;
import com.hryj.entity.bo.product.ProductInfo;
import com.hryj.mapper.PartyProductMapper;
import com.hryj.service.util.ProductUtil;
import com.hryj.service.util.RedisCacheUtil;
import com.hryj.utils.SpringContextUtil;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author 王光银
 * @className: PartyProductUtilService
 * @description:
 * @create 2018/8/16 0016 11:25
 **/
@Service
@Slf4j
public class PartyProductUtilService {

    @Autowired
    private PartyProductMapper partyProductMapper;

    @Autowired
    private RedisLock redisLock;

    /**
     * 返回门店商品是否是跨境商品
     * @param party_product_id
     * @return
     */
    public static boolean isCrossBorderProduct(Long party_product_id) {
        if (party_product_id == null || party_product_id <= 0L) {
            return false;
        }
        PartyProductService partyProductService = SpringContextUtil.getBean("partyProductService", PartyProductService.class);
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("id", party_product_id);
        wrapper.setSqlSelect("product_id");
        PartyProduct partyProduct = partyProductService.selectOne(wrapper);
        if (partyProduct == null || partyProduct.getProduct_id() == null || partyProduct.getProduct_id() <= 0L) {
            return false;
        }
        wrapper = new EntityWrapper();
        wrapper.eq("id", partyProduct.getProduct_id());
        wrapper.setSqlSelect("product_type_id");
        ProductInfo info = partyProductService.getProductService().selectOne(wrapper);
        if (info == null || UtilValidate.isEmpty(info.getProduct_type_id())) {
            return false;
        }
        return ProductTypeCacheHandler.isCrossBorder(info.getProduct_type_id());
    }


    public Map<Long, Integer> statisticsPartyProductNum(Collection<Long> party_ids) {
        if (UtilValidate.isEmpty(party_ids)) {
            return null;
        }
        Map<Long, Integer> statistics_map = new HashMap<>(party_ids.size());

        Collection<Long> need_to_reload = new HashSet<>(party_ids.size());

        for (Long party_id : party_ids) {
            Integer quantity = RedisCacheUtil.PartyProductStatisticsCacheUtil.getCacheData(party_id);
            if (quantity == null) {
                need_to_reload.add(party_id);
                continue;
            }
            try {
                statistics_map.put(party_id, quantity);
            } catch (NumberFormatException e) {
                need_to_reload.add(party_id);
            }
        }
        if (UtilValidate.isNotEmpty(need_to_reload)) {
            Date curr = new Date();
            Map<String, Object> params_map = new HashMap<>(6);
            params_map.put("forbid_sale_check", true);
            params_map.put("inventory_check", true);
            params_map.put("up_down_status", ProductUtil.UP_STATUS);
            params_map.put("introduction_date", curr);
            params_map.put("sales_end_date", curr);

            Iterator<Long> it = need_to_reload.iterator();
            while (it.hasNext()) {
                Long party_id = it.next();

                while (true) {
                    /**
                     * 门店商品的统计查询进行同步处理
                     */
                    boolean lock_result = redisLock.lock(RedisCacheUtil.RedisCacheKey.PARTY_PRODUCT_QUANTITY_STATISTICS_CACHE_GROUP.get(), party_id.toString(), 2);
                    if (!lock_result) {
                        Integer quantity = RedisCacheUtil.PartyProductStatisticsCacheUtil.getCacheData(party_id);
                        if (quantity == null) {
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                log.error("Thread.sleep failed:", e);
                            }
                            continue;
                        }
                        statistics_map.put(party_id, quantity);
                        it.remove();
                        break;
                    }

                    //加载门店商品的统计数据
                    params_map.put("party_id", party_id);
                    List<PartyProduct> statistics_list = partyProductMapper.statisticsPartyProd(params_map);
                    if (UtilValidate.isNotEmpty(statistics_list)) {
                        RedisCacheUtil.PartyProductStatisticsCacheUtil.setCacheData(statistics_list.get(0).getParty_id(), statistics_list.get(0).getInventory_quantity());
                        statistics_map.put(statistics_list.get(0).getParty_id(), statistics_list.get(0).getInventory_quantity());
                    } else {
                        RedisCacheUtil.PartyProductStatisticsCacheUtil.setCacheData(party_id, 0);
                        statistics_map.put(party_id, 0);
                    }

                    //释放锁
                    redisLock.unLock(RedisCacheUtil.RedisCacheKey.PARTY_PRODUCT_QUANTITY_STATISTICS_CACHE_GROUP.get(), party_id.toString());
                    break;
                }
            }
        }

        for (Long party_id : party_ids) {
            if (!statistics_map.containsKey(party_id)) {
                statistics_map.put(party_id, 0);
            }
        }

        return statistics_map;
    }
}
