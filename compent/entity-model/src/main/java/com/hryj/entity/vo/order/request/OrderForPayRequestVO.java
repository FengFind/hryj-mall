package com.hryj.entity.vo.order.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 罗秋涵
 * @className: OrderForPayRequestVO
 * @description:
 * @create 2018/7/5 0005 10:45
 **/

@Data
@ApiModel(value = "订单支付VO")
public class OrderForPayRequestVO extends RequestVO {

    @ApiModelProperty(value = "订单编号集合,逗号分隔", required = true)
    private String orderNumStr;

}
