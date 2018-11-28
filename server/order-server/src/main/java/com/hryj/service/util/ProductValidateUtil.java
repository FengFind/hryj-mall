package com.hryj.service.util;

import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.vo.product.common.request.ProductValidateItem;
import com.hryj.entity.vo.product.common.request.ProductsValidateRequestVO;
import com.hryj.entity.vo.product.common.response.ProductsValidateResponseVO;
import com.hryj.feign.ProductFeignClient;
import com.hryj.utils.UtilMisc;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author 王光银
 * @className: ProductValidateUtil
 * @description:
 * @create 2018/7/21 0021 17:06
 **/
@Slf4j
public class ProductValidateUtil {

    public static Result<ProductsValidateResponseVO> productsValidate(Long partyId,
                                                                      Long productId,
                                                                      Long activityId,
                                                                      Integer quantity,
                                                                      String follow_value,
                                                                      ProductFeignClient productFeignClient){
        return productsValidate(new ProductValidateItem(partyId, productId, activityId, quantity, follow_value), productFeignClient);
    }

    public static Result<ProductsValidateResponseVO> productsValidate(ProductValidateItem validateItem, ProductFeignClient productFeignClient){
        return productsValidate(UtilMisc.toList(validateItem), null, productFeignClient);
    }

    public static Result<ProductsValidateResponseVO> productsValidate(List<ProductValidateItem> validateItemList, String follow_value, ProductFeignClient productFeignClient){
        if (UtilValidate.isEmpty(validateItemList)) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "待验证商品集合数据不能是空值");
        }
        try {
            ProductsValidateRequestVO requestVO = new ProductsValidateRequestVO();
            requestVO.setFollow_value(follow_value);
            requestVO.setProd_summary_list(validateItemList);
            return productFeignClient.productsValidate(requestVO);
        } catch (Exception e) {
            log.error("调用商品验证接口异常", e);
            return new Result<>(CodeEnum.FAIL_BUSINESS, "调用商品验证接口异常:" + e.getMessage());
        }
    }
}
