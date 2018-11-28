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
 * 商品统计信息表
 *
 * @author daitingbo
 * @since 2018-06-28
 */
@Data
@TableName("p_product_summary")
public class ProductSummary extends Model<ProductSummary> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    private Long product_id;
    /**
     * 下单支付后纳入
     */
    private BigDecimal total_quantity_ordered;

    /**
     * 支付后取消订单的
     */
    private BigDecimal total_quantity_canceled;

    /**
     * 完成订单的
     */
    private BigDecimal total_quantity_finished;

    /**
     * 用户进入商品PV页面与离开的时间，单位秒
     */
    private Long total_times_viewed;
    /**
     * 退货完成后纳入该值计算
     */
    private BigDecimal total_quantity_retured;
    /**
     * 用户进入PV页面计算
     */
    private Long total_quantity_viewed;
    /**
     * 最后更新时间
     */
    private Date last_updated_stamp;
    /**
     * 门店仓库id
     */
    private Long party_id;

    @Override
    protected Serializable pkVal() {
          return this.id;
    }

}
