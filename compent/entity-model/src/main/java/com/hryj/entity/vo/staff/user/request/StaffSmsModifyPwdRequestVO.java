package com.hryj.entity.vo.staff.user.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: StaffSmsModifyPwdRequestVO
 * @description: 短信验证修改密码请求VO
 * @create 2018/7/5 10:01
 **/
@Data
@ApiModel(value = "短信验证修改密码请求VO")
public class StaffSmsModifyPwdRequestVO extends RequestVO {

    @ApiModelProperty(value = "员工账号", required = true)
    private String staff_account;

    @ApiModelProperty(value = "验证码", required = true)
    private String verify_code;

    @ApiModelProperty(value = "新密码", required = true)
    private String new_login_pwd;
}
