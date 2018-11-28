package com.hryj.entity.vo.order.response;

import com.hryj.entity.vo.user.UserAddressVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author BF
 * @className: OrderSettlementResponseVO
 * @description: 订单确认响应VO.v2
 * @create 2018/8/16 17:05
 **/
@Data
@ApiModel(value = "订单确认响应头VO.v1.2")
public class OrderConfirmInfoResponseVO {

    @ApiModelProperty(value = "代下单用户ID", required = true)
    private Long user_id;

    @ApiModelProperty(value = "分享用户ID")
    private Long share_user_id;

    @ApiModelProperty(value = "分享来源")
    private String share_source;

    @ApiModelProperty(value = "是否显示订购人和身份证号码")
    private Boolean is_show_buyer;

    @ApiModelProperty(value = "购买人姓名")
    private String buyer_name;

    @ApiModelProperty(value = "购买人身份证号")
    private String buyer_id_card;

    @ApiModelProperty(value = "合计订单金额", required = true)
    private BigDecimal total_order_amount;

    @ApiModelProperty(value = "合计优惠金额")
    private BigDecimal total_discount_amount;

    @ApiModelProperty(value = "合计综合税费")
    private BigDecimal total_tax;

    @ApiModelProperty(value = "用户收货地址集合", required = true)
    private List<UserAddressVO> user_address_list;

    @ApiModelProperty(value = "门店或仓库信息集合", required = true)
    private List<OrderConfirmStoreWarehouseResponseVO> store_warehouse_list;
}
