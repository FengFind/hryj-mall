package com.hryj.controller.crossborder;

import com.hryj.common.Result;
import com.hryj.entity.vo.product.request.ProductInventoryAdjustmentRequestVO;
import com.hryj.exception.BizException;
import com.hryj.service.inventory.ProductInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 王光银
 * @className: ProductController
 * @description:  跨境商品管理接口，开放给后台管理系统
 * @create 2018/6/26 0026 16:06
 **/
@RestController
@RequestMapping("/product/inventory")
public class CrossBorderProductController {

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
    @PostMapping("/crossBorderProductInventoryAdjustment")
    public Result crossBorderProductInventoryAdjustment(
            @RequestBody ProductInventoryAdjustmentRequestVO requestVO) throws BizException {
        return productInventoryService.crossBorderProductInventoryAdjustment(requestVO);
    }

}
