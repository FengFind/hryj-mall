package com.hryj.entity.vo.product.category.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: ProdCateIdRequestVO
 * @description: 属性分类ID请求VO
 * @create 2018/7/5 0005 11:39
 **/
@ApiModel(value = "属性分类ID请求VO", description = "属性分类ID请求VO")
@Data
public class ProdCateIdRequestVO extends RequestVO {

    @ApiModelProperty(value = "产品分类ID", required = true)
    private Long product_category_id;
}
