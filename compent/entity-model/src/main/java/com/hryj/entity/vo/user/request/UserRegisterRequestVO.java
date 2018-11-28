package com.hryj.entity.vo.user.request;

import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.user.UserAddressVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: UserRegisterRequestVO
 * @description: 用户注册请求VO
 * @create 2018/6/26 9:41
 **/
@Data
@ApiModel(value="用户注册请求VO")
public class UserRegisterRequestVO extends RequestVO {

    @ApiModelProperty(value = "手机号码", required = true)
    private String phone_num;

    @ApiModelProperty(value = "验证码")
    private String verify_code;

    @ApiModelProperty(value = "推荐码")
    private String referral_code;

    @ApiModelProperty(value = "用户地址VO", required = true)
    private UserAddressVO userAddressVO;
}
