package com.hryj.entity.vo.product.category.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: ProdCateAttrHandleResquestVO
 * @description:
 * @create 2018/7/5 0005 11:04
 **/
@ApiModel(value = "创建商品分类属性请求VO", description = "创建商品分类属性请求VO")
@Data
public class ProdCateAttrHandleResquestVO extends RequestVO {

    @ApiModelProperty(value = "产品分类ID", required = true)
    private Long product_category_id;

    @ApiModelProperty(value = "分类的一个属性，请求处理单个分类属性时该参数有效")
    private ProdCateAttrRequestVO one_attr;

    @ApiModelProperty(value = "分类的一组属性，请求批量处理分类属性时该参数有效")
    private List<ProdCateAttrRequestVO> many_attr;
}
