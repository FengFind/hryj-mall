package com.hryj.entity.vo.order.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 罗秋涵
 * @className: DistributionDetailRequestVO
 * @description: 确认配送完成VO
 * @create 2018/7/5 0005 11:09
 **/
@Data
@ApiModel(value = "确认配送完成VO")
public class DistributionDetailRequestVO extends RequestVO {

    @ApiModelProperty(value = "配送单id",required = true)
    private Long distribution_id;

    @ApiModelProperty(value="订单id",required = true)
    private Long order_id;

    @ApiModelProperty(value = "退货单状态：02-同意退货,03-取消退货,04-拒绝退货",required = true)
    private String return_status;

    @ApiModelProperty(value = "配送类别:01-送货,02-取货", required = true)
    private String distribution_type;

}
