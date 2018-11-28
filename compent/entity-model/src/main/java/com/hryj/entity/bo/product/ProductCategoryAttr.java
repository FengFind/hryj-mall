package com.hryj.entity.bo.product;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品分类属性表
 *
 * @author daitingbo
 * @since 2018-06-28
 */
@Data
@TableName("p_product_category_attr")
public class ProductCategoryAttr extends Model<ProductCategoryAttr> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 商品分类id
     */
    private Long prod_cate_id;
    /**
     * 属性名
     */
    private String attr_name;
    /**
     * 属性类型:01-枚举,02-字符串
     */
    private String attr_type;

    @Override
    protected Serializable pkVal() {
          return this.id;
    }

}
