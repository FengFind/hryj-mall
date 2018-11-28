package com.hryj.entity.vo.order.response;

import com.hryj.entity.vo.order.UserReceiveAddressVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 罗秋涵
 * @className: ImmediateBuyResponseVO
 * @description: 立即购买，订单确认
 * @create 2018/7/9 0009 14:28
 **/
@Data
@ApiModel(value = "立即购买——订单确认响应VO")
public class ImmediateBuyResponseVO {


    @ApiModelProperty(value = "用户收货地址", required = true)
    private UserReceiveAddressVO userReceiveAddressVO;

    @ApiModelProperty(value = "立即下单商品信息", required = true)
    private ImmediateBuyProductResponseVO productInfo;

}
