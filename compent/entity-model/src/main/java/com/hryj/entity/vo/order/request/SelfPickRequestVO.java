package com.hryj.entity.vo.order.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 罗秋涵
 * @className: SelfPickRequestVO
 * @description: 自提管理VO
 * @create 2018/7/5 0005 11:36
 **/

@Data
@ApiModel(value = "自提管理VO，两个参数根据实际情况选填，但必须填一个")
public class SelfPickRequestVO extends RequestVO {

    @ApiModelProperty(value = "自提码")
    private String  self_pick_code;

    @ApiModelProperty(value = "用户手机号")
    private String phone_num;
}
