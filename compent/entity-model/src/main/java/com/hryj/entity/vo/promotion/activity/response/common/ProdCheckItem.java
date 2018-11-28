package com.hryj.entity.vo.promotion.activity.response.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

/**
 * @author 王光银
 * @className: ProdCheckItem
 * @description: 从商品的角度检查促销活动响应条目VO
 * @create 2018/7/10 0010 18:02
 **/
@ApiModel(value = "从商品的角度检查促销活动响应条目VO", description = "从商品的角度检查促销活动响应条目VO")
@Data
public class ProdCheckItem {

    @ApiModelProperty(value = "商品ID")
    private Long product_id;

    @ApiModelProperty(value = "活动ID")
    private Long activity_id;

    @ApiModelProperty(value = "活动名称")
    private String activity_name;

    @ApiModelProperty(value = "活动类型:01-爆款,02-团购")
    private String activity_type;

    @ApiModelProperty(value = "活动开始时间")
    private Date start_date;

    @ApiModelProperty(value = "活动结束时间")
    private Date end_date;

    @ApiModelProperty(value = "活动角标图URL")
    private String activity_mark_image;

    @ApiModelProperty(value = "活动TITLE图URL")
    private String activity_image;

    @ApiModelProperty(value = "活动价")
    private BigDecimal activity_price;

    @ApiModelProperty(value = "活动模板")
    private String templete_data;

    @ApiModelProperty(value = "活动的应用范围当事组织(门店或仓库)ID")
    private Set<Long> scope_party;
}
