package com.hryj.entity.vo.product.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: ProductOwnedCategoryRequestVO
 * @description: 商品属性请求VO
 * @create 2018/7/5 0005 9:48
 **/
@ApiModel(value = "商品属性请求VO", description = "商品属性请求VO")
@Data
public class ProductOwnedCategoryRequestVO extends RequestVO {

    @ApiModelProperty(value = "商品ID", required = true)
    private Long product_id;

    @ApiModelProperty(value = "分类ID", required = true)
    private Long category_id;
}
