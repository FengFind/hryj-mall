package com.hryj.entity.bo.promotion;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 活动商品条目
 *
 * @author daitingbo
 * @since 2018-06-28
 */
@Data
@TableName("pt_activity_product_item")
public class ActivityProductItem extends Model<ActivityProductItem> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 活动id
     */
    private Long activity_id;
    /**
     * 商品id
     */
    private Long product_id;
    /**
     * 活动价格
     */
    private BigDecimal activity_price;

    /**
     * 排序
     */
    private Long sort_num;

    @Override
    protected Serializable pkVal() {
          return this.id;
    }

}
