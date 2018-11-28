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
@ApiModel(value = "发货分派VO")
public class DeliveryForDistributionVO extends RequestVO {

    @ApiModelProperty(value = "订单编号",required = true)
    private Long order_id;

    @ApiModelProperty(value = "配送员id",required = true)
    private Long staff_id;

    @ApiModelProperty(value = "配送员姓名",required = true)
    private String staff_name;
}
