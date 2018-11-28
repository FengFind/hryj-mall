package com.hryj.entity.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 叶方宇
 * @className: OrderActivityInfoVO
 * @description:
 * @create 2018/7/18 0018 9:05
 **/
@Data
@ApiModel(value = "活动信息")
public class OrderActivityInfoVO {

    @ApiModelProperty(value = "门店ID", required = true)
    private Long party_id;

    @ApiModelProperty(value = "活动名称", required = true)
    private String activity_name;

    @ApiModelProperty(value = "活动类型", required = true)
    private String activity_type;

    @ApiModelProperty(value = "活动价格", required = true)
    private BigDecimal activity_price;

    @ApiModelProperty(value = "活动ID", required = true)
    private Long activity_id;
}
