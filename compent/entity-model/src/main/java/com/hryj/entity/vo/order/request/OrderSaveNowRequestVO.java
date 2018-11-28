package com.hryj.entity.vo.order.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author 叶方宇
 * @className: OrderSaveNowRequestVO
 * @description:  立即购买生成订单的请求参数
 * @create 2018-07-07 13:52
 **/
@Data
@ApiModel(value = "订单保存请求VO")
public class OrderSaveNowRequestVO extends RequestVO {

    @ApiModelProperty(value = "用户id", notes = "代下单功能时有值，需要知道是谁的订单")
    private Long user_id;

    @ApiModelProperty(value = "门店编号", required = true)
    private Long party_id;

    @ApiModelProperty(value = "商品编号", required = true)
    private Long product_id;

    @ApiModelProperty(value = "收货地址id", required = true)
    private Long address_id;

    @ApiModelProperty(value = "配送方式", notes = "01-自提,02-送货上门,03-快递", required = true)
    private String delivery_type;

    @ApiModelProperty(value = "期望送达开始时间", notes = "时间格式: yyyy-MM-dd HH:mm:SS", required = true)
    private Date hope_delivery_start_time;

    @ApiModelProperty(value = "期望送达截止时间", notes = "时间格式: yyyy-MM-dd HH:mm:SS", required = true)
    private Date hope_delivery_end_time;

    @ApiModelProperty(value = "商品数量", required = true)
    private Integer quantity;

    @ApiModelProperty(value = "活动id")
    private Long activity_id;

}
