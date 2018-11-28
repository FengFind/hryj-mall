package com.hryj.entity.vo.payment;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 罗秋涵
 * @className: PaymentGroupResponseVO
 * @description:
 * @create 2018/7/19 0019 14:54
 **/
@Data
public class PaymentGroupResponseVO {

    private Long id;
    /**
     * 应用唯一标识
     */
    private String app_key;
    /**
     * 支付类型:01-收款,02-支出
     */
    private String payment_type;
    /**
     * 支付方式:01-微信,02-支付宝,03-银联
     */
    private String payment_method;

    /**
     * 支付渠道：100000：红瑞集团，100001：光彩国际
     */
    private Long payment_channel;

    /**
     * 支付账号
     */
    private String payment_account;
    /**
     * 收款账户
     */
    private String receive_account;
    /**
     * 支付总金额
     */
    private BigDecimal pay_total_amt;
    /**
     * 支付交易流水号
     */
    private String pay_trade_num;

    /**
     * 支付组编号
     */
    private String pay_group_sn;

    /**
     * 支付状态:01-支付中,02-支付成功,03-支付失败,04-支付超时
     */
    private String pay_status;
    /**
     * 创建时间
     */
    private Date create_time;
    /**
     * 更新时间
     */
    private Date update_time;
}
