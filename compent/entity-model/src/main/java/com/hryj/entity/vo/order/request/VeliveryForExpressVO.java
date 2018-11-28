package com.hryj.entity.vo.order.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 叶方宇
 * @className: VeliveryForExpressVO
 * @description:
 * @create 2018/7/5 0005 9:16
 **/

@Data
@ApiModel(value = "发货录入快递信息")
public class VeliveryForExpressVO  extends RequestVO {

    @ApiModelProperty(value = "订单编号",required = true)
    private Long order_id;

    @ApiModelProperty(value = "物流公司",required = true)
    private String express_agency;

    @ApiModelProperty(value = "快递单号",required = true)
    private String express_code;

    @ApiModelProperty(value = "用户ID",required = true)
    private Long user_id;

    @ApiModelProperty(value = "用户姓名",required = true)
    private String user_name;

    @ApiModelProperty(value = "用户电话",required = true)
    private String user_phone;

    @ApiModelProperty(value = "用户地址",required = true)
    private String user_address;
}
