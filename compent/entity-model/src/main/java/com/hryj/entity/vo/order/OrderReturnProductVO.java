package com.hryj.entity.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 叶方宇
 * @className: OrderInfoToDistribution
 * @description: 退货单商品信息表
 * @create 2018/7/9 0009 10:07
 **/

@Data
@ApiModel(value = "配送单产品信息记录需要数据")
public class OrderReturnProductVO {

    @ApiModelProperty(value = "订单id", required = true)
    private Long order_id;

    @ApiModelProperty(value = "退货单商品id", required = true)
    private Long order_product_id;

    @ApiModelProperty(value = "数量", required = true)
    private Integer return_quantity;

    @ApiModelProperty(value = "商品单价", required = true)
    private BigDecimal return_price;

    @ApiModelProperty(value = "退货金额", required = true)
    private BigDecimal return_amt;

    @ApiModelProperty(value = "门店ID", required = true)
    private Long party_id;

    @ApiModelProperty(value = "活动id", required = true)
    private Long activity_id;

    @ApiModelProperty(value = "商品id", required = true)
    private Long product_id;

}
