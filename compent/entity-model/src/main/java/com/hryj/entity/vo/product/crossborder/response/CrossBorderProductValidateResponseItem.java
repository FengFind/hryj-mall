package com.hryj.entity.vo.product.crossborder.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 王光银
 * @className: CrossBorderProductValidateResponseItem
 * @description:
 * @create 2018/9/13 0013 15:19
 **/
@Data
@ApiModel(value = "跨境商品验证响应条目", description = "跨境商品验证响应条目")
public class CrossBorderProductValidateResponseItem {

    @ApiModelProperty(value = "HSCODE对应的税率数据ID")
    private Long tax_id;

    @ApiModelProperty(value = "净含量值")
    private BigDecimal unit_1;

    @ApiModelProperty(value = "净含量单位")
    private String unit_2;

    @ApiModelProperty(value = "HSCODE")
    private String hs_code;

    @ApiModelProperty(value = "消费税率")
    private BigDecimal consume_tax;

    @ApiModelProperty(value = "增值税率")
    private BigDecimal increment_tax;

    @ApiModelProperty(value = "第三方SKU-ID")
    private String third_sku_id;

}
