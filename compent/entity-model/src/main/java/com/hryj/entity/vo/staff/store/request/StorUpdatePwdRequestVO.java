package com.hryj.entity.vo.staff.store.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: StorUpdatePwdRequestVO
 * @description:
 * @create 2018/7/4 0004-10:47
 **/
@Data
@ApiModel(value = "员工修改密码请求VO")
public class StorUpdatePwdRequestVO extends RequestVO {


    @ApiModelProperty(value = "旧密码", required = true)
    private String old_login_pwd;


    @ApiModelProperty(value = "新密码", required = true)
    private String new_login_pwd;
}
