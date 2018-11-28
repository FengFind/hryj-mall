package com.hryj.entity.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: UserTradeVO
 * @description: 用户交易VO
 * @create 2018/7/9 10:23
 **/
@Data
@ApiModel(value = "用户交易VO")
public class UserTradeVO {

    @ApiModelProperty(value = "用户id", required = true)
    private Long user_id;

    @ApiModelProperty(value = "用户姓名", required = true)
    private String user_name;

    @ApiModelProperty(value = "用户手机号", required = true)
    private String user_phone;

    @ApiModelProperty(value = "交易金额", required = true)
    private String trade_amt;
}
