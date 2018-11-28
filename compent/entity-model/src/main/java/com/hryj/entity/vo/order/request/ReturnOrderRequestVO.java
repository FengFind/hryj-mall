package com.hryj.entity.vo.order.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 罗秋涵
 * @className: ReturnOrderRequest
 * @description: 退取消参数VO
 * @create 2018/7/3 0003 9:49
 **/
@Data
@ApiModel(value = "退取消参数VO")
public class ReturnOrderRequestVO extends RequestVO {


    @ApiModelProperty(value = "订单编号", required = true)
    private Long order_id;

    @ApiModelProperty(value = "退货申请原因", required = true)
    private String return_reason;

    @ApiModelProperty(value = "退货上传图片路径用逗号隔开", required = true)
    private String return_image;

}
