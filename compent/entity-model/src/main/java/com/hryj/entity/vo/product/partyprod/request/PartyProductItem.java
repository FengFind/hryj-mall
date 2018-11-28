package com.hryj.entity.vo.product.partyprod.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 王光银
 * @className: PartyProductItem
 * @description:
 * @create 2018/7/7 0007 10:57
 **/
@ApiModel(value = "门店仓库商品条目", description = "门店仓库商品条目")
@Data
public class PartyProductItem {

    @ApiModelProperty(value = "门店仓库商品ID，当门店从仓库选择商品进行销售时需要")
    private Long party_product_id;

    @ApiModelProperty(value = "商品ID, 当仓库从商品中心池选择商品销售时需要", required = true)
    private Long product_id;

    @ApiModelProperty(value = "销售价格, 当事组织为仓库时可以维护销售价格，门店时不能维护销售价格")
    private BigDecimal sale_price;

    @ApiModelProperty(value = "库存量")
    private Integer inventory_quantity;

    @ApiModelProperty(value = "推介销售日期，缺省为添加时的日期")
    private String introduction_date;

    @ApiModelProperty(value = "销售终止日期，缺省空表示永久销售")
    private String sales_end_date;
}
