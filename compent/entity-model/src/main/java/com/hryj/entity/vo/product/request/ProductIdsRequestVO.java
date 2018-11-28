package com.hryj.entity.vo.product.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: ProductIdRequestVO
 * @description: 商品ID集合请求VO
 * @create 2018/7/5 0005 9:15
 **/
@ApiModel(value = "商品ID集合请求VO", description = "商品ID集合请求VO")
@Data
public class ProductIdsRequestVO extends RequestVO {

    @ApiModelProperty(value = "商品ID集合", required = true)
    private List<Long> product_id_list;
}
