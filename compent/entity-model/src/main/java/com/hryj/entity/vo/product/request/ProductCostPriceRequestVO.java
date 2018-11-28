package com.hryj.entity.vo.product.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 王光银
 * @className: ProductCostPriceRequestVO
 * @description: 商品成本价请求VO
 * @create 2018/7/5 0005 9:44
 **/
@ApiModel(value = "商品成本价请求VO", description = "商品成本价请求VO")
@Data
public class ProductCostPriceRequestVO extends RequestVO {

    @ApiModelProperty(value = "商品ID", required = true)
    private Long product_id;

    @ApiModelProperty(value = "商品成本价", required = true)
    private BigDecimal cost_price;
}
