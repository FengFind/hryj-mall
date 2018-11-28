package com.hryj.entity.vo.product.partyprod.request;

import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.promotion.recommend.request.PartyRecommendProdItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: PartyIdRequestVO
 * @description: 门店仓库商品请求VO
 * @create 2018/7/5 0005 10:36
 **/
@ApiModel(value = "门店仓库商品请求VO", description = "门店仓库商品请求VO")
@Data
public class PartyIdProductRequestVO extends RequestVO {

    @ApiModelProperty(value = "分门店或仓库ID", required = true)
    private Long party_id;

    @ApiModelProperty(value = "推荐位商品", required = true)
    private PartyRecommendProdItem one_party_recommend_item;

    @ApiModelProperty(value = "商品重复添加时的处理策略， 1返回错误，其他任意值直接忽略, 缺省1")
    private Integer when_prod_duplicate = 1;
}
