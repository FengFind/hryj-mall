package com.hryj.controller.app.user.v1_2;

import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.product.common.request.ProductValidateRequestVO;
import com.hryj.entity.vo.product.request.app.AppSearchProductRequestVO;
import com.hryj.entity.vo.product.response.app.AppProdListItemResponseVO;
import com.hryj.entity.vo.product.response.app.AppProductInfoResponseVO;
import com.hryj.entity.vo.promotion.recommend.request.app.PartyRecommendProdSearchRequestVO;
import com.hryj.service.app.v1_2.AppPartyProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 汪豪
 * @className: PartyProductForUserController
 * @description:
 * @create 2018/9/11 0011 10:46
 **/
@RestController
@RequestMapping("/v1-2/partyProductForApp")
public class PartyProductForUserController {

    @Autowired
    private AppPartyProductService appPartyProductService;

    /**
     * @author 汪豪
     * @methodName: getProductInfo
     * @methodDesc: APP用户端获取商品的详细信息
     * @description: 商品下架返回 biz_code=11000,  活动结束返回 biz_code=11010，库存数量不足返回 biz_code=11020，这三个业务上的非正常情况编码不会影响其他数据的正常返回
     * @param: [productValidateRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.product.response.app.AppProductInfoResponseVO>
     * @create 2018-09-11 10:29
     **/
    @PostMapping("/getProductInfo")
    public Result<AppProductInfoResponseVO> getProductInfo(@RequestBody(required = false) ProductValidateRequestVO productValidateRequestVO) {
        return appPartyProductService.getProductInfo(productValidateRequestVO);
    }

    /**
     * @author 代廷波
     * @methodName: findRecommendProductList
     * @methodDesc: App用户端查询推荐位商品列表
     * @description: 推荐商品返回列表不分页，商品返回数量的多少，排序都在接口内控制
     * @param: [requestVO]
     * @return com.hryj.common.Result<java.util.List<com.hryj.entity.vo.product.response.app.AppProdListItemResponseVO>>
     * @create 2018/09/11 15:00
     **/

    @PostMapping("/findRecommendProductList")
    public Result<ListResponseVO<AppProdListItemResponseVO>> findRecommendProduct(@RequestBody(required = false) PartyRecommendProdSearchRequestVO requestVO) {
        return appPartyProductService.findRecommendProductList(requestVO);
    }

    /**
     * @author 代廷波
     * @methodName: searchProductList
     * @methodDesc: App用户端商品搜素
     * @description:
     * @param: [appSearchProductRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.product.response.app.AppProdListItemResponseVO>>
     * @create 2018/09/11 15:00
     **/
    @PostMapping("/searchProductList")
    public Result<ListResponseVO<AppProdListItemResponseVO>> searchProduct(@RequestBody(required = false) AppSearchProductRequestVO appSearchProductRequestVO) {
        return appPartyProductService.searchProduct(appSearchProductRequestVO);
    }
}
