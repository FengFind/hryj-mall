package com.hryj.entity.vo.promotion.activity.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author 汪豪
 * @className: ActivityInProgressProductItemResponseVO
 * @description:
 * @create 2018/7/10 0010 13:46
 **/
@ApiModel(value = "在活动进行中的商品详情条目响应VO", description = "在活动进行中的商品详情条目响应VO")
@Data
public class ActivityInProgressProductItemResponseVO {

    @ApiModelProperty(value = "活动状态，1有效，其他任意值无效")
    private Integer activity_status;

    @ApiModelProperty(value = "活动ID")
    private Long activity_id;

    @ApiModelProperty(value = "商品ID")
    private Long product_id;

    @ApiModelProperty(value = "活动促销价格")
    private BigDecimal promotion_price;

    @ApiModelProperty(value = "活动类型，例如:01-爆款,02-团购")
    private String activity_type;

    @ApiModelProperty(value = "活动类型名称")
    private String activity_type_name;

    @ApiModelProperty(value = "活动名称")
    private String activity_name;

    @ApiModelProperty(value = "活动角标图")
    private String activity_mark_image;

    @ApiModelProperty(value = "活动开始时间，格式yyyy-MM-dd HH:mm:ss")
    private Date start_date;

    @ApiModelProperty(value = "活动结束时间，格式yyyy-MM-dd HH:mm:ss")
    private Date end_date;

    @ApiModelProperty(value = "活动专场页面URL")
    private String activity_page_url;

    @ApiModelProperty(value = "活动模板格式介绍说明")
    private String activity_model_desc;
}
