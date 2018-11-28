package com.hryj.entity.vo.cart.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 罗秋涵
 * @className: CartoPerationRequestVO
 * @description:
 * @create 2018/7/5 0005 9:39
 **/
@ApiModel(description = "购物车请求VO-v1.2")
@Data
public class CartOperationRequestVO extends RequestVO {

    @ApiModelProperty(value = "购物车ID集合", required = true)
    private String  cartItemIds;

    @ApiModelProperty(value = "客户ID,代下单时用于被代下单对象的user_id")
    private Long user_id;

    @ApiModelProperty(value = "合计金额，用户金额校验")
    private String pay_price;
}
