package com.hryj.entity.vo.order.response;

import com.hryj.entity.vo.order.OrderPorductVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author 李道云
 * @className: SelfPickResponseVO
 * @description: 自提单响应信息
 * @create 2018/6/30 15:27
 **/
@Data
@ApiModel(value = "自提码自提单响应信息")
public class SelfPickResponseVO {

    @ApiModelProperty(value = "订单编号", required = true)
    private Long order_id;

    @ApiModelProperty(value = "自提单id", required = true)
    private String self_pick_id;

    @ApiModelProperty(value = "自提码", required = true)
    private String self_pick_code;

    @ApiModelProperty(value = "下单时间", required = true)
    private Date create_time;

    @ApiModelProperty(value = "下单时间戳", required = true)
    private Long create_timestamp;

    @ApiModelProperty(value = "用户姓名", required = true)
    private String user_name;

    @ApiModelProperty(value = "用户电话", required = true)
    private String user_phone;

    @ApiModelProperty(value = "用户地址", required = true)
    private String user_address;

    @ApiModelProperty(value = "商品列表", required = true)
    private List<OrderPorductVO> orderProductList;
}
