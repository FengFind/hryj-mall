package com.hryj.controller;

import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.product.partyprod.request.*;
import com.hryj.entity.vo.product.partyprod.response.PartyIntersectionProductItem;
import com.hryj.entity.vo.product.partyprod.response.PartyProductDataResponseVO;
import com.hryj.entity.vo.product.partyprod.response.PartyProductListItemResponseVO;
import com.hryj.entity.vo.product.partyprod.response.PartyProductStatisticsItem;
import com.hryj.exception.ServerException;
import com.hryj.service.PartyProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 王光银
 * @className: PartyProductController
 * @description: 门店或仓库的商品管理接口
 * @create 2018/6/27 0027 16:38
 **/
@RestController
@RequestMapping("/partyProductMgr")
public class PartyProductController {

    @Autowired
    private PartyProductService partyProductService;

    /**
     * @author 王光银
     * @methodName: searchManyPartyPolymerizationProduct
     * @methodDesc: 分页查询多个门店或仓库的聚合商品
     * @description: 支持并集查询和交集查询两种方式
     * @param: [partyPolymerizationProductSearchRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.product.partyprod.response.PartyIntersectionProductItem>>
     * @create 2018-06-30 9:19
     **/
    @PostMapping("/searchManyPartyPolymerizationProduct")
    public Result<PageResponseVO<PartyIntersectionProductItem>> searchManyPartyPolymerizationProduct(
            @RequestBody SearchPartyPolymerizationProductRequestVO partyPolymerizationProductSearchRequestVO) throws ServerException {
        return partyProductService.searchManyPartyPolymerizationProduct(partyPolymerizationProductSearchRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: searchPartyProduct
     * @methodDesc: 返回指定门店或仓库的商品数据和基础信息
     * @description:
     * @param: [partyProductDataRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.product.partyprod.response.PartyProductDataResponseVO>
     * @create 2018-06-27 20:23
     **/
    @PostMapping("/searchPartyProduct")
    public Result<PartyProductDataResponseVO> searchPartyProduct(
            @RequestBody PartyProductDataRequestVO partyProductDataRequestVO) throws ServerException {
        return partyProductService.searchPartyProduct(partyProductDataRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: searchPartySelectableProduct
     * @methodDesc: 加载指定门店或仓库的可添加销售的商品列表数据
     * @description:
     * @param: [partySelectableProdsPoolRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.product.partyprod.response.PartySelectableProdItemResponseVO>>
     * @create 2018-06-30 9:18
     **/
    @PostMapping("/searchPartySelectableProduct")
    public Result<PageResponseVO<PartyProductListItemResponseVO>> searchPartySelectableProduct(
            @RequestBody PartySelectableProdsPoolRequestVO partySelectableProdsPoolRequestVO) throws ServerException {
        return partyProductService.searchPartySelectableProduct(partySelectableProdsPoolRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: appendProductToPartySaleablePool
     * @methodDesc:  添加指定门店或仓库的销售商品
     * @description:
     * @param: [partySaleableProdPoolAppendRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-27 20:
     **/
    @PostMapping("/appendProductToPartySaleablePool")
    public Result appendProductToPartySaleablePool(
            @RequestBody PartySaleableProdPoolAppendRequestVO partySaleableProdPoolAppendRequestVO) throws ServerException {
        return partyProductService.appendProductToPartySaleablePool(partySaleableProdPoolAppendRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: updateManyPartyProduct
     * @methodDesc: 批量更新维护门店或仓库的商品销售数据（销售价格和库存数量）
     * @description: 门店不能维护销售价格，即使传参数也不会处理
     * @param: [partyUpdatePriceInventoryQuantityRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-27 20:22
     **/
    @PostMapping("/updateManyPartyProduct")
    public Result updateManyPartyProduct(
            @RequestBody PartyUpdatePriceInventoryQuantityRequestVO partyUpdatePriceInventoryQuantityRequestVO) throws ServerException {
        return partyProductService.updateManyPartyProduct(partyUpdatePriceInventoryQuantityRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: updateOnePartyProduct
     * @methodDesc: 更新维护一个门店或仓库的销售商品（销售价格和库存量）
     * @description: 门店没有设置销售价格的权限，即使传递也不会进行处理
     * @param: [partyUpdatePriceInventoryQuantityRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-27 20:21
     **/
    @PostMapping("/updateOnePartyProduct")
    public Result updateOnePartyProduct(
            @RequestBody PartyUpdatePriceInventoryQuantityItemRequestVO partyUpdatePriceInventoryQuantityItemRequestVO) throws ServerException {
        return partyProductService.updateOnePartyProduct(partyUpdatePriceInventoryQuantityItemRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: upPartyProduct
     * @methodDesc: 上架指定的仓库或门店商品
     * @description:
     * @param: [idRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-27 20:21
     **/
    @PostMapping("/upPartyProduct")
    public Result upPartyProduct(@RequestBody IdRequestVO idRequestVO) throws ServerException {
        return partyProductService.upPartyProduct(idRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: downPartyProduct
     * @methodDesc: 下架指定仓库或门店商品
     * @description:
     * @param: [idRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-27 20:20
     **/
    @PostMapping("/downPartyProduct")
    public Result downPartyProduct(@RequestBody IdRequestVO idRequestVO) throws ServerException {
        return partyProductService.downPartyProduct(idRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: partyProdStatistics
     * @methodDesc: 门店或仓库商品简单统计接口
     * @description:
     * @param: [partyProductStatisticsRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.product.partyprod.response.PartyProductStatisticsItem>>
     * @create 2018-07-05 21:13
     **/
    @PostMapping("/partyProdSimpleStatistics")
    public Result<ListResponseVO<PartyProductStatisticsItem>> partyProdSimpleStatistics(@RequestBody PartyProductStatisticsRequestVO partyProductStatisticsRequestVO) {
        return partyProductService.partyProdSimpleStatistics(partyProductStatisticsRequestVO);
    }
}
