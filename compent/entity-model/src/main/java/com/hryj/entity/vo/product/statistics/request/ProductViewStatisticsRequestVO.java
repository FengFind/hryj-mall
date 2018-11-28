package com.hryj.entity.vo.product.statistics.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: ProductViewStatisticsRequestVO
 * @description:
 * @create 2018/7/4 0004 11:45
 **/
@ApiModel(value = "商品浏览统计请求VO", description = "商品浏览统计请求VO")
@Data
public class ProductViewStatisticsRequestVO extends RequestVO {

    @ApiModelProperty(value = "商品ID", required = true)
    private Long product_id;

    @ApiModelProperty(value = "门店或仓库ID", required = true)
    private Long party_id;

    @ApiModelProperty(value = "进行PV页面时间", required = true)
    private String in_pv_page_at;

    @ApiModelProperty(value = "离开PV页面时间", required = true)
    private String out_pv_page_at;
}
