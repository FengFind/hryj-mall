package com.hryj.entity.vo.staff.user.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: StaffModifyPwdRequestVO
 * @description: 修改密码请求VO
 * @create 2018/06/27 21:51
 **/
@Data
@ApiModel(value = "修改密码请求VO")
public class StaffModifyPwdRequestVO extends RequestVO {

    @ApiModelProperty(value = "旧密码", required = true)
    private String old_login_pwd;

    @ApiModelProperty(value = "新密码", required = true)
    private String new_login_pwd;

}
