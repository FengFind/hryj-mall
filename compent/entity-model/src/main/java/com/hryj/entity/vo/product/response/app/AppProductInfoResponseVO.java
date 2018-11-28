package com.hryj.entity.vo.product.response.app;

import com.hryj.entity.vo.product.common.response.CrossBorderSimpleInfoResponseVO;
import com.hryj.entity.vo.product.common.response.ProductBrand;
import com.hryj.entity.vo.product.common.response.ProductMadeWhere;
import com.hryj.entity.vo.product.response.ProdPromResponseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: AppProductInfoResponseVO
 * @description:
 * @create 2018/6/28 0028 20:38
 **/
@ApiModel(value = "APP端商品详情响应VO", description = "APP端商品详情响应VO")
@Data
public class AppProductInfoResponseVO {

    @ApiModelProperty(value = "当事组织(门店或仓库)ID")
    private Long party_id;

    @ApiModelProperty(value = "商品ID")
    private Long product_id;

    @ApiModelProperty(value = "商品名称")
    private String product_name;

    @ApiModelProperty(value = "商品分类名称")
    private String category_name;

    @ApiModelProperty(value = "商品分类ID")
    private Long category_id;

    @ApiModelProperty(value = "品牌对象")
    private ProductBrand brand;

    @ApiModelProperty(value = "产地对象")
    private ProductMadeWhere made_where;

    @ApiModelProperty(value = "保质期")
    private String shelf_life;

    @ApiModelProperty(value = "规格")
    private String specification;

    @ApiModelProperty(value = "商品特色描述")
    private String comments;

    @ApiModelProperty(value = "商品详细描述")
    private String long_description;

    @ApiModelProperty(value = "商品列表图URL")
    private String list_image_url;

    @ApiModelProperty(value = "库存数量")
    private String inventory_quantity;

    @ApiModelProperty(value = "商品售价")
    private String sale_price;

    @ApiModelProperty(value = "配送信息描述")
    private String distribution_info;

    @ApiModelProperty(value = "配送信息的图标URL")
    private String distribution_mark_image;

    @ApiModelProperty(value = "商品的当前促销信息，请求参数中包含有活动ID，并且活动正常时该对象属性有值")
    private ProdPromResponseVO current_prom_ifn;

    @ApiModelProperty(value = "商品所有的促销活动信息集合，包含字段 current_prom_ifn 的信息")
    private List<ProdPromResponseVO> prod_prom_info_list;

    @ApiModelProperty(value = "商品详情图集合")
    private List<String> detail_image_list;

    @ApiModelProperty(value = "商品属性集合")
    private List<AppProdAttrItemResponseVO> attr_list;

    @ApiModelProperty(value = "跨境商品信息")
    private CrossBorderSimpleInfoResponseVO crossBorderSimpleInfoResponseVO;

    @ApiModelProperty(value = "商品title标记图片链接")
    private List<String> title_mark_image_list;

    @ApiModelProperty(value = "商品类型ID，bonded跨境商品， new_retail新零售")
    private String product_type_id;

    @ApiModelProperty(value = "商品类型名称")
    private String product_type_name;
}
