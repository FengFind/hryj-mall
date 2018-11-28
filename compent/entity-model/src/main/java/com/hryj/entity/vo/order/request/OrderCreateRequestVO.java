package com.hryj.entity.vo.order.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 白飞
 * @className: OrderCreateRequestVO
 * @description: 订单创建VO.v2
 * @create 2018/8/22 14:59
 **/
@Data
@ApiModel(value = "订单创建VO.v1.2")
public class OrderCreateRequestVO extends RequestVO {

    @ApiModelProperty(value = "代下单用户ID", required = true)
    private Long user_id;

    @ApiModelProperty(value = "分享用户ID; 立即购买为分享商品时，必填")
    private Long share_user_id;

    @ApiModelProperty(value = "购买人姓名")
    private String buyer_name;

    @ApiModelProperty(value = "购买人身份证号")
    private String buyer_id_card;

    @ApiModelProperty(value = "分享来源; 立即购买为分享商品时，必填")
    private String share_source;

    @ApiModelProperty(value = "支付订单总金额", required = true)
    private BigDecimal pay_amount;

    @ApiModelProperty(value = "优惠总金额")
    private BigDecimal total_discount_amount;

    @ApiModelProperty(value = "用户收货地址ID", required = true)
    private Long address_id;

    @ApiModelProperty(value = "门店或仓库信息集合", required = true)
    private List<OrderCreateStoreWarehouseVO> store_warehouse_list;
}
