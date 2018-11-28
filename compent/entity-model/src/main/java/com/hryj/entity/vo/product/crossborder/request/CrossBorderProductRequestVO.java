package com.hryj.entity.vo.product.crossborder.request;

import com.hryj.entity.bo.product.crossborder.CrossBorderProduct;
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
public class CrossBorderProductRequestVO {

    @ApiModelProperty(value = "发货仓库", required = true)
    private String channel;

    @ApiModelProperty(value = "第三方SKU ID", required = true)
    private String third_sku_id;

    @ApiModelProperty(value = "净含量值, 整数部分最多6位，小数部分最多两位")
    private BigDecimal unit_1;

    @ApiModelProperty(value = "净含量单位")
    private String unit_2;

    @ApiModelProperty(value = "HSCODE", required = true)
    private String hs_code;

    @ApiModelProperty(value = "库存", required = true)
    private Integer inventory_quantity;

    @ApiModelProperty(value = "报关价", required = true)
    private BigDecimal declare_price;

    @ApiModelProperty(value = "启动国")
    private Long shipment_from;

    public CrossBorderProduct convertTo() {
        CrossBorderProduct crossBorderProduct = new CrossBorderProduct();
        crossBorderProduct.setInventory_quantity(this.inventory_quantity);
        crossBorderProduct.setChannel(this.channel);
        crossBorderProduct.setHs_code(this.hs_code);
        crossBorderProduct.setDeclare_price(this.declare_price);
        crossBorderProduct.setThird_sku_id(this.third_sku_id);
        crossBorderProduct.setShipment_from(this.shipment_from);
        crossBorderProduct.setUnit_1(this.unit_1);
        crossBorderProduct.setUnit_2(this.unit_2);
        return crossBorderProduct;
    }
}
