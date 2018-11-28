package com.hryj.entity.vo.cart.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: ShoppingCartItemAdjustRequestVO
 * @description:
 * @create 2018/7/21 0021 20:48
 **/
@Data
public class ShoppingCartItemAdjustRequestVO extends RequestVO {

    @ApiModelProperty(value = "购物车商品条目ID", notes = "购物车列表查询时返回", required = true)
    private Long cart_record_id;

    @ApiModelProperty(value = "购物车商品数量", notes = "将此参数对原值进行覆盖", required = true)
    private Integer quantity;
}
