package com.hryj.entity.bo.declare;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 白飞
 * @className: CebPay
 * @description:
 * @create 2018/9/26 11:16
 **/
@Data
@TableName("dec_ceb_pay")
public class CebPay extends BaseEntity{

    /** 系统唯一序号 */
    @TableField(value = "guid")
    private String guid;

    /** 报送类型 */
    @TableField(value = "app_type")
    private String appType;

    /** 报送时间 */
    @TableField(value = "app_time")
    private String appTime;

    /** 业务状态 */
    @TableField(value = "app_status")
    private String appStatus;

    /** 支付企业代码KEY */
    @TableField(value = "pay_key")
    private String payKey;

    /** 支付企业代码 */
    @TableField(value = "pay_code")
    private String payCode;

    /** 支付企业名称 */
    @TableField(value = "pay_name")
    private String payName;

    /** 支付交易编号 */
    @TableField(value = "pay_transaction_id")
    private String payTransactionId;

    /** 支付申报号 */
    @TableField(value = "pay_declare_no")
    private String payDeclareNo;

    /** 订单编号 */
    @TableField(value = "order_no")
    private String orderNo;

    /** 平台支付号 */
    @TableField(value = "pay_order_no")
    private String payOrderNo;

    /** 电商平台代码 */
    @TableField(value = "ebp_code")
    private String ebpCode;

    /** 电商平台名称 */
    @TableField(value = "ebp_name")
    private String ebpName;

    /** 支付人证件类型 */
    @TableField(value = "payer_id_type")
    private String payerIdType;

    /** 支付人证件号码 */
    @TableField(value = "payer_id_number")
    private String payerIdNumber;

    /** 支付人姓名 */
    @TableField(value = "payer_name")
    private String payerName;

    /** 支付人电话 */
    @TableField(value = "telephone")
    private String telephone;

    /** 支付金额 */
    @TableField(value = "amount_paid")
    private BigDecimal amountPaid;

    /** 支付币值 */
    @TableField(value = "currency")
    private String currency;

    /** 支付时间 */
    @TableField(value = "pay_time")
    private String payTime;

    /** 传输企业代码 */
    @TableField(value = "cop_code")
    private String copCode;

    /** 传输企业名称 */
    @TableField(value = "cop_name")
    private String copName;

    /** 报文传输模式 */
    @TableField(value = "dxp_model")
    private String dxpModel;

    /** 报文传输编号 */
    @TableField(value = "dxp_id")
    private String dxpId;

    /** 备注 */
    @TableField(value = "note")
    private String note;

    /** 支付申报状态：0-未申报，1-成功，2-失败 */
    @TableField(value = "pay_status")
    private Integer payStatus;

    /** 拆单标志-T拆单，F为不拆单 */
    @TableField(value = "split")
    private String split;

    /** 支付标识-区分用户端和门店端 */
    @TableField(value = "pay_flag")
    private String payFlag;


}
