package com.hryj.mapper;

import com.baomidou.mybatisplus.plugins.Page;
import com.hryj.entity.vo.profit.request.StoreManagerProfitRequestVO;
import com.hryj.entity.vo.profit.response.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 李道云
 * @className: StaffBalanceSummaryMapper
 * @description: 员工结算汇总mapper
 * @create 2018/7/13 19:46
 **/
@Component
public interface StaffBalanceSummaryMapper {

    /**
     * @author 李道云
     * @methodName: findStaffBalanceSummaryList
     * @methodDesc: 查询店员数据列表
     * @description:
     * @param: [store_id, query_month]
     * @return java.util.List<com.hryj.entity.vo.profit.response.StaffBalanceSummaryVO>
     * @create 2018-07-13 20:12
     **/
    List<StaffBalanceSummaryVO> findStaffBalanceSummaryList(@Param("store_id") Long store_id, @Param("query_month") String query_month);

    /**
     * @author 李道云
     * @methodName: findStoreCostDetailList
     * @methodDesc: 查询成本数据列表
     * @description:
     * @param: [store_id, query_month]
     * @return java.util.List<com.hryj.entity.vo.profit.response.StoreCostDetailVO>
     * @create 2018-07-13 20:12
     **/
    List<StoreCostDetailVO> findStoreCostDetailList(@Param("store_id") Long store_id, @Param("query_month") String query_month);

    /**
     * @author 李道云
     * @methodName: findStaffDistributionList
     * @methodDesc: 查询配送数据列表
     * @description:
     * @param: [store_id, query_month]
     * @return java.util.List<com.hryj.entity.vo.profit.response.StaffDistributionVO>
     * @create 2018-07-13 20:13
     **/
    List<StaffDistributionVO> findStaffDistributionList(@Param("store_id") Long store_id, @Param("query_month") String query_month);

    /**
     * @author 李道云
     * @methodName: searchStoreManagerProfitList
     * @methodDesc: 分页查询店长分润列表
     * @description:
     * @param: [storeManagerProfitRequestVO, page]
     * @return java.util.List<com.hryj.entity.vo.profit.response.StoreManagerProfitVO>
     * @create 2018-07-13 21:24
     **/
    List<StoreManagerProfitVO> searchStoreManagerProfitList(StoreManagerProfitRequestVO storeManagerProfitRequestVO, Page page);

    /**
     * @author 李道云
     * @methodName: findProfitDataDetail
     * @methodDesc: 查询分润数据明细
     * @description:
     * @param: [staff_id, query_month]
     * @return com.hryj.entity.vo.profit.response.ProfitDataDetailVO
     * @create 2018-07-14 9:34
     **/
    ProfitDataDetailVO findProfitDataDetail(@Param("staff_id") Long staff_id, @Param("query_month") String query_month);

    /**
     * @author 李道云
     * @methodName: searchProfitDataDetail
     * @methodDesc: 分页查询分润数据明细
     * @description:
     * @param: [staff_id, page]
     * @return com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.profit.response.ProfitDataDetailVO>
     * @create 2018-07-14 9:50
     **/
    List<ProfitDataDetailVO> searchProfitDataDetail(@Param("staff_id") Long staff_id, Page page);

    /**
     * @author 李道云
     * @methodName: updateReferralStatisData
     * @methodDesc: 更新推荐用户的统计数据
     * @description:
     * @param: [statis_date, staff_id]
     * @return void
     * @create 2018-08-29 10:19
     **/
    void updateReferralStatisData(@Param("statis_date") String statis_date, @Param("staff_id") Long staff_id);

}
