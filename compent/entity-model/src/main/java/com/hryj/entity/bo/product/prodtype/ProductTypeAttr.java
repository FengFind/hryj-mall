package com.hryj.entity.bo.product.prodtype;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 王光银
 * @className: ProductTypeAttr
 * @description: 商品类型属性
 * @create 2018/9/10 0010 16:23
 **/
@Data
@TableName("p_product_type_attr")
public class ProductTypeAttr extends Model<ProductTypeAttr> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;

    private String product_type_id;

    private String attr_name;

    private String attr_value;

    private String description;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
