package com.hryj.entity.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: OrderStatusRecordVO
 * @description: 订单状态记录VO
 * @create 2018/6/30 17:05
 **/
@Data
@ApiModel(value = "订单状态记录VO")
public class OrderStatusRecordVO {

    @ApiModelProperty(value = "订单状态", required = true)
    private String order_status;

    @ApiModelProperty(value = "记录时间", required = true)
    private String record_time;

    @ApiModelProperty(value = "状态说明", required = true)
    private String status_remark;

    @ApiModelProperty(value = "操作人id", required = true)
    private Long operator_id;

    @ApiModelProperty(value = "操作人姓名", required = true)
    private String operator_name;

    @ApiModelProperty(value = "当前状态", required = true)
    private String change_reason;

    @ApiModelProperty(value = "耗时", required = true)
    private String take_time;

    @ApiModelProperty(value = "用户编号", hidden = true)
    private Long user_id;

    @ApiModelProperty(value = "订单编号", hidden = true)
    private Long order_id;

    @ApiModelProperty(value = "操作用户类型", hidden = true)
    private String operator_user_type;

    @ApiModelProperty(value = "耗时", hidden = true)
    private Integer consume_time;
}
