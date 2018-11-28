package com.hryj.entity.bo.permission;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 王光银
 * @className: OrderTypeRole
 * @description:
 * @create 2018/9/10 0010 9:27
 **/
@Data
@TableName("o_order_type_role")
public class OrderTypeRole extends Model<OrderTypeRole> {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;

    private Long staff_id;

    private Long role_id;

    private String order_type_id;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
