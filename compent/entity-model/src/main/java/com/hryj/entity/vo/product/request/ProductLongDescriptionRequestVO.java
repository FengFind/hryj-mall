package com.hryj.entity.vo.product.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: ProductLongDescriptionRequestVO
 * @description: 商品图文详情请求VO
 * @create 2018/7/5 0005 9:42
 **/
@ApiModel(value = "商品图文详情请求VO", description = "商品图文详情请求VO")
@Data
public class ProductLongDescriptionRequestVO extends RequestVO {

    @ApiModelProperty(value = "商品ID", required = true)
    private Long product_id;

    @ApiModelProperty(value = "商品图文详情", required = true)
    private String long_description;
}
