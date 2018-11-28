package com.hryj.entity.vo.user.response;

import com.hryj.entity.vo.user.UserPartyVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: UserLoginResponseVO
 * @description: 用户登录响应VO
 * @create 2018/6/26 10:38
 **/
@Data
@ApiModel(value="用户登录响应VO")
public class UserLoginResponseVO {

    @ApiModelProperty(value = "登录token", required = true)
    private String login_token;

    @ApiModelProperty(value = "手机号码", required = true)
    private String phone_num;

    @ApiModelProperty(value = "推荐码")
    private String referral_code;

    @ApiModelProperty(value = "用户默认门店或仓库", notes = "可能没有默认值")
    private UserPartyVO defaultParty;
}
