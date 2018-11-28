package com.hryj.entity.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author 李道云
 * @className: OrderListInfoVO
 * @description: 订单列表信息VO
 * @create 2018/6/29 22:11
 **/
@Data
@ApiModel(value = "订单列表信息VO")
public class OrderListInfoForStoreVO {

    @ApiModelProperty(value = "订单id", required = true)
    private Long order_id;

    @ApiModelProperty(value = "订单编号", required = true)
    private Long order_num;

    @ApiModelProperty(value = "订单状态", notes = "01-待支付,02-待发货,03-待自提,04-已发货,05-退货申请中,06-退货成功,07-已取消,08-已完成", required = true)
    private String order_status;

    @ApiModelProperty(value = "门店仓库名称", required = true)
    private String party_name;

    @ApiModelProperty(value = "配送方式:01-自提,02-送货上门,03-快递", required = true)
    private String delivery_type;

    @ApiModelProperty(value = "用户id", required = true)
    private Long user_id;

    @ApiModelProperty(value = "用户姓名", required = true)
    private String user_name;

    @ApiModelProperty(value = "用户电话", required = true)
    private String user_phone;

    @ApiModelProperty(value = "用户地址", required = true)
    private String user_address;

    @ApiModelProperty(value = "创建时间", required = true)
    private Date create_time;

}
