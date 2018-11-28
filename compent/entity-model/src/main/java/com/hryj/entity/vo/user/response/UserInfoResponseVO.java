package com.hryj.entity.vo.user.response;

import com.hryj.entity.vo.user.UserPartyVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: UserInfoResponseVO
 * @description: 用户基本信息响应VO
 * @create 2018/8/29 9:30
 **/
@Data
@ApiModel(value="用户基本信息响应VO")
public class UserInfoResponseVO {

    @ApiModelProperty(value = "手机号码", required = true)
    private String phone_num;

    @ApiModelProperty(value = "推荐码")
    private String referral_code;

    @ApiModelProperty(value = "收货人", required = true)
    private String receive_name;

    @ApiModelProperty(value = "收货手机号", required = true)
    private String receive_phone;

    @ApiModelProperty(value = "收货地址", required = true)
    private String receive_address;

    @ApiModelProperty(value = "位置坐标,经纬度", required = true)
    private String locations;

    @ApiModelProperty(value = "用户默认门店或仓库")
    private UserPartyVO defaultParty;
}
