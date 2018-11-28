package com.hryj.entity.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: OrderUserVO
 * @description: 订单用户信息
 * @create 2018/6/30 17:12
 **/
@Data
@ApiModel(value = "订单用户信息")
public class OrderUserVO {

    @ApiModelProperty(value = "用户姓名", required = true)
    private String user_name;

    @ApiModelProperty(value = "用户电话", required = true)
    private String user_phone;

    @ApiModelProperty(value = "用户地址", required = true)
    private String user_address;

    @ApiModelProperty(value = "推荐人姓名", required = true)
    private String staff_name;

    @ApiModelProperty(value = "推荐码", required = true)
    private String referral_code;
}
