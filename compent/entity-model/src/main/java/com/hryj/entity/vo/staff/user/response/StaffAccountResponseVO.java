package com.hryj.entity.vo.staff.user.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: StaffAccountResponseVO
 * @description: 添加员工响应VO
 * @create 2018-07-03 19:04
 **/
@Data
@ApiModel(value = "添加员工响应VO")
public class StaffAccountResponseVO {

    @ApiModelProperty(value = "员工账号")
    private String staff_account;

    @ApiModelProperty(value = "密码")
    private String login_pwd;


}
