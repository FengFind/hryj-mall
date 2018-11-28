package com.hryj.controller;

import com.hryj.common.Result;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.product.request.ProductInventoryAdjustmentRequestVO;
import com.hryj.entity.vo.product.request.*;
import com.hryj.entity.vo.product.response.*;
import com.hryj.exception.BizException;
import com.hryj.exception.ServerException;
import com.hryj.service.ProductService;
import com.hryj.service.inventory.ProductInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 王光银
 * @className: ProductController
 * @description:  商品管理接口，开放给后台管理系统
 * @create 2018/6/26 0026 16:06
 **/
@RestController
@RequestMapping("/productMgr")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductInventoryService productInventoryService;

    /**
     * @author 王光银
     * @methodName: searchProductByPage
     * @methodDesc: 分页查询商品数据
     * @description:
     * @param: [productSearchRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.product.response.ProdListItemResponseVO>>
     * @create 2018-06-30 9:09
     **/
    @PostMapping("/findProductByPage")
    public Result<PageResponseVO<ProdListItemResponseVO>> searchProductByPage(
            @RequestBody SearchProductRequestVO productSearchRequestVO) throws BizException {
        return productService.searchProductByPage(productSearchRequestVO);
    }


    /**
     * @author 王光银
     * @methodName: getProduct
     * @methodDesc: 查询一个商品的完整信息,
     * @description: 该接口会跟随业务中商品数据的增加而增加返回数据
     * @param: [product_id]
     * @return com.hryj.common.Result<com.hryj.entity.vo.product.response.ProductDetailResponseVO>
     * @create 2018-06-27 20:02
     **/
    @PostMapping("/getProduct")
    public Result<ProductDetailResponseVO> getProduct(@RequestBody ProductIdRequestVO productIdRequestVO) throws BizException {
        return productService.getProduct(productIdRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: saveCreateProduct
     * @methodDesc: 保存新增一个商品
     * @description:
     * @param: [product]
     * @return com.hryj.common.Result
     * @create 2018-06-27 20:00
     **/
    @PostMapping("/saveCreateProduct")
    public Result saveCreateProduct(@RequestBody ProductRequestVO product) throws BizException, ServerException {
        return productService.saveCreateProduct(product);
    }

    /**
     * @author 王光银
     * @methodName: updateProduct
     * @methodDesc: 保存修改商品信息
     * @description: 调用该接口修改商品信息会导致商品下架
     * @param: [product_id, product]
     * @return com.hryj.common.Result
     * @create 2018-06-27 20:00
     **/
    @PostMapping("/updateProduct")
    public Result updateProduct(@RequestBody ProductRequestVO product) throws BizException, ServerException {
        return productService.updateProduct(product);
    }

    /**
     * @author 王光银
     * @methodName: downProduct
     * @methodDesc: 上架商品（一个或多个）
     * @description:
     * @param: [idsRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-07-13 9:31
     **/
    @PostMapping("/upProducts")
    public Result upProducts(@RequestBody ProductIdsRequestVO idsRequestVO) throws ServerException {
        return productService.upProducts(idsRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: downProduct
     * @methodDesc: 下架商品（一个或多个）
     * @description:
     * @param: [idsRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-07-13 9:31
     **/
    @PostMapping("/downProducts")
    public Result downProducts(@RequestBody ProductIdsRequestVO idsRequestVO) throws ServerException {
        return productService.downProducts(idsRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: adjustmentCrossBorderProductInventory
     * @methodDesc: 调整商品中心-商品库存
     * @description:
     * @param: [requestVO]
     * @return com.hryj.common.Result
     * @create 2018-09-11 9:38
     **/
    @PostMapping("/adjustmentProductInventory")
    public Result adjustmentProductInventory(
            @RequestBody ProductInventoryAdjustmentRequestVO requestVO) throws BizException {
        return productInventoryService.crossBorderProductInventoryAdjustment(requestVO);
    }
}
