package com.hryj.controller;

import com.hryj.common.Result;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.profit.request.*;
import com.hryj.entity.vo.profit.response.*;
import com.hryj.exception.GlobalException;
import com.hryj.service.ProfitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 李道云
 * @className: ProfitAdminController
 * @description: 分润模块（运营后台）
 * @create 2018/7/9 20:17
 **/
@Slf4j
@RestController
@RequestMapping("/profitAdmin")
public class ProfitAdminController {

    @Autowired
    private ProfitService profitService;

    /**
     * @author 李道云
     * @methodName: searchRegionProfitList
     * @methodDesc: 分页查询区域分润列表
     * @description:
     * @param: [regionProfitRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.profit.response.RegionBalanceVO>>
     * @create 2018-07-09 20:31
     **/
    @PostMapping("/searchRegionProfitList")
    public Result<PageResponseVO<RegionBalanceVO>> searchRegionProfitList(@RequestBody RegionProfitRequestVO regionProfitRequestVO) throws GlobalException {
        return profitService.searchRegionProfitList(regionProfitRequestVO);
    }

    /**
     * @author 李道云
     * @methodName: searchRegionProfitDetailList
     * @methodDesc: 分页查询区域分润明细列表
     * @description:
     * @param: [regionProfitDetailRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.profit.response.RegionBalanceDetailVO>>
     * @create 2018-07-09 20:52
     **/
    @PostMapping("/searchRegionProfitDetailList")
    public Result<PageResponseVO<RegionBalanceDetailVO>> searchRegionProfitDetailList(@RequestBody RegionProfitDetailRequestVO regionProfitDetailRequestVO) throws GlobalException {
        return profitService.searchRegionProfitDetailList(regionProfitDetailRequestVO);
    }

    /**
     * @author 李道云
     * @methodName: updateRegionNonFixedCost
     * @methodDesc: 更新区域公司非固定成本
     * @description:
     * @param: [nonFixedCostSetRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-07-09 21:05
     **/
    @PostMapping("/updateRegionNonFixedCost")
    public Result updateRegionNonFixedCost(@RequestBody NonFixedCostSetRequestVO nonFixedCostSetRequestVO) throws GlobalException {
        return profitService.updateRegionNonFixedCost(nonFixedCostSetRequestVO);
    }

    /**
     * @author 李道云
     * @methodName: searchStoreProfitList
     * @methodDesc: 分页查询门店分润列表
     * @description:
     * @param: [storeProfitRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.profit.response.StoreBalanceVO>>
     * @create 2018-07-09 21:15
     **/
    @PostMapping("/searchStoreProfitList")
    public Result<PageResponseVO<StoreBalanceVO>> searchStoreProfitList(@RequestBody StoreProfitRequestVO storeProfitRequestVO) throws GlobalException {
        return profitService.searchStoreProfitList(storeProfitRequestVO);
    }

    /**
     * @author 李道云
     * @methodName: findStoreProfitDetailList
     * @methodDesc: 查询门店分润明细数据
     * @description:
     * @param: [storeProfitDetailRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.profit.response.StoreProfitDetailResponseVO>
     * @create 2018-07-09 22:43
     **/
    @PostMapping("/findStoreProfitDetailList")
    public Result<StoreProfitDetailResponseVO> findStoreProfitDetailList(@RequestBody StoreProfitDetailRequestVO storeProfitDetailRequestVO) throws GlobalException {
        return profitService.findStoreProfitDetailList(storeProfitDetailRequestVO);
    }

    /**
     * @author 李道云
     * @methodName: updateStoreNonFixedCost
     * @methodDesc: 更新门店非固定成本
     * @description:
     * @param: [nonFixedCostSetRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-07-09 21:17
     **/
    @PostMapping("/updateStoreNonFixedCost")
    public Result updateStoreNonFixedCost(@RequestBody NonFixedCostSetRequestVO nonFixedCostSetRequestVO) throws GlobalException {
        return profitService.updateStoreNonFixedCost(nonFixedCostSetRequestVO);
    }

    /**
     * @author 李道云
     * @methodName: searchStoreManagerProfitList
     * @methodDesc: 分页查询店长分润列表
     * @description:
     * @param: [storeManagerProfitRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.profit.response.StoreManagerProfitVO>>
     * @create 2018-07-09 22:46
     **/
    @PostMapping("/searchStoreManagerProfitList")
    public Result<PageResponseVO<StoreManagerProfitVO>> searchStoreManagerProfitList(@RequestBody StoreManagerProfitRequestVO storeManagerProfitRequestVO) throws GlobalException {
        return profitService.searchStoreManagerProfitList(storeManagerProfitRequestVO);
    }
}
