package com.hryj.entity.vo.user.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: UserPhoneRequestVO
 * @description: 用户手机号请求VO
 * @create 2018/7/5 10:21
 **/
@Data
@ApiModel(value="用户手机号请求VO")
public class UserPhoneRequestVO extends RequestVO {

    @ApiModelProperty(value = "手机号码", required = true)
    private String phone_num;
}
