package com.hryj.entity.bo.order;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 罗秋涵
 * @className: OrderAdjustmentType
 * @description: 订单调整类型
 * @create 2018/9/10 0010 9:41
 **/

@Data
@TableName("o_order_adjustment_type")
public class OrderAdjustmentType extends Model<OrderAdjustmentType> {

    /**
     * 调整类型ID
     */
    private String adjustment_type_id;
    /**
     * 上级类型ID
     */
    private String parent_type_id;
    /**
     * 是否有表
     */
    private String has_table;
    /**
     * 描述说明
     */
    private String description;


    @Override
    protected Serializable pkVal() {
        return null;
    }
}
