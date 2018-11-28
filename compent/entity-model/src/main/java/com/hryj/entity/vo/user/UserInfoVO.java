package com.hryj.entity.vo.user;

import com.hryj.entity.bo.user.UserAddress;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: UserInfoVO
 * @description: 用户基本信息VO
 * @create 2018/6/27 21:55
 **/
@Data
@ApiModel(value="用户基本信息VO")
public class UserInfoVO {

    @ApiModelProperty(value = "用户id", required = true)
    private Long user_id;

    @ApiModelProperty(value = "手机号码", required = true)
    private String phone_num;

    @ApiModelProperty(value = "用户姓名", required = true)
    private String user_name;

    @ApiModelProperty(value = "注册时间", required = true)
    private String register_time;

    @ApiModelProperty(value = "推荐码", required = true)
    private String referral_code;

    @ApiModelProperty(value = "推荐人姓名", required = true)
    private String staff_name;

    @ApiModelProperty(value = "推荐电话", required = true)
    private String staff_phone;

    @ApiModelProperty(value = "推荐人岗位名称", required = true)
    private String staff_job_name;

    @ApiModelProperty(value = "推荐门店", required = true)
    private String store_name;

    @ApiModelProperty(value = "用户地址id", required = true)
    private Long address_id;

    @ApiModelProperty(value = "配送地址", required = true)
    private String receive_address;

    @ApiModelProperty(value = "用户默认地址信息", required = true)
    private UserAddress userAddress;

    @ApiModelProperty(value = "地址经纬度", required = true)
    private String locations;
}
