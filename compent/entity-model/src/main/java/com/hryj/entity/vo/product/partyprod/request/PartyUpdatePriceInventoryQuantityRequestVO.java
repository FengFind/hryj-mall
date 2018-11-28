package com.hryj.entity.vo.product.partyprod.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: PartyUpdatePriceInventoryQuantityRequestVO
 * @description:
 * @create 2018/6/27 0027 17:52
 **/
@ApiModel(value = "门店或仓库销售商品数据维护请求VO", description = "门店或仓库销售商品数据维护请求VO")
@Data
public class PartyUpdatePriceInventoryQuantityRequestVO extends RequestVO {

    @ApiModelProperty(value = "一组门店或仓库的商品数据，维护多个销售商品时使用该参数")
    private List<PartyUpdatePriceInventoryQuantityItemRequestVO> many_item;
}
