package com.hryj.entity.vo.order.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 罗秋涵
 * @className: OrderDifferentStateNumResponseVO
 * @description: 用户订单不同状态的数量
 * @create 2018/7/3 0003 16:50
 **/
@Data
@ApiModel(value = "用户订单不同状态的数量VO")
public class OrderDifferentStateNumResponseVO {

    @ApiModelProperty(value = "待支付数量", required = true)
    private Integer unpay=0;
    @ApiModelProperty(value = "待发货数量", required = true)
    private Integer wait_send=0;
    @ApiModelProperty(value = "已发货数量", required = true)
    private Integer already_send=0;
    @ApiModelProperty(value = "退货中数量", required = true)
    private Integer return_midway=0;

}
