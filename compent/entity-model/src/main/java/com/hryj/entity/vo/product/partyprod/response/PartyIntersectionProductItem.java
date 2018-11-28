package com.hryj.entity.vo.product.partyprod.response;

import com.hryj.entity.vo.product.common.response.ProductBrand;
import com.hryj.entity.vo.product.common.response.ProductMadeWhere;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: PartyIntersectionProductItem
 * @description:
 * @create 2018/9/13 0013 14:13
 **/
@Data
@ApiModel(value = "门店交集商品响应条目", description = "门店交集商品响应条目")
public class PartyIntersectionProductItem {

    @ApiModelProperty(value = "商品类型ID, new_retail新零售商品， bonded跨境商品")
    private String product_type_id;

    @ApiModelProperty(value = "商品类型名称")
    private String product_type_name;

    @ApiModelProperty(value = "商品ID")
    private Long product_id;

    @ApiModelProperty(value = "商品名称")
    private String product_name;

    @ApiModelProperty(value = "分类名称")
    private String category_name;

    @ApiModelProperty(value = "商品图片URL")
    private String list_image_url;

    @ApiModelProperty(value = "规格")
    private String specification;

    @ApiModelProperty(value = "品牌")
    private ProductBrand brand;

    @ApiModelProperty(value = "产地")
    private ProductMadeWhere made_where;

    @ApiModelProperty(value = "成本价格，已经处理为最多两位小数的浮点数字符串")
    private String cost_price;
}
