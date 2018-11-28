package com.hryj.entity.vo.user.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: UserLoginRequestVO
 * @description: 用户登录请求VO
 * @create 2018/6/26 10:38
 **/
@Data
@ApiModel(value="用户登录请求VO")
public class UserLoginRequestVO extends RequestVO {

    @ApiModelProperty(value = "手机号码", required = true)
    private String phone_num;

    @ApiModelProperty(value = "验证码", required = true)
    private String verify_code;
}
