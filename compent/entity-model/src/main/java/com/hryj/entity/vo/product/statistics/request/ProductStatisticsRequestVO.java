package com.hryj.entity.vo.product.statistics.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: ProductStatisticsItem
 * @description: 商品统计请求VO
 * @create 2018/7/4 0004 11:32
 **/
@ApiModel(value = "商品统计请求VO", description = "商品统计请求VO")
@Data
public class ProductStatisticsRequestVO extends RequestVO {

    @ApiModelProperty(value = "统计类型, sale 销售统计， return退货统计", required = true)
    private String statistics_type;

    @ApiModelProperty(value = "商品统计条目集合", required = true)
    private List<ProductStatisticsItem> item_list;
}
