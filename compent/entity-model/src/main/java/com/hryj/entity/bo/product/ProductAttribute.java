package com.hryj.entity.bo.product;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品属性表
 *
 * @author daitingbo
 * @since 2018-06-28
 */
@Data
@TableName("p_product_attribute")
public class ProductAttribute extends Model<ProductAttribute> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 商品id
     */
    private Long product_id;
    /**
     * 属性类别:01-分类属性,02-自定义属性
     */
    private String attr_type;
    /**
     * 属性名称
     */
    private String attr_name;
    /**
     * 属性值
     */
    private String attr_value;
    /**
     * 商品分类属性id
     */
    private Long prod_cate_attr_id;
    /**
     * 商品分类属性条目id
     */
    private Long prod_cate_attr_item_id;

    @Override
    protected Serializable pkVal() {
          return this.id;
    }

}
