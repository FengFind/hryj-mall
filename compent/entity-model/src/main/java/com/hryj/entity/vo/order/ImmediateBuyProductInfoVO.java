package com.hryj.entity.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 罗秋涵
 * @className: ImmediateBuyProductInfoVO
 * @description: 立即购买商品信息
 * @create 2018/7/9 0009 14:40
 **/

@ApiModel(value = "商品信息")
@Data
public class ImmediateBuyProductInfoVO {

    @ApiModelProperty(value = "商品id", required = true)
    private Long product_id;

    @ApiModelProperty(value = "商品名称", required = true)
    private String product_name;

    @ApiModelProperty(value = "商品图片", required = true)
    private String list_image_url;

    @ApiModelProperty(value = "销售价格", required = true)
    private String sale_price;

    @ApiModelProperty(value = "数量", required = true)
    private Integer quantity;

    @ApiModelProperty(value = "活动id")
    private Long activity_id;

    @ApiModelProperty(value = "活动名称")
    private String activity_name;

    @ApiModelProperty(value = "活动类型", notes = "01-爆款,02-团购")
    private String activity_type;

    @ApiModelProperty(value = "活动角标图")
    private String activity_mark_image;

    @ApiModelProperty(value = "活动价格")
    private String activity_price;

    @ApiModelProperty(value = "优惠金额")
    private String discount_amt="0";

}
