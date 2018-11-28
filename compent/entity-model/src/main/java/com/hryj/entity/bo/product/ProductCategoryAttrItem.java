package com.hryj.entity.bo.product;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品分类属性条目表
 *
 * @author daitingbo
 * @since 2018-06-28
 */
@Data
@TableName("p_product_category_attr_item")
public class ProductCategoryAttrItem extends Model<ProductCategoryAttrItem> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 商品分类属性id
     */
    private Long prod_cate_attr_id;
    /**
     * 属性枚举条目
     */
    private String attr_value;

    @Override
    protected Serializable pkVal() {
          return this.id;
    }

}
