package com.hryj.entity.vo.product.common.response;

import com.hryj.entity.vo.product.crossborder.response.CrossBorderProductValidateResponseItem;
import com.hryj.entity.vo.promotion.activity.response.ActivityInProgressProductItemResponseVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author 王光银
 * @className: ProductValidateResponseItem
 * @description:
 * @create 2018/7/19 0019 21:57
 **/
@Data
public class ProductValidateResponseItem {

    public ProductValidateResponseItem() {}

    public ProductValidateResponseItem(Long party_id, Long product_id, Long activity_id) {
        this.party_id = party_id;
        this.product_id = product_id;
        this.activity_id = activity_id;
    }

    @ApiModelProperty(value = "商品类型ID", required = true)
    private String product_type_id;

    @ApiModelProperty(value = "商品类型名称", required = true)
    private String product_type_name;

    @ApiModelProperty(value = "商品类型的前置显示标记内容")
    private List<String> title_mark_list;

    @ApiModelProperty(value = "门店或仓库ID", required = true)
    private Long party_id;

    @ApiModelProperty(value = "商品ID", required = true)
    private Long product_id;

    @ApiModelProperty(value = "活动ID")
    private Long activity_id;

    @ApiModelProperty(value = "商品名称")
    private String product_name;

    @ApiModelProperty(value = "成本价")
    private BigDecimal cost_price;

    @ApiModelProperty(value = "商品分类id")
    private Long prod_cate_id;

    @ApiModelProperty(value = "商品分类名称")
    private String prod_cate_path;

    @ApiModelProperty(value = "商品图片URL")
    private String list_image_url;

    @ApiModelProperty(value = "规格")
    private String specification;

    @ApiModelProperty(value = "品牌名称")
    private String brand_name;

    @ApiModelProperty(value = "品牌对象")
    private ProductBrand brand;

    @ApiModelProperty(value = "通过验证所需要的最小库存量")
    private Integer required_min_inventory_quantity;

    @ApiModelProperty(value = "该字段取决于对应的入参，原样返回")
    private String follow_value;

    @ApiModelProperty(value = "商品是否有效, true有效，false无效")
    private Boolean is_valid;

    @ApiModelProperty(value = "商品验证结果状态值: 11000商品下架，11010活动已结束，11020商品库存不足，11030商品不存在，16100组织不存在 ")
    private Integer validate_status_code;

    @ApiModelProperty(value = "商品库存数量")
    private Integer inventory_quantity;

    @ApiModelProperty(value = "商品当前时刻销售价格，如果没有促销活动则为正常售价，如果有活动并且活动未过期为活动价格，活动过期为正常售价")
    private BigDecimal this_moment_sale_price;

    @ApiModelProperty(value = "商品在当事组织中的正常销售价")
    private BigDecimal normal_price;

    @ApiModelProperty(value = "商品无效时的一些说明, 有效时该字段无值")
    private String other_comments;

    @ApiModelProperty(value = "发货仓库编码")
    private String channel;

    @ApiModelProperty(value = "发货仓库名称")
    private String channel_name;

    @ApiModelProperty(value = "商品促销信息")
    private ActivityInProgressProductItemResponseVO promotion_info;

    @ApiModelProperty(value = "跨境商品税费计算相关数据响应")
    private CrossBorderProductValidateResponseItem crossBorderProductValidateResponseItem;

    @Override
    public int hashCode()  {
        Integer party_id_hash = null;
        Integer prod_id_hash = null;
        Integer act_id_hash = null;
        if (this.party_id != null) {
            party_id_hash = this.party_id.hashCode();
        }
        if (this.product_id != null) {
            prod_id_hash = this.product_id.hashCode();
        }
        if (this.activity_id != null) {
            act_id_hash = this.activity_id.hashCode();
        }

        if (party_id_hash == null && prod_id_hash == null && act_id_hash == null) {
            return -1;
        } else if (party_id_hash != null && prod_id_hash != null && act_id_hash != null) {
            return party_id_hash * prod_id_hash * act_id_hash;
        } else if (party_id_hash != null && prod_id_hash != null) {
            return party_id_hash * prod_id_hash;
        } else if (party_id_hash != null && act_id_hash != null) {
            return party_id_hash * act_id_hash;
        } else if (prod_id_hash != null && act_id_hash != null) {
            return prod_id_hash * act_id_hash;
        } else if (party_id_hash != null) {
            return party_id_hash;
        } else if (prod_id_hash != null) {
            return prod_id_hash;
        } else {
            return act_id_hash;
        }
    }

    @Override
    public boolean equals(Object obj)  {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ProductValidateResponseItem)) {
            return false;
        }
        ProductValidateResponseItem item = (ProductValidateResponseItem) obj;
        boolean party_eq;
        if (this.party_id == null && item.party_id == null) {
            party_eq = true;
        } else if (this.party_id != null && item.party_id != null && this.party_id.equals(item.party_id)) {
            party_eq = true;
        } else {
            party_eq = false;
        }

        boolean prod_eq;
        if (this.product_id == null && item.product_id == null) {
            prod_eq = true;
        } else if (this.product_id != null && item.product_id != null && this.product_id.equals(item.product_id)) {
            prod_eq = true;
        } else {
            prod_eq = false;
        }

        boolean act_eq;
        if (this.activity_id == null && item.activity_id == null) {
            act_eq = true;
        } else if (this.activity_id != null && item.activity_id != null && this.activity_id.equals(item.activity_id)) {
            act_eq = true;
        } else {
            act_eq = false;
        }

        return party_eq && prod_eq && act_eq;
    }

}
