package com.hryj.entity.vo.product.response.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author 王光银
 * @className: AppProdListItemResponseVO
 * @description:
 * @create 2018/6/28 0028 20:10
 **/
@ApiModel(value = "APP端商品条目响应VO", description = "APP端商品条目响应VO")
@Data
public class AppProdListItemResponseVO {

    @ApiModelProperty(value = "商品ID")
    private Long product_id;

    @ApiModelProperty(value = "门店或仓库ID")
    private Long party_id;

    @ApiModelProperty(value = "活动ID")
    private Long activity_id;

    @ApiModelProperty(value = "商品名称")
    private String product_name;

    @ApiModelProperty(value = "商品图片URL")
    private String list_image_url;

    @ApiModelProperty(value = "规格")
    private String specification;

    @ApiModelProperty(value = "库存数量")
    private String inventory_quantity;

    @ApiModelProperty(value = "商品售价")
    private String sale_price;

    @ApiModelProperty(value = "促销活动价")
    private String activity_price;

    @ApiModelProperty(value = "活动商品角标图")
    private String activity_mark_image;

    @ApiModelProperty(value = "排序号，APP端请无视该字段")
    private String sort;

    @ApiModelProperty(value = "活动结束时间，服务端使用，APP商品请忽略该字段")
    private Date end_date;

    @ApiModelProperty(value = "商品类型")
    private String product_type_id;

    @ApiModelProperty(value = "商品title标记图片链接")
    private List<String> title_mark_image_list;

    @Override
    public int hashCode() {
        Integer prod_hash = null;
        Integer act_hash = null;
        Integer party_hash = null;

        if (this.product_id != null && this.product_id > 0L) {
            prod_hash = this.product_id.hashCode();
        }
        if (this.activity_id != null && this.activity_id > 0L) {
            act_hash = this.activity_id.hashCode();
        }
        if (this.party_id != null && this.party_id > 0L) {
            party_hash = this.party_id.hashCode();
        }

        /**
         * 有无活动是首要识别条件
         */
        if (act_hash == null) {
            return prod_hash == null ? -1 : prod_hash;
        } else if (party_hash != null && prod_hash != null) {
            return prod_hash * party_hash * act_hash;
        } else {
            return -1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof  AppProdListItemResponseVO)) {
            return false;
        }
        AppProdListItemResponseVO item = (AppProdListItemResponseVO) obj;

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

        boolean party_eq;
        if (this.party_id == null && item.party_id == null) {
            party_eq = true;
        } else if (this.party_id != null && item.party_id != null && this.party_id.equals(item.party_id)) {
            party_eq = true;
        } else {
            party_eq = false;
        }

        boolean thisHasAct = this.activity_id != null && this.activity_id > 0L;
        boolean objHasAct = item.activity_id != null && item.activity_id > 0L;
        if (thisHasAct || objHasAct) {
            return prod_eq && act_eq && party_eq;
        }
        return prod_eq;
    }
}