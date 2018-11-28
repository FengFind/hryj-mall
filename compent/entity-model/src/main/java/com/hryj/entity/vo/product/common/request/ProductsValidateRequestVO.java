package com.hryj.entity.vo.product.common.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: ProductsValidateRequestVO
 * @description:
 * @create 2018/7/19 0019 21:46
 **/
@Data
public class ProductsValidateRequestVO extends RequestVO {

    @ApiModelProperty(value = "该参数可不传，如果传了，则原样返回")
    private String follow_value;

    @ApiModelProperty(value = "是否合并验证库存,  true合并， false不合并，默认true, 当提交验证的商品集合中存在多个相同的商品时将对库存进行合并验证")
    private Boolean merge_validate_inventory;

    @ApiModelProperty(value = "待验证的商品条目集合")
    private List<ProductValidateItem> prod_summary_list;

    @ApiModelProperty(value = "是否返回商品的促销活动信息, true返回 false不返回，缺省处理为false")
    private Boolean return_promotion_info;

    @ApiModelProperty(value = "跨境商品是否返回商品的税率信息, true返回 false不返回，缺省处理为false")
    private Boolean return_tax_rate;
}
