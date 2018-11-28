package com.hryj.entity.bo.product;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品价格变动历史表
 *
 * @author daitingbo
 * @since 2018-06-28
 */
@Data
@TableName("p_product_price_history")
public class ProductPriceHistory extends Model<ProductPriceHistory> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 商品id
     */
    private Long product_id;
    /**
     * 变动前商品价格
     */
    private BigDecimal prod_price_before;
    /**
     * 变动后商品价格
     */
    private BigDecimal prod_price_after;
    /**
     *  变动价格类型:cost-成本价,sales-门店仓库销售价
     */
    private String prod_price_type;
    /**
     * 变更人id
     */
    private Long updated_by;
    /**
     * 门店仓库id
     */
    private Long party_id;
    /**
     * 数据类型:01-商品中心库,02-仓库或者门店
     */
    private String data_type;

    @Override
    protected Serializable pkVal() {
          return this.id;
    }

}
