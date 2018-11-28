package com.hryj.entity.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 叶方宇
 * @className: ProductInventoryAdjustVo
 * @description:
 * @create 2018/8/4 0004 16:46
 **/
@Data
@ApiModel(value = "库存锁定VO")
public class ProductInventoryAdjustVO {

    @ApiModelProperty(value = "门店ID", required = true)
    Long party_id;

    @ApiModelProperty(value = "商品ID", required = true)
    Long product_id;

    @ApiModelProperty(value = "商品数量", required = true)
    Integer quantity;

}
