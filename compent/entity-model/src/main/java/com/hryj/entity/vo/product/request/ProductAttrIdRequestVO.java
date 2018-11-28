package com.hryj.entity.vo.product.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: ProductAttrIdRequestVO
 * @description:
 * @create 2018/7/5 0005 10:03
 **/
@ApiModel(value = "商品属性ID请求VO", description = "商品属性ID请求VO")
@Data
public class ProductAttrIdRequestVO extends RequestVO {

    @ApiModelProperty(value = "商品属性ID", required = true)
    private Long product_attribute_id;
}
