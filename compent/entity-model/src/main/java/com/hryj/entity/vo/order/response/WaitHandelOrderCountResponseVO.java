package com.hryj.entity.vo.order.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: WaitHandelOrderCountResponseVO
 * @description: 待处理订单数量VO
 * @create 2018/6/30 10:30
 **/
@Data
@ApiModel(value = "待处理订单数量VO")
public class WaitHandelOrderCountResponseVO {

    @ApiModelProperty(value = "待配送数量", required = true)
    private Integer wait_delivery_num;

    @ApiModelProperty(value = "待取货数量", required = true)
    private Integer wait_take_num;

    @ApiModelProperty(value = "待退货处理数量", required = true)
    private Integer wait_return_num;

    @ApiModelProperty(value = "待分配取货/退货", required = true)
    private Integer wait_distribution;
}
