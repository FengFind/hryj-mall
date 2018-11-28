package com.hryj.entity.bo.product;


import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hryj.entity.vo.product.common.response.ProductMadeWhere;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 汪豪
 * @className: ProductGeo
 * @description: 国家、区域
 * @create 2018/9/10 0010 16:46
 **/
@Data
@TableName("p_product_geo")
public class ProductGeo extends Model<ProductGeo> {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 类型， 1 国家，2地区或城市
     */
    private int type;

    /**
     * 国家或地区的代码
     */
    private String code;

    /**
     * 国家或区域名称
     */
    private String name;

    /**
     * 国家或区域LOGO图片
     */
    private String logo_image;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    public ProductMadeWhere convertToMadeWhere() {
        ProductMadeWhere pmw = new ProductMadeWhere();
        pmw.setId(this.id);
        pmw.setMade_where(this.name);
        pmw.setLogo_image(this.logo_image);
        return pmw;
    }
}
