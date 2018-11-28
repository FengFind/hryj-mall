package com.hryj.entity.bo.payment;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author 李道云
 * @className: PaymentGroupOrder
 * @description: 支付组订单
 * @create 2018/7/10 17:50
 **/
@Data
@TableName("py_payment_group_order")
public class PaymentGroupOrder extends Model<PaymentGroupOrder> {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 支付组id
     */
    private Long pay_group_id;
    /**
     * 订单id
     */
    private Long order_id;
    /**
     * 订单编号
     */
    private String order_num;
    /**
     * 支付子流水号
     */
    private String sub_pay_trade_num;
    /**
     * 支付金额
     */
    private BigDecimal pay_amt;
    /**
     * 订单描述
     */
    private String order_desc;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
