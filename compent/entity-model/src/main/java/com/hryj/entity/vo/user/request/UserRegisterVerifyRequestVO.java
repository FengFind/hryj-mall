package com.hryj.entity.vo.user.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: UserRegisterVerifyRequestVO
 * @description: 用户注册验证请求VO
 * @create 2018/8/16 11:38
 **/
@Data
@ApiModel(value="用户注册验证请求VO")
public class UserRegisterVerifyRequestVO extends RequestVO {

    @ApiModelProperty(value = "手机号码", required = true)
    private String phone_num;

    @ApiModelProperty(value = "验证码", required = true)
    private String verify_code;

    @ApiModelProperty(value = "推荐码")
    private String referral_code;
}
