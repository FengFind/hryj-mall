package com.hryj.entity.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 叶方宇
 * @className: OrderSelfPickUserVo
 * @description:
 * @create 2018/7/11 0011 20:22
 **/
@Data
@ApiModel(value = "根据订单id查询门店信息和用户信息")
public class OrderSelfPickUserVO {

    @ApiModelProperty(value = "订单id", required = true)
    private Long order_id;

    @ApiModelProperty(value = "用户姓名", required = true)
    private String user_name;

    @ApiModelProperty(value = "用户电话", required = true)
    private String user_phone;

    @ApiModelProperty(value = "用户地址", required = true)
    private String user_address;

    @ApiModelProperty(value = "自提地址", required = true)
    private String self_pick_address;

    @ApiModelProperty(value = "自提联系人", required = true)
    private String self_pick_contact;

    @ApiModelProperty(value = "自提联系电话", required = true)
    private String self_pick_phone;
}
