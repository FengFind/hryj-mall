package com.hryj.controller;

import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.inventory.request.ProductsInventoryLockRequestVO;
import com.hryj.entity.vo.inventory.request.ProductsInventoryLockRollBackRequestVO;
import com.hryj.entity.vo.inventory.response.ProductsInventoryLockResponseVO;
import com.hryj.entity.vo.product.common.request.*;
import com.hryj.entity.vo.product.common.response.*;
import com.hryj.entity.vo.product.partyprod.request.SearchPartyPolymerizationProductRequestVO;
import com.hryj.entity.vo.product.partyprod.response.PartyIntersectionProductItem;
import com.hryj.entity.vo.product.response.ProductTopSalesItemResponseVO;
import com.hryj.exception.ServerException;
import com.hryj.service.PartyProductService;
import com.hryj.service.ProductCommonService;
import com.hryj.service.inventory.ProductInventoryService;
import com.hryj.synchronizationtask.SyncCrossBorderProductInventoryTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 王光银
 * @className: ProductCommonController
 * @description: 商品对外部其他组件开放的公共接口
 * @create 2018/7/3 0003 21:21
 **/
@RestController
@RequestMapping("/productCommon")
public class ProductCommonController {

    @Autowired
    private ProductCommonService productCommonService;

    @Autowired
    private PartyProductService partyProductService;

    @Autowired
    private ProductInventoryService productInventoryService;

    @Autowired
    private SyncCrossBorderProductInventoryTask syncCrossBorderProductInventoryTask;
    /**
     * @author 王光银
     * @methodName: getProductShareContent
     * @methodDesc: 获取商品分享内容
     * @description:
     * @param: [requestVO]
     * @return
     * @create 2018-06-30 9:19
     **/
    @PostMapping("/getProductShareContent")
    public Result<ShareProductResponseVO> getProductShareContent(
            @RequestBody ShareProductRequestVO requestVO) throws ServerException {
        return partyProductService.getProductShareContent(requestVO);
    }

    /**
     * @author 王光银
     * @methodName: searchManyPartyPolymerizationProduct
     * @methodDesc: 分页查询多个门店或仓库的聚合商品
     * @description: 支持并集查询和交集查询两种方式
     * @param: [partyPolymerizationProductSearchRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.product.partyprod.response.PartyProductListItemResponseVO>>
     * @create 2018-06-30 9:19
     **/
    @PostMapping("/searchManyPartyPolymerizationProduct")
    public Result<PageResponseVO<PartyIntersectionProductItem>> searchManyPartyPolymerizationProduct(
            @RequestBody SearchPartyPolymerizationProductRequestVO partyPolymerizationProductSearchRequestVO) throws ServerException {
        return partyProductService.searchManyPartyPolymerizationProduct(partyPolymerizationProductSearchRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: productsValidate
     * @methodDesc: 商品的批量验证
     * @description: 验证失败返回编码说明:  16100:party_id对应的组织不存在,  11030: product_id对应的商品不存在, 11000: 商品下架， 11020：库存不足, 11010:活动已结束
     * @param: [productsValidateRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.product.common.response.ProductsValidateResponseVO>
     * @create 2018-07-20 9:15
     **/
    @PostMapping("/productsValidate")
    public Result<ProductsValidateResponseVO> productsValidate(
            @RequestBody ProductsValidateRequestVO productsValidateRequestVO) {
        return productCommonService.productsValidate(productsValidateRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: findTopSalesProduct
     * @methodDesc: 查询TOP销量商品
     * @description: 该接口的统计范围是在统计时刻仍然在销售的商品，已经下架的商品不管销售多高不会返回，同时参数party_id有值返回指定门店或仓库的TOP销售商品，否则返回全国范围内的TOP销量商品，目前返回的商品数量为10个
     * @param: [partyIdRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.product.response.ProductTopSalesItemResponseVO>>
     * @create 2018-07-03 21:45
     **/
    @PostMapping("/findTopSalesProduct")
    public Result<ListResponseVO<ProductTopSalesItemResponseVO>> findTopSalesProduct(
            @RequestBody TopSaleRequestVO topSaleRequestVO) {
        return productCommonService.findTopSalesProduct(topSaleRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: findTopSalesProduct
     * @methodDesc: 查询TOP销量商品
     * @description: 该接口的统计范围是只按照销量统计，不管商品是否下架，返回集合按照销量排序， 参数 party_id有值返回指定门店或仓库的TOP销售商品，否则返回全国范围内的TOP销量商品，目前返回的商品数量为10个
     * @param: [partyIdRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.product.response.ProductTopSalesItemResponseVO>>
     * @create 2018-07-03 21:45
     **/
    @PostMapping("/findTopSalesAndHisProduct")
    public Result<ListResponseVO<ProductTopSalesItemResponseVO>> findTopSalesAndHisProduct(
            @RequestBody TopSaleRequestVO topSaleRequestVO) {
        return productCommonService.findTopSalesAndHisProduct(topSaleRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: lockProductInventory
     * @methodDesc:
     * @description:
     * @param: [requestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.inventory.response.ProductsInventoryLockResponseVO>
     * @create 2018-08-21 11:18
     **/
    @PostMapping("/lockProductInventory")
    public Result<ProductsInventoryLockResponseVO> lockProductInventory(@RequestBody ProductsInventoryLockRequestVO requestVO) {
        return productInventoryService.lockProductInventory(requestVO);
    }

    /**
     * @author 王光银
     * @methodName: compensateProductsLock
     * @methodDesc:
     * @description:
     * @param: [requestVO]
     * @return com.hryj.common.Result
     * @create 2018-08-21 11:18
     **/
    @PostMapping("/compensateProductsLock")
    public Result compensateProductsLock(@RequestBody ProductsInventoryLockRollBackRequestVO requestVO) {
        return productInventoryService.compensateProductsLock(requestVO);
    }

    /**
     * @author 汪豪
     * @methodName: searchProductBrand
     * @methodDesc:
     * @description: 多条品牌数据只返回前十条数据--v1.2新增接口
     * @param: [searchProductBrandRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.product.common.response.ProductBrandResponseVO>>
     * @create 2018-09-11 9:09
     **/
    @PostMapping("/searchProductBrand")
    public Result<ListResponseVO<ProductBrandResponseVO>> searchProductBrand(){
        return productCommonService.getBrandList();
    }

    /**
     * @author 汪豪
     * @methodName: searchProductGeo
     * @methodDesc:
     * @description: 多条商品产地数据只返回前十条数据--v1.2新增接口
     * @param: [searchProductGeoRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.product.common.response.ProductGeoResponseVO>>
     * @create 2018-09-11 9:13
     **/
    @PostMapping("/searchProductGeo")
    public Result<ListResponseVO<ProductGeoResponseVO>> searchProductGeo(){
        return productCommonService.getProductGeoList();
    }

    /**
     * @author 汪豪
     * @methodName: searchHSCode
     * @methodDesc:
     * @description: 多条商品HSCode数据只返回前十条数据--v1.2新增接口
     * @param: [searchHSCodeRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.product.common.response.ProductTaxRateResponseVO>>
     * @create 2018-09-11 9:16
     **/
    @PostMapping("/searchHSCode")
    public Result<ListResponseVO<ProductTaxRateResponseVO>> searchHSCode(@RequestBody SearchHSCodeRequestVO searchHSCodeRequestVO){
        return productCommonService.findTaxRateListByHSCode(searchHSCodeRequestVO);
    }

    /**
     * @author 汪豪
     * @methodName: syncCrossBorderProductInventory
     * @methodDesc: 同步跨境商品库存
     * @description:
     * @param: []
     * @return
     * @create 2018-09-11 9:16
     **/
    @PostMapping("/syncCrossBorderProductInventory")
    public void syncCrossBorderProductInventory(){
        syncCrossBorderProductInventoryTask.syncCrossBorderProductInventory();
    }
}
