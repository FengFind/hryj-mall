package com.hryj.entity.bo.product;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.hryj.entity.vo.product.common.response.ProductBrand;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 汪豪
 * @className: Brand
 * @description: 商品品牌
 * @create 2018/9/10 0010 16:23
 **/
@Data
@TableName("p_brand")
public class Brand extends Model<Brand> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;

    /**
     * 品牌中文名称
     */
    private String brand_name;

    /**
     * 品牌名称辅助,可以是品牌的英文名称或字母简写等
     */
    private String brand_name_assist;

    /**
     * 品牌LOGO图片地址
     */
    private String logo_image;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    public ProductBrand convertToProdBrand() {
        ProductBrand prod_brand = new ProductBrand();
        prod_brand.setId(this.id);
        prod_brand.setBrand_name(this.brand_name);
        prod_brand.setLogo_image(this.logo_image);
        return prod_brand;
    }
}
