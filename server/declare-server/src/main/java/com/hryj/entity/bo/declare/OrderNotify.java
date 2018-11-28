package com.hryj.entity.bo.declare;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 白飞
 * @className: OrderNotify
 * @description:
 * @create 2018/10/10 9:50
 **/
@Data
@TableName("dec_order_notify")
public class OrderNotify extends BaseEntity{

    /** 电商企业海关备案编码 */
    @TableField(value = "ebc_code")
    private String ebcCode;
    /** 电商企业海关备案名称 */
    @TableField(value = "ebc_name")
    private String ebcName;
    /** 申报订单号 */
    @TableField(value = "order_no")
    private String orderNo;
    /** 申报内部号 */
    @TableField(value = "cop_no")
    private String copNo;
    /** 物流编码-如：yunda、ems等 */
    @TableField(value = "waybill_key")
    private String waybillKey;
    /** 物流名称 */
    @TableField(value = "waybill_name")
    private String waybillName;
    /** 物流单号 */
    @TableField(value = "waybill")
    private String waybill;
    /** 申报金额 */
    @TableField(value = "order_amount")
    private BigDecimal orderAmount;
    /** 申报税金 */
    @TableField(value = "tax_amount")
    private BigDecimal taxAmount;
    /** 通知地址 */
    @TableField(value = "notify_url")
    private String notifyUrl;
}
