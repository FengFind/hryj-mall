package com.hryj.entity.vo.payment;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 罗秋涵
 * @className: PaymentGroupInfo
 * @description: 支付组信息
 * @create 2018/7/11 0011 10:22
 **/
@Data
public class PaymentGroupInfo {
    /**
     * 支付组Id
     */
    private Long pay_group_id;

    /**
     *  appKey
     */
    private String app_key;
    /**
     * 订单Id
     */
    private Long order_id;
    /**
     * 订单编号
     */
    private String order_num;
    /**
     * 支付金额
     */
    private BigDecimal pay_amt;
    /**
     * 支付类型
     */
    private String payment_type;
    /**
     * 支付方式
     */
    private String payment_method;

    /**
     * 支付渠道：100000：红瑞集团，100001：光彩国际
     */
    private Long payment_channel;

    /**
     * 交易流水号
     */
    private String pay_trade_num;

    /**
     * 支付组编号
     */
    private String pay_group_sn;
    /**
     * 支付状态
     */
    private String pay_status;


}
