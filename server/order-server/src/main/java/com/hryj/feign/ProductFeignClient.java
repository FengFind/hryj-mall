package com.hryj.feign;

import com.hryj.common.Result;
import com.hryj.entity.vo.inventory.request.ProductsInventoryLockRequestVO;
import com.hryj.entity.vo.inventory.response.ProductsInventoryLockResponseVO;
import com.hryj.entity.vo.product.common.request.PartyProductInventoryAdjustRequestVO;
import com.hryj.entity.vo.product.common.request.ProductsValidateRequestVO;
import com.hryj.entity.vo.product.common.response.ProductsValidateResponseVO;
import com.hryj.entity.vo.promotion.activity.request.PartyProductActivityRequestVO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author 罗秋涵
 * @className: ProductFeignClient
 * @description:
 * @create 2018/7/6 0006 17:50
 **/
@FeignClient(name = "product-server")
public interface ProductFeignClient {

    /**
     * @author 罗秋涵
     * @description: 多商品验证
     * @param: [productValidateRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.product.common.response.ProductValidateResponseVO>
     * @create 2018-07-06 17:56
     **/
    @RequestMapping("/productCommon/productsValidate")
    Result<ProductsValidateResponseVO> productsValidate(ProductsValidateRequestVO productsValidateRequestVO);

    /**
     * @return com.hryj.common.Result
     * @author 罗秋涵
     * @description: 释放库存
     * @param: [partyProductInventoryAdjustRequestVO]
     * @create 2018-07-11 22:15
     **/
    @RequestMapping("/productCommon/partyProductInventoryAdjust")
    Result partyProductInventoryAdjust(PartyProductInventoryAdjustRequestVO partyProductInventoryAdjustRequestVO);

    /**
     * @author 罗秋涵
     * @description: 批量查询商品信息
     * @param: [prod_ids_list]
     * @return com.hryj.common.Result
     * @create 2018-08-17 11:29
     **/
    @RequestMapping("/productCommon/getProductsSimpleInfo")
    Result getProductsSimpleInfo(@RequestBody List<PartyProductActivityRequestVO> prod_ids_list);


    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 库存锁定
     * @param:
     * @return
     * @create 2018-08-21 16:44
     **/
    @RequestMapping("/productCommon/lockProductInventory")
    Result<ProductsInventoryLockResponseVO> lockProductInventory(@RequestBody ProductsInventoryLockRequestVO requestVO);

    /**
     * @author 叶方宇
     * @className: StaffAppFeignClient
     * @description:
     * @create 2018/7/10 0010 20:22
     **/
    @FeignClient(name = "staff-server")
    interface StaffAppFeignClient {


    }
}
