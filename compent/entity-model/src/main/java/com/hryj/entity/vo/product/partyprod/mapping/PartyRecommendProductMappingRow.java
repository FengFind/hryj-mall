package com.hryj.entity.vo.product.partyprod.mapping;

import com.hryj.entity.bo.product.Brand;
import com.hryj.entity.vo.delegator.GenericConverter;
import com.hryj.entity.vo.promotion.recommend.response.PartyRecommendProductItemResponseVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: PartyRecommendProductMappingRow
 * @description: 门店推荐商品查询结果映射 vo
 * @create 2018/9/25 0025 10:43
 **/
@Data
public class PartyRecommendProductMappingRow {

    @ApiModelProperty(value = "门店或仓库推荐商品条目ID")
    private Long recommend_product_id;

    @ApiModelProperty(value = "商品ID")
    private Long product_id;

    @ApiModelProperty(value = "商品名称")
    private String product_name;

    @ApiModelProperty(value = "商品图片URL")
    private String list_image_url;

    @ApiModelProperty(value = "品牌id")
    private Long brand;

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

    public PartyRecommendProductItemResponseVO convertTo(GenericConverter<Brand> brand_getter) {
        PartyRecommendProductItemResponseVO vo = new PartyRecommendProductItemResponseVO();
        vo.setRecommend_product_id(this.recommend_product_id);
        vo.setProduct_id(this.product_id);
        vo.setProduct_name(this.product_name);
        vo.setList_image_url(this.list_image_url);
        vo.setCategory_name(this.category_name);
        vo.setSpecification(this.specification);
        vo.setSale_price(this.sale_price);
        vo.setTop_flag(this.top_flag);
        vo.setStart_date(this.start_date);
        vo.setEnd_date(this.end_date);
        Brand brand = brand_getter.convert(this.brand);
        if (brand != null) {
            vo.setBrand(brand.convertToProdBrand());
        }
        return vo;
    }
}
