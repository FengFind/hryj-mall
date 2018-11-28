package com.hryj.entity.bo.order;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 罗秋涵
 * @className: OrderType
 * @description: 订单类型
 * @create 2018/9/10 0010 9:36
 **/
@Data
@TableName("o_order_type")
public class OrderType  extends Model<OrderType> {

    private static final long serialVersionUID = 1L;

    /**
     * 订单类型编码
     */
    private String order_type_id;
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
