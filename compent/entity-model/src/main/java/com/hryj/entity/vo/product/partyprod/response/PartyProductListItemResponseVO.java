package com.hryj.entity.vo.product.partyprod.response;

import com.hryj.entity.vo.product.common.response.ProductBrand;
import com.hryj.entity.vo.product.common.response.ProductMadeWhere;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: PartyProductListItemResponseVO
 * @description: 当事组织商品列表条目响应VO
 * @create 2018/6/27 0027 17:02
 **/
@ApiModel(value = "当事组织商品列表条目响应VO", description = "当事组织商品列表条目响应VO")
@Data
public class PartyProductListItemResponseVO {

    @ApiModelProperty(value = "当事组织商品ID")
    private Long party_product_id;

    @ApiModelProperty(value = "库存数量")
    private Integer inventory_quantity;

    @ApiModelProperty(value = "上下架状态, 1上架， 0下架")
    private Integer up_down_status;

    @ApiModelProperty(value = "销售价格，已经处理为最多两位小数的浮点数字符串，多门店或仓库聚合查询时该字段没有值")
    private String sale_price;

    @ApiModelProperty(value = "商品ID")
    private Long product_id;

    @ApiModelProperty(value = "商品名称")
    private String product_name;

    @ApiModelProperty(value = "商品分类ID")
    private Long prod_cate_id;

    @ApiModelProperty(value = "分类名称")
    private String category_name;

    @ApiModelProperty(value = "商品图片URL")
    private String list_image_url;

    @ApiModelProperty(value = "品牌")
    private ProductBrand brand;

    @ApiModelProperty(value = "规格")
    private String specification;

    @ApiModelProperty(value = "保持期")
    private String shelf_life;

    @ApiModelProperty(value = "产地")
    private ProductMadeWhere made_where;

    @ApiModelProperty(value = "商品特色描述")
    private String product_info;

    @ApiModelProperty(value = "销售开始时间，格式:yyyy-MM-dd HH:mm:ss")
    private String introduction_date;

    @ApiModelProperty(value = "销售结束时间，格式:yyyy-MM-dd HH:mm:ss")
    private String sales_end_date;

    @ApiModelProperty(value = "审核状态，1已审核，0未审核")
    private Integer audit_status;

    @ApiModelProperty(value = "商品创建人姓名")
    private String operator_name;

    @ApiModelProperty(value = "创建时间")
    private String create_time;

    @ApiModelProperty(value = "全网禁售标识，1全网禁售，0正常")
    private Integer forbid_sale_flag;

    @ApiModelProperty(value = "成本价格，已经处理为最多两位小数的浮点数字符串")
    private String cost_price;

    @ApiModelProperty(value = "商品类型ID, new_retail新零售商品， bonded跨境商品")
    private String product_type_id;

    @ApiModelProperty(value = "商品类型名称")
    private String product_type_name;

    @ApiModelProperty(value = "报关价，product_type_id = bonded时有值")
    private String declare_price;
}
