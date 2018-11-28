package com.hryj.entity.bo.product;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品浏览记录表
 *
 * @author daitingbo
 * @since 2018-06-28
 */
@Data
@TableName("p_product_browse_record")
public class ProductBrowseRecord extends Model<ProductBrowseRecord> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 商品分类id
     */
    private Long product_category_id;
    /**
     * 商品id
     */
    private Long product_id;
    /**
     * 门店仓库id
     */
    private Long party_id;
    /**
     * 浏览入口源:链接到商品页面的源地址
     */
    private String referrer_url;
    /**
     * 浏览用户id
     */
    private Long browse_user_id;
    /**
     * 进入商品页面时间
     */
    private Date in_pv_page_at;
    /**
     * 离开商品页面时间
     */
    private Date out_pv_page_at;
    /**
     * 以秒为单位
     */
    private Long stay_in_second;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
