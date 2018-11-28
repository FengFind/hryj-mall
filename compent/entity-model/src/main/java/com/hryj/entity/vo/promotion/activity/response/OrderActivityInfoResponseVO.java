package com.hryj.entity.vo.promotion.activity.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 汪豪
 * @className: OrderActivityInfoVO
 * @description:
 * @create 2018/8/16 0016 9:00
 **/
@ApiModel(value = "活动商品简单信息响应VO", description = "活动商品简单信息响应VO")
@Data
public class OrderActivityInfoResponseVO {

    @ApiModelProperty(value = "活动id")
    private Long activity_id;

    @ApiModelProperty(value = "商品id")
    private Long product_id;

    @ApiModelProperty(value = "商品活动价格")
    private BigDecimal activity_price;

    @ApiModelProperty(value = "活动名称")
    private String activity_name;

    @ApiModelProperty(value = "活动类型:01-爆款,02-团购")
    private String activity_type;

    @ApiModelProperty(value = "活动角标图")
    private String activity_mark_image;

    @ApiModelProperty(value = "门店或仓库id")
    private Long party_id;
}
