package com.hryj.entity.vo.product.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: ProdPromResponseVO
 * @description: 商品活动信息响应VO
 * @create 2018/6/27 0027 10:14
 **/
@ApiModel(value = "商品活动信息响应VO", description = "商品活动信息响应VO")
@Data
public class ProdPromResponseVO {

    @ApiModelProperty(value = "活动ID")
    private String activity_id;

    @ApiModelProperty(value = "活动类型，例如:01-爆款,02-团购")
    private String activity_type;

    @ApiModelProperty(value = "活动类型名称")
    private String activity_type_name;

    @ApiModelProperty(value = "活动名称")
    private String activity_name;

    @ApiModelProperty(value = "活动角标图")
    private String activity_mark_image;

    @ApiModelProperty(value = "促销活动价")
    private String promotion_price;

    @ApiModelProperty(value = "活动开始时间，格式yyyy-MM-dd HH:mm")
    private String start_date;

    @ApiModelProperty(value = "活动结束时间，格式yyyy-MM-dd HH:mm")
    private String end_date;

    @ApiModelProperty(value = "活动专场页面URL")
    private String activity_page_url;

    @ApiModelProperty(value = "活动模板格式介绍说明")
    private String activity_model_desc;

}
