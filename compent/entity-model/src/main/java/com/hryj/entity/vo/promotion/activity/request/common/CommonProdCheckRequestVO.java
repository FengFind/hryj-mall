package com.hryj.entity.vo.promotion.activity.request.common;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: ActivityProductItemIdRequestVO
 * @description: 从商品的角度检查促销活动请求VO
 * @create 2018/7/5 0005 21:13
 **/
@ApiModel(value = "从商品的角度检查促销活动请求VO", description = "从商品的角度检查促销活动请求VO")
@Data
public class CommonProdCheckRequestVO extends RequestVO {

    @ApiModelProperty(value = "商品id集合", required = true)
    private List<Long> product_id_list;
}
