package com.hryj.entity.vo.promotion.recommend.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: PartyRecommendProdAppendRequestVO
 * @description: 门店或仓库推荐商品请求VO
 * @create 2018/6/27 0027 21:14
 **/
@ApiModel(value = "门店或仓库推荐商品请求VO", description = "门店或仓库推荐商品请求VO")
@Data
public class PartyRecommendProdAppendRequestVO extends RequestVO {

    @ApiModelProperty(value = "门店或仓库ID", required = true)
    private Long party_id;

    @ApiModelProperty(value = "推荐的商品ID集合", required = true)
    private List<PartyRecommendProdItem> recommend_prod_list;

    @ApiModelProperty(value = "商品重复添加时，商品已下架时，商品已全网禁售时的处理策略， 1返回错误，其他任意值直接忽略, 缺省1")
    private Integer when_prod_duplicate = 1;

}
