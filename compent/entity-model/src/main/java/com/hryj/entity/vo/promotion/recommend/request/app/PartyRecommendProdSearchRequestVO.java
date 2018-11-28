package com.hryj.entity.vo.promotion.recommend.request.app;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: PartyRecommendProdSearchRequestVO
 * @description:
 * @create 2018/8/14 0014 11:48
 **/
@ApiModel(value = "APP用户端查询推荐商品请求VO", description = "APP用户端查询推荐商品请求VO")
@Data
public class PartyRecommendProdSearchRequestVO extends RequestVO {

    @ApiModelProperty(value = "门店ID，缺省使用用户的默认门店ID")
    private Long party_id;

    @ApiModelProperty(value = "商品类型ID， all全部, new_retail新零售商品， bonded跨境商品，缺省默认: all")
    private String product_type_id;
}
