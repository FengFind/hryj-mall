package com.hryj.entity.vo.cart.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: ShoppingCartRequestVO
 * @description: 购物车请求VO
 * @create 2018/6/29 18:41
 **/
@ApiModel(description = "购物车请求VO")
@Data
public class ShoppingCartRequestVO extends RequestVO {


    @ApiModelProperty(value = "用户id", notes = "代下单功能时有值，需要知道是谁的购物车")
    private Long user_id;

    @ApiModelProperty(value = "门店仓库id", required = true)
    private Long party_id;

    @ApiModelProperty(value = "商品id", required = true)
    private Long product_id;

    @ApiModelProperty(value = "商品数量", required = true)
    private Integer quantity;

    @ApiModelProperty(value = "活动id")
    private Long activity_id;

    @ApiModelProperty(value = "分享者ID,购买分享商品时有值")
    private Long share_user_id;

    @ApiModelProperty(value = "分享来源,购买分享商品时有值")
    private String share_source;

    @ApiModelProperty(value = "合计金额，用户金额校验")
    private String pay_price;
}
