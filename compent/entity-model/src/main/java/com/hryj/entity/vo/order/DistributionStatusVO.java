package com.hryj.entity.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author 叶方宇
 * @className: DistributionStatusVO
 * @description:
 * @create 2018/7/5 0005 9:26
 **/
@Data
@ApiModel(value = "配送单状态操作表")
public class DistributionStatusVO {

    @ApiModelProperty(value = "订单编号",required = true)
    private Long order_id;

    @ApiModelProperty(value = "配送单状态:01待分配,02待配送,03配送完成,04配送超时,05取消配送",required = true)
    private String distribution_status;

    @ApiModelProperty(value = "配送单类别:01-送货,02-取货",required = true)
    private String distribution_type;

    @ApiModelProperty(value = "实际送达截止时间")
    private Date actual_delivery_end_time;
}
