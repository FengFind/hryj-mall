package com.hryj.entity.vo.staff.user.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: StaffUserAppTokenVO
 * @description: 员工登录请求VO
 * @create 2018/06/27 21:51
 **/
@Data
@ApiModel(value = "员工登录请求VO")
public class StaffVerifyCodeLoginRequestVO extends RequestVO {

    @ApiModelProperty(value = "员工账号", required = true)
    private String staff_account;

    @ApiModelProperty(value = "登录密码", required = true)
    private String login_pwd;





}
