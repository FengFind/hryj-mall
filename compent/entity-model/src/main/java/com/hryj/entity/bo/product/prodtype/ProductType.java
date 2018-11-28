package com.hryj.entity.bo.product.prodtype;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 王光银
 * @className: ProductType
 * @description: 商品类型
 * @create 2018/9/10 0010 16:23
 **/
@Data
@TableName("p_product_type")
public class ProductType extends Model<ProductType> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "product_type_id", type = IdType.INPUT)
    private String product_type_id;

    private String parent_type_id;

    private String has_table;

    private String description;

    @Override
    protected Serializable pkVal() {
        return this.product_type_id;
    }
}
