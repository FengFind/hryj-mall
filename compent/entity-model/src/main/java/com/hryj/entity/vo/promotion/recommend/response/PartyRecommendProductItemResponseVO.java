package com.hryj.entity.vo.promotion.recommend.response;

import com.hryj.entity.vo.product.common.response.ProductBrand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: PartyRecommendProductItemResponseVO
 * @description:
 * @create 2018/6/27 0027 21:04
 **/
@ApiModel(value = "门店或仓库推荐商品条目数据VO", description = "门店或仓库推荐商品条目数据VO")
@Data
public class PartyRecommendProductItemResponseVO {

    @ApiModelProperty(value = "门店或仓库推荐商品条目ID")
    private Long recommend_product_id;

    @ApiModelProperty(value = "商品ID")
    private Long product_id;

    @ApiModelProperty(value = "商品名称")
    private String product_name;

    @ApiModelProperty(value = "商品图片URL")
    private String list_image_url;

    @ApiModelProperty(value = "品牌名称")
    private ProductBrand brand;

    @ApiModelProperty(value = "分类名称")
    private String category_name;

    @ApiModelProperty(value = "规格")
    private String specification;

    @ApiModelProperty(value = "销售价格")
    private String sale_price;

    @ApiModelProperty(value = "是否置顶，1置顶，0未置顶")
    private Integer top_flag;

    @ApiModelProperty(value = "推荐开始日期, 格式:yyyy-MM-dd HH:mm:ss")
    private String start_date;

    @ApiModelProperty(value = "推荐结束日期, 格式:yyyy-MM-dd HH:mm:ss")
    private String end_date;

}
