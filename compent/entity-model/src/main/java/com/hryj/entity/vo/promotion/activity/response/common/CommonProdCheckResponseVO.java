package com.hryj.entity.vo.promotion.activity.response.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author 王光银
 * @className: CommonProdCheckResponseVO
 * @description: 从商品的角度检查促销活动响应VO
 * @create 2018/7/10 0010 18:01
 **/
@ApiModel(value = "从商品的角度检查促销活动响应VO", description = "从商品的角度检查促销活动响应VO")
@Data
public class CommonProdCheckResponseVO {

    @ApiModelProperty(value = "商品的促销活动参与检查结果集合，key为商品ID")
    private Map<Long, List<ProdCheckItem>> check_result;
}
