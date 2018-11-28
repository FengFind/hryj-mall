package com.hryj.entity.vo.order.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 叶方宇
 * @className: OrderExpressVO
 * @description:
 * @create 2018/7/16 0016 15:20
 **/
@Data
@ApiModel(value = "物流信息")
public class OrderExpressVO extends RequestVO {

    @ApiModelProperty(value = "订单ID", required = true)
    private Long order_id;

    @ApiModelProperty(value = "快递公司")
    private String express_name;

    @ApiModelProperty(value = "快递公司编号")
    private String express_id;

    @ApiModelProperty(value = "快递单号")
    private String express_code;
}
