package com.hryj.entity.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: UserReceiveAddressVO
 * @description: 用户收货地址
 * @create 2018/6/29 19:51
 **/
@Data
@ApiModel(value = "用户收货地址")
public class UserReceiveAddressVO {

    @ApiModelProperty(value = "收货地址id", required = true)
    private Long address_id;

    @ApiModelProperty(value = "收货人姓名", required = true)
    private String receive_name;

    @ApiModelProperty(value = "收货人电话", required = true)
    private String receive_phone;

    @ApiModelProperty(value = "收货电话", required = true)
    private String receive_address;
}
