package com.hryj.entity.bo.product;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 门店仓库商品
 *
 * @author daitingbo
 * @since 2018-06-28
 */
@Data
@TableName("p_party_product")
public class PartyProduct extends Model<PartyProduct> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 仓库或门店id
     */
    private Long party_id;
    /**
     * 商品id
     */
    private Long product_id;
    /**
     * 推介销售时间
     */
    private Date introduction_date;
    /**
     * 销售结束时间
     */
    private Date sales_end_date;
    /**
     * 上下架状态:1-上架,0-下架
     */
    private Integer up_down_status;
    /**
     * 销售价格
     */
    private BigDecimal sale_price;
    /**
     * 库存数量
     */
    private Integer inventory_quantity;
    /**
     * 操作人id
     */
    private Long operator_id;
    /**
     * 创建时间
     */
    private Date create_time;
    /**
     * 更新时间
     */
    private Date update_time;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
