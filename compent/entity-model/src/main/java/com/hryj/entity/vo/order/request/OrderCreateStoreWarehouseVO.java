package com.hryj.entity.vo.order.request;

import com.hryj.entity.vo.order.OrderProductItemVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 白飞
 * @className: OrderCreateStoreWarehouseVO
 * @description: 订单创建-门店或仓库信息集合
 * @create 2018/8/22 0002 13:05
 **/
@Data
@ApiModel(value = "订单创建-门店或仓库信息集合-v1.2")
public class OrderCreateStoreWarehouseVO {

    @ApiModelProperty(value = "仓库或门店id", required = true)
    private Long party_id;

    @ApiModelProperty(value = "配送方式", notes = "01-自提,02-送货上门,03-快递", required = true)
    private String delivery_type;

    @ApiModelProperty(value = "期望送达开始时间", notes = "时间戳", required = true)
    private Long hope_delivery_start_time;

    @ApiModelProperty(value = "期望送达截止时间", notes = "时间戳", required = true)
    private Long hope_delivery_end_time;

    @ApiModelProperty(value = "送货上门的说明")
    private String delivery_info;

    @ApiModelProperty(value = "订单金额")
    private BigDecimal order_amount;

    @ApiModelProperty(value = "优惠金额")
    private BigDecimal discount_amount;

    @ApiModelProperty(value = "其他备注")
    private String memo;

    @ApiModelProperty(value = "商品列表")
    private List<OrderProductItemVO> product_list;
}
