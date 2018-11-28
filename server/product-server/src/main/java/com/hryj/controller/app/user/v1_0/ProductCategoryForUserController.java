package com.hryj.controller.app.user.v1_0;

import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.product.category.request.app.AppProdCateSearchRequestVO;
import com.hryj.entity.vo.product.category.response.ProductCategoryItemResponseVO;
import com.hryj.exception.BizException;
import com.hryj.service.app.v1_0.AppProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 王光银
 * @className: ProductCategoryController
 * @description: APP用户端商品分类接口
 * @create 2018/6/25 0025 20:07
 **/
@RestController("v1.0-ProductCategoryForUserController")
@RequestMapping("/v1-0/productCategoryForApp")
public class ProductCategoryForUserController {

    @Autowired
    private AppProductCategoryService appProductCategoryService;

    /**
     * @author 王光银
     * @methodName: findProdCate
     * @methodDesc: APP用户端查询商品分类
     * @description: 如果category_id参数有值则返回该分类下的直接子分类，如果没有值则返回所有一级分类
     * @param: [requestVO, category_id]
     * @return com.hryj.common.Result<java.util.List<com.hryj.entity.vo.product.category.response.ProductCategoryItemResponseVO>>
     * @create 2018-06-29 11:54
     **/
    @PostMapping("/findProdCate")
    public Result<ListResponseVO<ProductCategoryItemResponseVO>> findProdCate(
            @RequestBody AppProdCateSearchRequestVO appProdCateSearchRequestVO) throws BizException {
        return appProductCategoryService.findProdCate(appProdCateSearchRequestVO, null);
    }

}
