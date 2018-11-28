package com.hryj.entity.vo.promotion.activity.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: PromotionActivityAppendProdRequestVO
 * @description:
 * @create 2018/6/28 0028 11:53
 **/
@ApiModel(value = "追加促销活动商品请求VO", description = "追加促销活动商品请求VO")
@Data
public class PromotionActivityAppendProdRequestVO extends RequestVO {

    @ApiModelProperty(value = "活动ID", required = true)
    private Long activity_id;

    @ApiModelProperty(value = "追加的商品集合", required = true)
    private List<PromotionActivityProductItemRequestVO> product_list;
}
