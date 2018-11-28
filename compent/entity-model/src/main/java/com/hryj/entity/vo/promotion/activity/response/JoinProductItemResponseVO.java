package com.hryj.entity.vo.promotion.activity.response;

import com.hryj.entity.bo.product.Brand;
import com.hryj.entity.vo.product.common.response.ProductBrand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: JoinProductItemResponseVO
 * @description:
 * @create 2018/6/28 0028 11:49
 **/
@ApiModel(value = "活动商品详情条目响应VO", description = "活动商品详情条目响应VO")
@Data
public class JoinProductItemResponseVO {

    @ApiModelProperty(value = "活动参与商品条目ID，该参数可用于请求删除一个活动的参与商品")
    private Long activity_product_item_id;

    @ApiModelProperty(value = "商品ID")
    private Long product_id;

    @ApiModelProperty(value = "商品在活动中的排序，排序值按从小到大降序排，越大的排在前面")
    private Integer sort_num;

    @ApiModelProperty(value = "活动价格")
    private String activity_price;

    @ApiModelProperty(value = "商品名称")
    private String product_name;

    @ApiModelProperty(value = "商品分类名称")
    private String category_name;

    @ApiModelProperty(value = "品牌id")
    private Long brand_id;

    @ApiModelProperty(value = "品牌信息")
    private ProductBrand brand;

    @ApiModelProperty(value = "规格")
    private String specification;

    @ApiModelProperty(value = "成本价")
    private String cost_price;

    @ApiModelProperty(value = "商品列表图片URL")
    private String list_image_url;

}
