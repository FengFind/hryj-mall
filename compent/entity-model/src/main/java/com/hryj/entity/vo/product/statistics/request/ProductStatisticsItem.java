package com.hryj.entity.vo.product.statistics.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 王光银
 * @className: ProductStatisticsItem
 * @description: 商品统计请求VO
 * @create 2018/7/4 0004 11:32
 **/
@ApiModel(value = "商品统计条目", description = "商品统计条目")
@Data
public class ProductStatisticsItem extends RequestVO {

    @ApiModelProperty(value = "商品ID", required = true)
    private Long product_id;

    @ApiModelProperty(value = "门店或仓库ID", required = true)
    private Long party_id;

    @ApiModelProperty(value = "数量", required = true)
    private BigDecimal quantity;
}
