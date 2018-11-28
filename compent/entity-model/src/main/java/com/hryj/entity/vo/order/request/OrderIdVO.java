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
@ApiModel(value = "orderid参数传递vo")
public class OrderIdVO extends RequestVO {

    @ApiModelProperty(value = "订单编号",required = true)
    private Long order_id;
}
