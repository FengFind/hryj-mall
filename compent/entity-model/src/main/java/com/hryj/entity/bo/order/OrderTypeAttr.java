package com.hryj.entity.bo.order;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 罗秋涵
 * @className: OrderTypeAttr
 * @description: 订单类型属性
 * @create 2018/9/12 0012 14:09
 **/

@Data
@TableName("o_order_type_attr")
public class OrderTypeAttr extends Model<OrderTypeAttr> {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;

    /**
     * 订单类型ID
     */
    private String order_type_id;

    /**
     * 属性名
     */
    private String attr_name;

    /**
     * 属性值
     */
    private String attr_value;

    /**
     * 描述
     */
    private String description;


    @Override
    protected Serializable pkVal() {
        return null;
    }
}
