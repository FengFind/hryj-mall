package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hryj.entity.bo.profit.StoreBalanceSummary;
import com.hryj.entity.vo.profit.request.StoreProfitRequestVO;
import com.hryj.entity.vo.profit.response.StoreBalanceVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 李道云
 * @className: StoreBalanceSummaryMapper
 * @description: 门店结算汇总mapper
 * @create 2018/7/11 15:49
 **/
@Component
public interface StoreBalanceSummaryMapper extends BaseMapper<StoreBalanceSummary> {

    /**
     * @author 李道云
     * @methodName: searchStoreProfitList
     * @methodDesc: 分页查询门店分润列表
     * @description:
     * @param: [storeProfitRequestVO,page]
     * @return java.util.List<com.hryj.entity.vo.profit.response.StoreBalanceVO>
     * @create 2018-07-13 19:11
     **/
    List<StoreBalanceVO> searchStoreProfitList(StoreProfitRequestVO storeProfitRequestVO, Page page);

    /**
     * @author 李道云
     * @methodName: findNoSetNonFixedCostStoreList
     * @methodDesc: 查询未设置非固定成本的门店列表
     * @description:
     * @param: [balance_month, region_id]
     * @return java.util.List<com.hryj.entity.bo.profit.StoreBalanceSummary>
     * @create 2018-07-19 22:11
     **/
    List<StoreBalanceSummary> findNoSetNonFixedCostStoreList(@Param("balance_month") String balance_month, @Param("region_id") Long region_id);

    /**
     * @author 李道云
     * @methodName: updateReferralStatisData
     * @methodDesc: 更新推荐用户的统计数据
     * @description:
     * @param: [statis_date, store_id]
     * @return void
     * @create 2018-08-29 10:19
     **/
    void updateReferralStatisData(@Param("statis_date") String statis_date, @Param("store_id") Long store_id);
}
