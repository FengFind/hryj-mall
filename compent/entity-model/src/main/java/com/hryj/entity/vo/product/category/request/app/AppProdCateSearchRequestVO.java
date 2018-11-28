package com.hryj.entity.vo.product.category.request.app;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: AppProdCateSearchRequestVO
 * @description:
 * @create 2018/7/5 0005 17:55
 **/
@ApiModel(value = "APP端查询商品分类请求VO", description = "APP端查询商品分类请求VO")
@Data
public class AppProdCateSearchRequestVO extends RequestVO {

    @ApiModelProperty(value = "门店ID，缺省使用用户的默认门店ID")
    private Long party_id;

    @ApiModelProperty(value = "商品分类ID，缺省查询所有顶级分类，否则查询指定分类的直接下级分类")
    private Long category_id;

    @ApiModelProperty(value = "商品类型ID， all全部, new_retail新零售商品， bonded跨境商品，缺省默认: all")
    private String product_type_id;

    @ApiModelProperty(value = "用户ID， 门店端需要传此参数， 用户端请忽略此参数")
    private Long user_id;
}
