package com.hryj.entity.vo.product.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 汪豪
 * @className: ProductHSCodeResponseVO
 * @description:
 * @create 2018/9/11 0011 8:55
 **/
@ApiModel(value = "商品基本税率集合响应VO", description = "商品基本税率集合响应VO")
@Data
public class ProductTaxRateResponseVO {

    @ApiModelProperty(value = "税率id")
    private Long tax_rate_id;

    @ApiModelProperty(value = "HSCode")
    private String hs_code;

    @ApiModelProperty(value = "商品编码8位")
    private String goods_code;

    @ApiModelProperty(value = "商品编码附加码")
    private String goods_code_add;

    @ApiModelProperty(value = "商品名称")
    private String goods_name;

    @ApiModelProperty(value = "关税-低税率")
    private BigDecimal low_rate;

    @ApiModelProperty(value = "关税-高税率")
    private BigDecimal high_rate;

    @ApiModelProperty(value = "消费税率")
    private BigDecimal consume_tax;

    @ApiModelProperty(value = "增值税率")
    private BigDecimal increment_tax;

    @ApiModelProperty(value = "第一法定单位")
    private String unit_1;

    @ApiModelProperty(value = "第二法定单位")
    private String unit_2;

    @ApiModelProperty(value = "备注")
    private String note;

    @ApiModelProperty(value = "行邮税率")
    private BigDecimal shuilv;

    @ApiModelProperty(value = "是否有消费税，Y有，N没有，默认N")
    private String has_consume_tax;
}
