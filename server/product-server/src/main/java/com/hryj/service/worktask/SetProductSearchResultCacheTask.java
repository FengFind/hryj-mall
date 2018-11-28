package com.hryj.service.worktask;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.hryj.entity.vo.product.response.app.AppProdListItemResponseVO;
import com.hryj.service.util.RedisCacheUtil;
import com.hryj.service.util.cacheutil.PartyActivityMonitorCacheUtil;
import com.hryj.utils.UtilValidate;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author 王光银
 * @className: SetProductSearchResultCacheTask
 * @description: 商品搜索结果缓存任务
 * @create 2018/8/27 0027 9:27
 **/
public class SetProductSearchResultCacheTask implements Runnable {

    private Long party_id;
    private String cache_key;
    private List<AppProdListItemResponseVO> data_list;

    public SetProductSearchResultCacheTask(Long party_id, String cache_key, List<AppProdListItemResponseVO> data_list) {
        this.party_id = party_id;
        this.cache_key = cache_key;
        this.data_list = UtilValidate.isEmpty(data_list) ? new LinkedList<>() : data_list;
    }

    @Override
    public void run() {
        /**
         * 1、从缓存中获取到影响当前门店最近的活动日期
         * 2、利用得到的日期设置缓存数据
         */

        //步骤1
        Date earliest_date_from_cache = PartyActivityMonitorCacheUtil.getEarliestAfterNow(this.party_id);

        if (earliest_date_from_cache != null) {
            RedisCacheUtil.ProductSearchCacheUtil.setCacheData(this.party_id, cache_key, this.data_list, DateUtil.between(earliest_date_from_cache, new Date(), DateUnit.SECOND));
        } else {
            RedisCacheUtil.ProductSearchCacheUtil.setCacheData(this.party_id, cache_key, this.data_list);
        }
    }
}
