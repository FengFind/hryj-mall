package com.hryj.entity.vo.profit.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author 李道云
 * @className: DataQueryResponseVO
 * @description: 数据查询响应VO
 * @create 2018/7/7 12:11
 **/
@ApiModel(value = "数据查询响应VO")
@Data
public class DataQueryResponseVO implements Serializable {

    @ApiModelProperty(value = "已配送数量")
    private Integer distribution_num;

    @ApiModelProperty(value = "已取件数量")
    private Integer take_back_num;

    @ApiModelProperty(value = "超时配送数量")
    private Integer timeout_distribution_num;

    @ApiModelProperty(value = "代下订单总数")
    private Integer help_order_num;

    @ApiModelProperty(value = "代下订单总额")
    private BigDecimal help_order_amt;

    @ApiModelProperty(value = "代下订单商品总数")
    private Integer help_order_product_num;

    @ApiModelProperty(value = "门店订单总数")
    private Integer store_order_num;

    @ApiModelProperty(value = "门店订单总额")
    private BigDecimal store_order_amt;

    @ApiModelProperty(value = "门店订单商品总数")
    private Integer store_order_product_num;

    @ApiModelProperty(value = "推荐注册用户数")
    private Integer referral_register_num;

    @ApiModelProperty(value = "交易用户数")
    private Integer trade_user_num;

    @ApiModelProperty(value = "新增交易用户数")
    private Integer new_trade_user_num;

}
