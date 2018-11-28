package com.hryj.entity.bo.order;

/**
 * @author 罗秋涵
 * @className: OrderAdjustment
 * @description: 订单调整
 * @create 2018/9/10 0010 9:45
 **/

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("o_order_adjustment")
public class OrderAdjustment extends Model<OrderAdjustment> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;

    /**
     * 创建时间
     */
    private Date create_time;
    /**
     * 更新时间
     */
    private Date update_time;

    /**
     * 调整类型ID
     */
    private String adjustment_type_id;
    /**
     * 订单ID
     */
    private Long order_id;
    /**
     * 订单条目ID
     */
    private Long order_item_id;
    /**
     * 调整金额
     */
    private BigDecimal amount;
    /**
     * 备注描述
     */
    private String description;
    /**
     * 源参考标识ID(商品基础税率表ID)
     */
    private Long source_refrence_id;
    /**
     * 创建人
     */
    private Long user_id;
    /**
     * 创建人姓名
     */
    private String user_name;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
