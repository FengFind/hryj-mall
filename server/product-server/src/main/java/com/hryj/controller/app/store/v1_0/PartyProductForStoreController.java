package com.hryj.controller.app.store.v1_0;

import com.hryj.common.BizCodeEnum;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.product.common.request.ProductValidateRequestVO;
import com.hryj.entity.vo.product.request.app.AppSearchProductRequestVO;
import com.hryj.entity.vo.product.response.app.AppProdListItemResponseVO;
import com.hryj.entity.vo.product.response.app.AppProductInfoResponseVO;
import com.hryj.feign.UserFeignClient;
import com.hryj.service.app.v1_0.AppPartyProductService;
import com.hryj.service.util.UserCoveredByPartyUtil;
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
@RestController("v1.0-PartyProductForStoreController")
@RequestMapping("/v1-0/partyProductForStore")
public class PartyProductForStoreController {

    @Autowired
    private AppPartyProductService appPartyProductService;

    @Autowired
    private UserFeignClient userFeignClient;

    /**
     * @author 王光银
     * @methodName: searchProductList
     * @methodDesc: App门店端商品搜素
     * @description:
     * @param: [appSearchProductRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.product.response.app.AppProdListItemResponseVO>>
     * @create 2018-06-30 9:33
     **/
    @SuppressWarnings("unchecked")
    @PostMapping("/searchProductList")
    public Result<ListResponseVO<AppProdListItemResponseVO>> searchProduct(@RequestBody(required = false) AppSearchProductRequestVO appSearchProductRequestVO) {
        Result<UserCoveredByPartyUtil.UserCoveredPartyHandler> result = UserCoveredByPartyUtil.calculateAndGetUserCoveredParty(appSearchProductRequestVO.getUser_id(), userFeignClient);
        if (result.isFailed()) {
            return new Result(CodeEnum.FAIL_BUSINESS, result.getMsg());
        }
        UserCoveredByPartyUtil.UserCoveredPartyHandler userCoveredPartyHandler = result.getData();
        if (!userCoveredPartyHandler.hasParty()) {
            Result return_result = new Result(CodeEnum.FAIL_BUSINESS, "没有覆盖用户的门店和仓库");
            return_result.setBiz_code(BizCodeEnum.NO_COVERED_PARTY.getCode());
            return return_result;
        }

        return appPartyProductService.searchProduct(appSearchProductRequestVO, userCoveredPartyHandler);
    }

    /**
     * @author 王光银
     * @methodName: getProductInfo
     * @methodDesc: APP门店端获取商品的详细信息
     * @description: 商品下架返回 biz_code=11000,  活动结束返回 biz_code=11010，库存数量不足返回 biz_code=11020，这三个业务上的非正常情况编码不会影响其他数据的正常返回
     * @param: [productValidateRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.product.response.app.AppProductInfoResponseVO>
     * @create 2018-06-30 18:29
     **/
    @PostMapping("/getProductInfo")
    public Result<AppProductInfoResponseVO> getProductInfo(@RequestBody(required = false) ProductValidateRequestVO productValidateRequestVO) {
        Result<UserCoveredByPartyUtil.UserCoveredPartyHandler> userPartyHandlerResult = UserCoveredByPartyUtil.calculateAndGetUserCoveredParty(productValidateRequestVO.getUser_id(), userFeignClient);
        if (userPartyHandlerResult.getData() == null) {
            //门店端端口详情数据加载，如果覆盖用户的门店与仓库获取失败，要设置一个默认值，避免业务方法内部使用 login_token去获取覆盖仓库与门店
            userPartyHandlerResult.setData(new UserCoveredByPartyUtil.UserCoveredPartyHandler());
        }
        return appPartyProductService.getProductInfo(productValidateRequestVO, userPartyHandlerResult.getData());
    }
}
