package com.hryj.controller;

import com.hryj.common.Result;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.promotion.activity.request.AdvertisingPositionIdRequestVO;
import com.hryj.entity.vo.promotion.activity.request.AdvertisingPositionScopeIdListRequestVO;
import com.hryj.entity.vo.promotion.activity.request.AdvertisingPositionScopeIdRequestVO;
import com.hryj.entity.vo.promotion.activity.request.PromotionActivityDetailRequestVO;
import com.hryj.entity.vo.promotion.activity.response.PromotionActivityDataResponseVO;
import com.hryj.entity.vo.promotion.advertisingposition.request.AdvertisingPositionDetailRequestVO;
import com.hryj.entity.vo.promotion.advertisingposition.request.AdvertisingPositionRequestVO;
import com.hryj.entity.vo.promotion.advertisingposition.request.SearchAdvertisingPartyDataRequestVO;
import com.hryj.entity.vo.promotion.advertisingposition.request.SearchAdvertisingPositionRequestVO;
import com.hryj.entity.vo.promotion.advertisingposition.response.AdvertisingPositionDataResponseVO;
import com.hryj.entity.vo.promotion.advertisingposition.response.AdvertisingPositionItemResponseVO;
import com.hryj.entity.vo.promotion.advertisingposition.response.AdvertisingPositionJoinPartyDataResponseVO;
import com.hryj.entity.vo.promotion.advertisingposition.response.JoinPartyAdvertisingItemResponseVO;
import com.hryj.service.AdvertisingPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 王光银
 * @className: AdvertisingController
 * @description: 广告管理
 * @create 2018/6/27 0027 21:43
 **/
@RestController
@RequestMapping("/advertisingMgr")
public class AdvertisingController {

    @Autowired
    private AdvertisingPositionService advertisingPositionService;

    /**
     * @author 王光银
     * @methodName: searchAdvertisingPositionPage
     * @methodDesc: 分页查询广告位
     * @description:
     * @param: [searchAdvertisingPositionRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.promotion.advertisingposition.response.AdvertisingPositionItemResponseVO>>
     * @create 2018-06-30 9:23
     **/
    @PostMapping("/searchAdvertisingPositionPage")
    public Result<PageResponseVO<AdvertisingPositionItemResponseVO>> searchAdvertisingPositionPage(@RequestBody SearchAdvertisingPositionRequestVO searchAdvertisingPositionRequestVO) {
        return advertisingPositionService.searchAdvertisingPositionPage(searchAdvertisingPositionRequestVO);
    }

    /**
     * @author 汪豪
     * @methodName: getAdvertisingPosition
     * @methodDesc:  根据广告ID返回广告详细信息
     * @description: include_party是否返回参与门店或仓库，true返回，false不返回， include_jump_config是否返回跳转配置信息：true返回，false不返回
     * @param: [advertisingPositionDetailRequestVO]
     * @return Result<AdvertisingPositionDataResponseVO>
     * @create 2018-07-05 21:23
     **/
    @PostMapping("/getAdvertisingPosition")
    public Result<AdvertisingPositionDataResponseVO> getAdvertisingPosition(@RequestBody AdvertisingPositionDetailRequestVO advertisingPositionDetailRequestVO) {
        return advertisingPositionService.getAdvertisingPosition(advertisingPositionDetailRequestVO);
    }

    /**
     * @author 汪豪
     * @methodName: searchAdvertisingJoinPartyData
     * @methodDesc: 查询广告位参与门店或仓库信息
     * @description:
     * @param: [searchAdvertisingPartyDataRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.promotion.advertisingposition.response.AdvertisingPositionJoinPartyDataResponseVO>
     * @create 2018-07-17 10:11
     **/
    @PostMapping("/searchAdvertisingJoinPartyData")
    public Result<AdvertisingPositionJoinPartyDataResponseVO> searchAdvertisingJoinPartyData(@RequestBody SearchAdvertisingPartyDataRequestVO searchAdvertisingPartyDataRequestVO){
        return advertisingPositionService.searchAdvertisingJoinPartyData(searchAdvertisingPartyDataRequestVO);
    }
    /**
     * @author 王光银
     * @methodName: saveCreateAdvertisingPosition
     * @methodDesc: 保存创建一个新的广告位
     * @description: 新增的广告位状态为停用， 必须审核通过后才启用
     * @param: [advertisingPositionRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-27 22:56
     **/
    @PostMapping("/saveCreateAdvertisingPosition")
    public Result saveCreateAdvertisingPosition(@RequestBody AdvertisingPositionRequestVO advertisingPositionRequestVO) {
        return advertisingPositionService.saveCreateAdvertisingPosition(advertisingPositionRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: updateAdvertisingPosition
     * @methodDesc: 保存修改一个广告位
     * @description: 广告位的应用范围（门店与仓库）会先删除后新增
     * @param: [advertisingPositionRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-27 23:01
     **/
    @PostMapping("/updateAdvertisingPosition")
    public Result updateAdvertisingPosition(@RequestBody AdvertisingPositionRequestVO advertisingPositionRequestVO) {
        return advertisingPositionService.updateAdvertisingPosition(advertisingPositionRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: enableAdvertisingPosition
     * @methodDesc: 启用一个广告位
     * @description:
     * @param: [advertisingPositionIdRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-27 23:00
     **/
    @PostMapping("/enableAdvertisingPosition")
    public Result enableAdvertisingPosition(@RequestBody AdvertisingPositionIdRequestVO advertisingPositionIdRequestVO) {
        return advertisingPositionService.enableAdvertisingPosition(advertisingPositionIdRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: disableAdvertisingPosition
     * @methodDesc: 停用一个广告位
     * @description:
     * @param: [advertisingPositionIdRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-27 23:00
     **/
    @PostMapping("/disableAdvertisingPosition")
    public Result disableAdvertisingPosition(@RequestBody AdvertisingPositionIdRequestVO advertisingPositionIdRequestVO) {
        return advertisingPositionService.disableAdvertisingPosition(advertisingPositionIdRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: deleteOnePartyFromAdvertisingPositionScope
     * @methodDesc: 从广告位的应用范围中删除一个门店或仓库
     * @description:
     * @param: [advertisingPositionScopeIdRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-27 23:05
     **/
    @PostMapping("/deleteOnePartyFromAdvertisingPositionScope")
    public Result deleteOnePartyFromAdvertisingPositionScope(@RequestBody AdvertisingPositionScopeIdRequestVO advertisingPositionScopeIdRequestVO) {
        return advertisingPositionService.deleteOnePartyFromAdvertisingPositionScope(advertisingPositionScopeIdRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: topOneAdvertisingPositionScopeItem
     * @methodDesc: 置顶一个广告位的应用范围条目
     * @description: 就是将某个门店或仓库的这个广告位置顶
     * @param: [advertisingPositionScopeIdRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-28 9:15
     **/
    @PostMapping("/topOneAdvertisingPositionScopeItem")
    public Result topOneAdvertisingPositionScopeItem(@RequestBody AdvertisingPositionScopeIdRequestVO advertisingPositionScopeIdRequestVO) {
        return advertisingPositionService.topOneAdvertisingPositionScopeItem(advertisingPositionScopeIdRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: untopOneAdvertisingPositionScopeItem
     * @methodDesc: 取消置顶一个广告位的应用范围条目
     * @description: 就是将某个门店或仓库的这个广告位取消置顶
     * @param: [advertisingPositionScopeIdRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-28 9:16
     **/
    @PostMapping("/untopOneAdvertisingPositionScopeItem")
    public Result untopOneAdvertisingPositionScopeItem(@RequestBody AdvertisingPositionScopeIdRequestVO advertisingPositionScopeIdRequestVO) {
        return advertisingPositionService.untopOneAdvertisingPositionScopeItem(advertisingPositionScopeIdRequestVO);
    }

    /**
     * @author 汪豪
     * @methodName: topManyAdvertisingPositionScopeItem
     * @methodDesc: 批量置顶广告位的应用范围条目
     * @description:
     * @param: [advertisingPositionScopeIdRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-28 9:16
     **/
    @PostMapping("/topManyAdvertisingPositionScopeItem")
    public Result topManyAdvertisingPositionScopeItem(@RequestBody AdvertisingPositionScopeIdListRequestVO advertisingPositionScopeIdListRequestVO){
        return advertisingPositionService.topManyAdvertisingPositionScopeItem(advertisingPositionScopeIdListRequestVO);
    }
}
