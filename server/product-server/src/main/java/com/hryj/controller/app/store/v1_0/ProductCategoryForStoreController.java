package com.hryj.controller.app.store.v1_0;

import com.hryj.common.BizCodeEnum;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.product.category.request.app.AppProdCateSearchRequestVO;
import com.hryj.entity.vo.product.category.response.ProductCategoryItemResponseVO;
import com.hryj.entity.vo.user.UserPartyVO;
import com.hryj.exception.BizException;
import com.hryj.feign.UserFeignClient;
import com.hryj.service.app.v1_0.AppProductCategoryService;
import com.hryj.service.util.UserCoveredByPartyUtil;
import com.hryj.utils.UtilValidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

/**
 * @author 王光银
 * @className: ProductCategoryController
 * @description: APP门店端商品分类查询接口
 * @create 2018/6/25 0025 20:07
 **/
@RestController("v1.0-ProductCategoryForStoreController")
@RequestMapping("/v1-0/productCategoryForStore")
public class ProductCategoryForStoreController {

    @Autowired
    private AppProductCategoryService appProductCategoryService;

    @Autowired
    private UserFeignClient userFeignClient;

    /**
     * @author 王光银
     * @methodName: findProdCate
     * @methodDesc: APP门店端查询商品分类
     * @description: 如果category_id参数有值则返回该分类下的直接子分类，如果没有值则返回所有一级分类
     * @param: [requestVO, category_id]
     * @return com.hryj.common.Result<java.util.List<com.hryj.entity.vo.product.category.response.ProductCategoryItemResponseVO>>
     * @create 2018-06-29 11:54
     **/
    @PostMapping("/findProdCate")
    public Result<ListResponseVO<ProductCategoryItemResponseVO>> findProdCate(
            @RequestBody AppProdCateSearchRequestVO appProdCateSearchRequestVO) throws BizException {
        //门店端需要先获取用户的覆盖门店
        Result<UserCoveredByPartyUtil.UserCoveredPartyHandler> result = UserCoveredByPartyUtil.calculateAndGetUserCoveredParty(appProdCateSearchRequestVO.getUser_id(), userFeignClient);
        if (result.isFailed()) {
            return new Result(CodeEnum.FAIL_BUSINESS, result.getMsg());
        }
        UserCoveredByPartyUtil.UserCoveredPartyHandler userCoveredPartyHandler = result.getData();
        if (!userCoveredPartyHandler.hasParty()) {
            Result return_result = new Result(CodeEnum.FAIL_BUSINESS, "没有覆盖用户的门店和仓库");
            return_result.setBiz_code(BizCodeEnum.NO_COVERED_PARTY.getCode());
            return return_result;
        }
        Set<Long> party_id_set = new HashSet<>((userCoveredPartyHandler.warehouse == null ? 0 : 1) + (UtilValidate.isEmpty(userCoveredPartyHandler.store) ? 0 : userCoveredPartyHandler.store.size()));
        if (userCoveredPartyHandler.warehouse != null) {
            Long party_id = userCoveredPartyHandler.warehouse.getParty_id();
            if (party_id != null && party_id > 0L) {
                party_id_set.add(party_id);
            }
        }
        if (UtilValidate.isNotEmpty(userCoveredPartyHandler.store)) {
            for (UserPartyVO userPartyVO : userCoveredPartyHandler.store) {
                Long party_id = userPartyVO.getParty_id();
                if (party_id != null && party_id > 0L) {
                    party_id_set.add(party_id);
                }
            }
        }

        return appProductCategoryService.findProdCate(appProdCateSearchRequestVO, party_id_set);
    }
}
