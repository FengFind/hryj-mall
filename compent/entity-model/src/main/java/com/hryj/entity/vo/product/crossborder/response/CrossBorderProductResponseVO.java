package com.hryj.entity.vo.product.crossborder.response;

import com.hryj.entity.vo.product.common.response.ProductMadeWhere;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 王光银
 * @className: CrossBorderProductRequestVO
 * @description:
 * @create 2018/9/11 0011 9:22
 **/
@Data
@ApiModel(value = "跨境商品数据VO", description = "跨境商品数据VO")
public class CrossBorderProductResponseVO {

    @ApiModelProperty(value = "发货仓库编码")
    private String channel;

    @ApiModelProperty(value = "发货仓库名称")
    private String channel_name;

    @ApiModelProperty(value = "第三方SKU ID")
    private String third_sku_id;

    @ApiModelProperty(value = "净含量值")
    private BigDecimal unit_1;

    @ApiModelProperty(value = "净含量单位")
    private String unit_2;

    @ApiModelProperty(value = "HSCODE")
    private String hs_code;

    @ApiModelProperty(value = "库存")
    private Integer inventory_quantity;

    @ApiModelProperty(value = "报关价")
    private String declare_price;

    @ApiModelProperty(value = "启运地")
    private ProductMadeWhere shipment_from;
}
