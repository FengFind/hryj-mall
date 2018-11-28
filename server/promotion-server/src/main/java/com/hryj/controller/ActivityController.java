package com.hryj.controller;

import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.promotion.activity.request.*;
import com.hryj.entity.vo.promotion.activity.response.*;
import com.hryj.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author 王光银
 * @className: ActivityController
 * @description: 活动管理
 * @create 2018/6/27 0027 21:43
 **/
@RestController
@RequestMapping("/promotionActivityMgr")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    /**
     * @author 王光银
     * @methodName: searchPromotionActivityPage
     * @methodDesc: 分页查询促销活动
     * @description: 该接口同时适用于活动管理与活动审核管理使用
     * @param: [searchPromotionActivityRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.promotion.activity.response.SearchPromotionActivityItemResponseVO>>
     * @create 2018-06-30 9:20
     **/
    @PostMapping("/searchActivityPage")
    public Result<PageResponseVO<SearchPromotionActivityItemResponseVO>> searchPromotionActivityPage(@RequestBody SearchPromotionActivityRequestVO searchPromotionActivityRequestVO) {
        return activityService.searchPromotionActivityPage(searchPromotionActivityRequestVO);
    }

    /**
     * @author 汪豪
     * @methodName: getPromotionActivity
     * @methodDesc:  根据活动ID返回活动详细信息
     * @description: include_party是否返回参与门店或仓库，true返回，false不返回， include_product是否返回参与产品信息，true返回，false不返回,include_audit_record是否返回审核记录，true返回，false不返回
     * @param: [romotionActivityDetailRequestVo]
     * @return com.hryj.common.Result<com.hryj.entity.vo.promotion.activity.response.PromotionActivityDataResponseVO>
     * @create 2018-07-05 21:23
     **/
    @PostMapping("/getActivity")
    public Result<PromotionActivityDataResponseVO> getPromotionActivity(@RequestBody PromotionActivityDetailRequestVO promotionActivityDetailRequestVO) {
        return activityService.getPromotionActivity(promotionActivityDetailRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: searchPromotionActivityJoinPartyData
     * @methodDesc: 查询活动参与门店或仓库信息
     * @description: 参与门店可能比较多，是以分页查询方式处理
     * @param: [searchActivityJoinPartyRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.promotion.activity.response.PromotionActivityJoinPartyDataResponseVO>>
     * @create 2018-06-30 9:21
     **/
    @PostMapping("/searchActivityJoinPartyData")
    public Result<PageResponseVO<PromotionActivityJoinPartyDataResponseVO>> searchPromotionActivityJoinPartyData(@RequestBody SearchActivityJoinPartyRequestVO searchActivityJoinPartyRequestVO) {
        return activityService.searchPromotionActivityJoinPartyData(searchActivityJoinPartyRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: searchPromotionActivityJoinProductData
     * @methodDesc: 查询活动参与产品信息
     * @description: 参与产品可能比较多，是以分页查询方式处理
     * @param: [searchActivityJoinProductRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.promotion.activity.response.JoinProductItemResponseVO>>
     * @create 2018-06-30 9:22
     **/
    @PostMapping("/searchActivityJoinProductData")
    public Result<PageResponseVO<JoinProductItemResponseVO>> searchPromotionActivityJoinProductData(@RequestBody SearchActivityJoinProductRequestVO searchActivityJoinProductRequestVO) {
        return activityService.searchPromotionActivityJoinProductData(searchActivityJoinProductRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: findActivityAuditRecord
     * @methodDesc: 查询活动的审核记录信息
     * @description:
     * @param: [onlyActivityIdRequestVo]
     * @return com.hryj.common.Result<com.hryj.entity.vo.promotion.activity.response.PromotionActivityAuditRecordResponseVO>
     * @create 2018-06-28 17:08
     **/
    @PostMapping("/findActivityAuditRecord")
    public Result<ListResponseVO<PromotionActivityAuditRecordResponseVO>> findPromotionActivityAuditRecord(@RequestBody OnlyActivityIdRequestVO onlyActivityIdRequestVo) {
        return activityService.findPromotionActivityAuditRecord(onlyActivityIdRequestVo);
    }

    /**
     * @author 王光银
     * @methodName: saveCreateActivity
     * @methodDesc: 保存创建一个新的促销活动
     * @description:
     * @param: [promotionActitivyRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-28 11:36
     **/
    @PostMapping("/saveCreateActivity")
    public Result saveCreatePromotionActivity(@RequestBody PromotionActivityRequestVO promotionActitivyRequestVO) {
        return activityService.saveCreatePromotionActivity(promotionActitivyRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: updateActivity
     * @methodDesc: 保存修改一个促销活动
     * @description: 活动审核通过后不能再进行修改，活动基本信息采用更新方式，参与门店或仓库，参与商品以先删除后新增的方式处理
     * @param: [promotionActitivyRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-28 11:37
     **/
    @PostMapping("/updateActivity")
    public Result updatePromotionActivity(@RequestBody PromotionActivityRequestVO promotionActitivyRequestVO) {
        return activityService.updatePromotionActivity(promotionActitivyRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: updateActivityBase
     * @methodDesc: 修改活动的基本信息
     * @description: 活动审核通过后不能再进行修改
     * @param: [promotionActivityBaseRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-28 11:44
     **/
    @PostMapping("/updateActivityBase")
    public Result updatePromotionActivityBase(@RequestBody PromotionActivityBaseRequestVO promotionActivityBaseRequestVO) {
        return activityService.updatePromotionActivityBase(promotionActivityBaseRequestVO);
    }

    /**
     * @author 汪豪
     * @methodName: deleteOnePartyFormScope
     * @methodDesc: 删除一个指定的活动应用范围（门店或仓库）
     * @description: 活动审核通过后不能删除
     * @param: [activityScopeItemIdRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-28 11:47
     **/
    @PostMapping("/deleteOnePartyFormScope")
    public Result deleteOnePartyFormScope(@RequestBody ActivityScopeItemIdRequestVO activityScopeItemIdRequestVO) {
        return activityService.deleteOnePartyFormScope(activityScopeItemIdRequestVO);
    }

    /**
     * @author 汪豪
     * @methodName: deleteOneProductFormScope
     * @methodDesc: 删除一个指定的活动参与商品
     * @description: 活动审核通过后不能删除
     * @param: [activityProductItemIdRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-28 11:52
     **/
    @PostMapping("/deleteOneProductFormScope")
    public Result deleteOneProductFormScope(@RequestBody PromotionActivityAppendProdRequestVO promotionActivityAppendProdRequestVO) {
        return activityService.deleteOneProductFormScope(promotionActivityAppendProdRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: appendManyProductToActivity
     * @methodDesc: 向活动追加商品
     * @description: 活动审核通过后不能追加商品
     * @param: [promotionActivityAppendProdRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-28 11:56
     **/
    @PostMapping("/appendManyProductToActivity")
    public Result appendManyProductToActivity(@RequestBody PromotionActivityAppendProdRequestVO promotionActivityAppendProdRequestVO) {
        return activityService.appendManyProductToActivity(promotionActivityAppendProdRequestVO);
    }

    /**
     * @author 汪豪
     * @methodName: upMoveOneJoinProduct
     * @methodDesc: 上移一个产品在活动中的排序位置
     * @description:
     * @param: [activityProductItemIdRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-07-05 21:40
     **/
    @PostMapping("/upMoveOneJoinProduct")
    public Result upMoveOneJoinProduct(@RequestBody ActivityProductItemIdRequestVO activityProductItemIdRequestVO) {
        return activityService.upMoveOneJoinProduct(activityProductItemIdRequestVO);
    }

    /**
     * @author 汪豪
     * @methodName: downMoveJoinProduct
     * @methodDesc: 下移一个产品在活动中的排序位置
     * @description:
     * @param: [activityProductItemIdRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-07-05 21:40
     **/
    @PostMapping("/downMoveJoinProduct")
    public Result downMoveJoinProduct(@RequestBody ActivityProductItemIdRequestVO activityProductItemIdRequestVO) {
        return activityService.downMoveJoinProduct(activityProductItemIdRequestVO);
    }

    /**
     * @author 汪豪
     * @methodName: activityJoinProductDetail
     * @methodDesc: 验证一个在活动进行中的商品信息
     * @description:
     * @param: [activityIdProductIdRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.promotion.activity.response.ActivityInProgressProductItemResponseVO>
     * @create 2018-07-10 13:53
     **/
    @PostMapping("/activityJoinProductDetail")
    public Result<ListResponseVO<ActivityInProgressProductItemResponseVO>> activityJoinProductDetail(@RequestBody List<ActivityIdProductIdRequestVO> activityIdProductIdRequestVO){
        return activityService.activityJoinProductDetail(activityIdProductIdRequestVO);
    }

    /**
     * @author 汪豪
     * @methodName: disablePomotionActivity
     * @methodDesc: 停用一个促销活动
     * @description:
     * @param: [onlyActivityIdRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-07-10 13:53
     **/
    @PostMapping("/disablePomotionActivity")
    public Result disablePomotionActivity(@RequestBody OnlyActivityIdRequestVO onlyActivityIdRequestVO){
        return activityService.disablePomotionActivity(onlyActivityIdRequestVO);
    }

    /**
     * @author 汪豪
     * @methodName: enablePomotionActivity
     * @methodDesc: 启用一个促销活动
     * @description:
     * @param: [onlyActivityIdRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-07-10 13:53
     **/
    @PostMapping("/enablePomotionActivity")
    public Result enablePomotionActivity(@RequestBody OnlyActivityIdRequestVO onlyActivityIdRequestVO){
        return activityService.enablePomotionActivity(onlyActivityIdRequestVO);
    }

    /**
     * @author 汪豪
     * @methodName: orderProductJoinActivityInfo
     * @methodDesc: 查询订单商品参与活动信息
     * @description: 内部接口
     * @param: [partyProductActivityRequestVO]
     * @return
     * @create 2018-08-16 10:24
     **/
    @PostMapping("/orderProductJoinActivityInfo")
    public Result<List<OrderActivityInfoResponseVO>> orderProductJoinActivityInfo(@RequestBody List<PartyProductActivityRequestVO> partyProductActivityRequestVO){
        return activityService.orderProductJoinActivityInfo(partyProductActivityRequestVO);
    }

    /**
     * @author 汪豪
     * @methodName: getActivityInfoById
     * @methodDesc: 根据活动id查询活动信息
     * @description: 内部接口
     * @param: [partyProductActivityRequestVO]
     * @return com.hryj.common.Result<java.util.List<com.hryj.entity.vo.promotion.activity.response.OrderActivityInfoResponseVO>>
     * @create 2018-08-21 14:29
     **/
    @PostMapping("/getActivityInfoById")
    public Result<List<OrderActivityInfoResponseVO>> getActivityInfoById(@RequestBody List<OnlyActivityIdRequestVO> onlyActivityIdRequestVOS){
        return activityService.getActivityInfoById(onlyActivityIdRequestVOS);
    }
}
