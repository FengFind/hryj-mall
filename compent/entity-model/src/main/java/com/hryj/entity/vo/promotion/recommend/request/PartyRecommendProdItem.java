package com.hryj.entity.vo.promotion.recommend.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: PartyRecommendProdItem
 * @description:门店或仓库推荐商品条目VO
 * @create 2018/7/9 0009 14:14
 **/
@ApiModel(value = "门店或仓库推荐商品条目VO", description = "门店或仓库推荐商品条目VO")
@Data
public class PartyRecommendProdItem {

    @ApiModelProperty(value = "商品ID", required = true)
    private Long product_id;

    @ApiModelProperty(value = "是否置顶, 1置顶，0不置顶，缺省0")
    private Integer top_flag = 0;

    @ApiModelProperty(value = "推荐开始日期")
    private String start_date;

    @ApiModelProperty(value = "推荐结束日期")
    private String end_date;
}
