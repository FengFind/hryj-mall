package com.hryj.entity.vo.order.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 罗秋涵
 * @className: ConfirmPayOrderRequestVO
 * @description: 确认支付
 * @create 2018/7/5 0005 10:51
 **/

@Data
@ApiModel(value = "确认支付请求VO")
public class ConfirmPayOrderRequestVO extends RequestVO {

    @ApiModelProperty(value = "订单编号集合,逗号分隔", required = true)
    private String orderNumStr;

    @ApiModelProperty(value = "支付方式:01-微信,02-支付宝,03-银联", required = true)
    private String pay_method;

}
