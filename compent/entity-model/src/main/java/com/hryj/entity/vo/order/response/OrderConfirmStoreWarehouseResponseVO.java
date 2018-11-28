package com.hryj.entity.vo.order.response;

import com.hryj.entity.vo.order.OrderProductItemVO;
import com.hryj.entity.vo.order.response.opentime.OpenTimeResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 白飞
 * @className: OrderConfirmStoreWarehouseResponseVO
 * @description: 订单结算-门店或仓库信息集合
 * @create 2018/8/21 0002 14:05
 **/
@Data
@ApiModel(value = "订单结算-门店或仓库信息集合-v1.2")
public class OrderConfirmStoreWarehouseResponseVO {

    @ApiModelProperty(value = "仓库或门店id", required = true)
    private Long party_id;

    @ApiModelProperty(value = "部门类型", notes = "01-门店,02-仓库", required = true)
    private String dept_type;

    @ApiModelProperty(value = "仓库或门店名称", required = true)
    private String party_name;

    @ApiModelProperty(value = "仓库或门店联系人姓名")
    private String party_contact_name;

    @ApiModelProperty(value = "仓库或门店联系人电话")
    private String party_contact_phone;

    @ApiModelProperty(value = "仓库或门店地址")
    private String party_address;

    @ApiModelProperty(value = "送货上门的日期", notes = "Map<当前日期及向上六天,List<每个日期对应的时间段>>")
    private List<OpenTimeResponse> delivery_time;

    @ApiModelProperty(value = "送货上门的说明")
    private String delivery_info;

    @ApiModelProperty(value = "订单金额")
    private String order_amount;

    @ApiModelProperty(value = "优惠金额")
    private String discount_amount;

    @ApiModelProperty(value = "单个门店或仓库的综合税费")
    private BigDecimal tax;

    @ApiModelProperty(value = "商品列表")
    private List<OrderProductItemVO> product_list;

    @ApiModelProperty(value = "购买文字提示")
    private String buy_tips;
}
