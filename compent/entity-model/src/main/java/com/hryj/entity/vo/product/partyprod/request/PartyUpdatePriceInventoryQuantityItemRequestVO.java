package com.hryj.entity.vo.product.partyprod.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 王光银
 * @className: PartyUpdatePriceInventoryQuantityItemRequestVO
 * @description: 门店仓库商品维护请求VO
 * @create 2018/6/27 0027 17:57
 **/
@ApiModel(value = "门店仓库商品维护请求VO", description = "门店仓库商品维护请求VO")
@Data
public class PartyUpdatePriceInventoryQuantityItemRequestVO extends RequestVO {

    @ApiModelProperty(value = "门店仓库商品ID，不是商品ID，请注意", required = true)
    private Long party_product_id;

    @ApiModelProperty(value = "销售价格, 仓库时可以维护销售价格，门店时不能维护销售价格")
    private BigDecimal sale_price;

    @ApiModelProperty(value = "库存量", required = true)
    private Integer inventory_quantity;

    @ApiModelProperty(value = "销售推介日期")
    private String introduction_date;

    @ApiModelProperty(value = "销售终止日期")
    private String sales_end_date;

}
