package com.hryj.entity.vo.product.partyprod.mapping;

import com.hryj.entity.bo.product.Brand;
import com.hryj.entity.bo.product.ProductGeo;
import com.hryj.entity.vo.delegator.GenericConverter;
import com.hryj.entity.vo.product.partyprod.response.PartyProductListItemResponseVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 王光银
 * @className: PartyProductMappingRow
 * @description: 门店商品查询包装类
 * @create 2018/6/27 0027 17:02
 **/
@Data
public class PartyProductMappingRow {

    @ApiModelProperty(value = "当事组织商品ID")
    private Long party_product_id;

    @ApiModelProperty(value = "库存数量")
    private Integer inventory_quantity;

    @ApiModelProperty(value = "库存数量")
    private Integer center_inventory_quantity;

    @ApiModelProperty(value = "上下架状态, 1上架， 0下架")
    private Integer up_down_status;

    @ApiModelProperty(value = "销售价格，已经处理为最多两位小数的浮点数字符串，多门店或仓库聚合查询时该字段没有值")
    private BigDecimal sale_price;

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
    private Long brand;

    @ApiModelProperty(value = "规格")
    private String specification;

    @ApiModelProperty(value = "保持期")
    private String shelf_life;

    @ApiModelProperty(value = "产地")
    private Long made_where;

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
    private BigDecimal declare_price;

    public PartyProductListItemResponseVO convertToPartyProductListItem(GenericConverter<Boolean> cross_border_prod_check,
                                                                        GenericConverter<Brand> brand_converter,
                                                                        GenericConverter<ProductGeo> geo_converter,
                                                                        GenericConverter<String> prod_type_name_converter) {
        PartyProductListItemResponseVO item = new PartyProductListItemResponseVO();
        item.setParty_product_id(this.party_product_id);
        item.setProduct_name(this.product_name);
        item.setOperator_name(this.operator_name);
        item.setList_image_url(this.list_image_url);
        item.setCost_price(this.cost_price);
        item.setProduct_info(this.product_info);
        item.setShelf_life(this.shelf_life);
        item.setSpecification(this.specification);
        item.setCreate_time(this.create_time);
        item.setCategory_name(this.category_name);
        item.setSales_end_date(this.sales_end_date);
        item.setIntroduction_date(this.introduction_date);
        item.setAudit_status(this.audit_status);
        item.setUp_down_status(this.up_down_status);
        item.setForbid_sale_flag(this.forbid_sale_flag);
        item.setProd_cate_id(this.prod_cate_id);
        item.setProduct_id(this.product_id);
        item.setDeclare_price(this.declare_price == null ? "0.00" : this.declare_price.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        item.setProduct_type_id(this.product_type_id);
        item.setProduct_type_name(prod_type_name_converter.convert(this.product_type_id));

        Brand brand = brand_converter.convert(this.brand);
        if (brand != null) {
            item.setBrand(brand.convertToProdBrand());
        }

        ProductGeo geo = geo_converter.convert(this.made_where);
        if (geo != null) {
            item.setMade_where(geo.convertToMadeWhere());
        }

        item.setSale_price(this.sale_price == null ? "0.00" : this.sale_price.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        if (cross_border_prod_check.convert(this.product_type_id)) {
            item.setInventory_quantity(this.center_inventory_quantity);
        } else {
            item.setInventory_quantity(this.inventory_quantity);
        }
        return item;
    }
}
