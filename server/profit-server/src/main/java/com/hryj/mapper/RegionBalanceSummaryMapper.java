package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hryj.entity.bo.profit.RegionBalanceSummary;
import com.hryj.entity.vo.profit.request.RegionProfitRequestVO;
import com.hryj.entity.vo.profit.response.RegionBalanceVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 李道云
 * @className: RegionBalanceSummaryMapper
 * @description: 区域公司结算汇总Mapper
 * @create 2018/7/11 15:00
 **/
@Component
public interface RegionBalanceSummaryMapper extends BaseMapper<RegionBalanceSummary> {

    /**
     * @author 李道云
     * @methodName: searchRegionProfitList
     * @methodDesc: 分页查询区域分润列表
     * @description:
     * @param: [regionProfitRequestVO, page]
     * @return java.util.List<com.hryj.entity.vo.profit.response.RegionBalanceVO>
     * @create 2018-07-13 16:17
     **/
    List<RegionBalanceVO> searchRegionProfitList(RegionProfitRequestVO regionProfitRequestVO, Page page);

    /**
     * @author 李道云
     * @methodName: updateStoreSetupStatus
     * @methodDesc: 更新区域公司结算汇总表的门店设置状态
     * @description:
     * @param: [balance_month, region_id]
     * @return void
     * @create 2018-07-19 22:24
     **/
    void updateStoreSetupStatus(@Param("balance_month") String balance_month, @Param("region_id") Long region_id);

}
