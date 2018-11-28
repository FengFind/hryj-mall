package com.hryj.controller.app.store.v1_2;

import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.product.common.request.ProductValidateRequestVO;
import com.hryj.entity.vo.product.request.app.AppSearchProductRequestVO;
import com.hryj.entity.vo.product.response.app.AppProdListItemResponseVO;
import com.hryj.entity.vo.product.response.app.AppProductInfoResponseVO;
import com.hryj.service.app.v1_2.AppPartyProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 王光银
 * @className: ProductController
 * @description: APP门店端端口服务接口
 * @create 2018/6/26 0026 16:06
 **/
@RestController
@RequestMapping("/v1-2/partyProductForStore")
public class PartyProductForStoreController {

    @Autowired
    private AppPartyProductService appPartyProductService;

    /**
     * @author 王光银
     * @methodName: searchProduct
     * @methodDesc: App门店端商品搜素
     * @description:
     * @param: [appSearchProductRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.product.response.app.AppProdListItemResponseVO>>
     * @create 2018-08-15 15:26
     **/
    @SuppressWarnings("unchecked")
    @PostMapping("/searchProductList")
    public Result<ListResponseVO<AppProdListItemResponseVO>> searchProduct(@RequestBody(required = false) AppSearchProductRequestVO appSearchProductRequestVO) {
        return appPartyProductService.searchProduct(appSearchProductRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: getProductInfo
     * @methodDesc: APP门店端获取商品的详细信息
     * @description: 商品下架返回 biz_code=11000,  活动结束返回 biz_code=11010，库存数量不足返回 biz_code=11020，这三个业务上的非正常情况编码不会影响其他数据的正常返回
     * @param: [productValidateRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.product.response.app.AppProductInfoResponseVO>
     * @create 2018-08-15 15:26
     **/
    @PostMapping("/getProductInfo")
    public Result<AppProductInfoResponseVO> getProductInfo(@RequestBody(required = false) ProductValidateRequestVO productValidateRequestVO) {
        return appPartyProductService.getProductInfo(productValidateRequestVO);
    }
}
