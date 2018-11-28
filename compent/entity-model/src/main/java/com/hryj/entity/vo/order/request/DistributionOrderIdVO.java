package com.hryj.entity.vo.order.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 叶方宇
 * @className: OrderIdVO
 * @description:
 * @create 2018/7/5 0005 9:26
 **/
@Data
@ApiModel(value = "（退货）处理订单")
public class DistributionOrderIdVO extends RequestVO {

    @ApiModelProperty(value = "订单编号",required = true)
    private Long order_id;

    @ApiModelProperty(value = "配送员ID",hidden = true)
    private Long distribution_staff_id;

    @ApiModelProperty(value = "退货单状态:01-申请中,02-已分配,03-同意退货,04-取消退货,05-拒绝退货",required = true)
    private String return_status;
}
