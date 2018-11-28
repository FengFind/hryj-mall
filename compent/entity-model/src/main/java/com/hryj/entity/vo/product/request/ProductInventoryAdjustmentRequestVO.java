package com.hryj.entity.vo.product.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: ProductInventoryAdjustmentRequestVO
 * @description:
 * @create 2018/9/11 0011 9:34
 **/
@Data
@ApiModel(value = "跨境商品库存调整请求VO", description = "跨境商品库存调整请求VO")
public class ProductInventoryAdjustmentRequestVO extends RequestVO {

    @ApiModelProperty(value = "商品ID", required = true)
    private Long product_id;

    @ApiModelProperty(value = "库存数量，必须大于0", required = true)
    private Integer inventory_quantity;
}
