package com.hryj.entity.vo.order.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 叶方宇
 * @className: AdminOorderReturnResponseVO
 * @description:
 * @create 2018/7/3 0003 20:37
 **/

@Data
@ApiModel(value = "订单退货信息VO")
public class AdminOrderReturnResponseVO {


    @ApiModelProperty(value="退货单id")
    private String return_id;

    @ApiModelProperty(value="退货单类别")
    private String return_type;

    @ApiModelProperty(value="退货申请原因",required = true)
    private String return_reason;

    @ApiModelProperty(value="退货说明",required = true)
    private String return_remark;

    @ApiModelProperty(value="快递公司")
    private String express_agency;

    @ApiModelProperty(value="快递单号")
    private String express_code;

    @ApiModelProperty(value="图片")
    private String return_image;
}
