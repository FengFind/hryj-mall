package com.hryj.entity.vo.order.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: OrderListRequestVO
 * @description: 订单列表查询VO
 * @create 2018-06-30 16:19
 **/
@Data
@ApiModel(value = "订单列表查询VO")
public class OrderListRequestVO extends RequestVO {

    @ApiModelProperty(value = "用户id", notes = "代下单功能时有值，需要知道是谁的订单")
    private Long user_id;

    @ApiModelProperty(value = "代下单员工编号(不需要传，后台根据token获取)")
    private Long help_staff_id;

    @ApiModelProperty(value = "订单状态")
    private String order_status;

    @ApiModelProperty(value = "页码", required = false)
    private Integer page_num = 1;

    @ApiModelProperty(value = "每页大小", required = false)
    private Integer page_size = 10;
}
