package com.hryj.entity.vo.user.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: UserSearchResponseVO
 * @description: 用户搜索响应VO
 * @create 2018/7/5 10:24
 **/
@Data
@ApiModel(value="用户搜索响应VO")
public class UserSearchResponseVO{

    @ApiModelProperty(value = "用户id", required = true)
    private Long user_id;

    @ApiModelProperty(value = "用户手机号", required = true)
    private String phone_num;

    @ApiModelProperty(value = "收货人姓名", required = true)
    private String receive_name;

    @ApiModelProperty(value = "收货人手机号", required = true)
    private String receive_phone;

    @ApiModelProperty(value = "收货地址", required = true)
    private String receive_address;

}
