package com.hryj.entity.vo.cart.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 白飞
 * @className: CartRequestVO
 * @description: 立即购买、购物车列表请求VO
 * @version 2.0
 * @create 2018/8/16 16:50
 **/
@ApiModel(description = "立即购买、购物车列表请求VO.v2")
@Data
public class ProductBuyRequestVO extends RequestVO {

    /** 类型来源 */
    public enum BuyType{
        /** 立即购买 */
        buyNow,
        /** 购物车 */
        cart,
        /** 拼团 */
        group
    }

    @ApiModelProperty(value = "类型来源：0-立即购买，1-购物车，2-拼团")
    private Integer type;

    @ApiModelProperty(value = "代下单用户ID;当前订单为代下单时，必填")
    private Long user_id;

    @ApiModelProperty(value = "分享用户ID;立即购买为分享商品时，必填")
    private Long share_user_id;

    @ApiModelProperty(value = "分享来源;立即购买为分享商品时，必填")
    private String share_source;

    @ApiModelProperty(value = "合计金额")
    private BigDecimal total_amount;

    @ApiModelProperty(value = "商品信息")
    private List<ProductItemRequestVO> product_items;
}
