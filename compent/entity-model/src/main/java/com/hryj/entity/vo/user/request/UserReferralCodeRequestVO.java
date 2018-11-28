package com.hryj.entity.vo.user.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: UserReferralCodeRequestVO
 * @description: 用户推荐码设置请求VO
 * @create 2018/8/28 17:35
 **/
@Data
@ApiModel(value="用户推荐码设置请求VO")
public class UserReferralCodeRequestVO extends RequestVO {

    @ApiModelProperty(value = "推荐码", required = true)
    private String referral_code;
}
