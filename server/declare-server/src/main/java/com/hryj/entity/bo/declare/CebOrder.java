package com.hryj.entity.bo.declare;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 白飞
 * @className: CebOrder
 * @description: 订单申报类
 * @create 2018/9/26 10:28
 **/
@Data
@TableName("dec_ceb_order")
public class CebOrder extends  BaseEntity{

    /** UUID */
    @TableField(value = "guid")
    private String guid;

    /** 企业报送类型。1-新增 2-变更 3-删除。默认为1。 */
    @TableField(value = "app_type")
    private String appType;

    /** 企业报送时间。格式:YYYYMMDDhhmmss。 */
    @TableField(value = "app_time")
    private String appTime;

    /** 业务状态:1-暂存,2-申报,默认为2。 */
    @TableField(value = "app_status")
    private String appStatus;

    /** 电子订单类型：I进口 */
    @TableField(value = "order_type")
    private String orderType;

    /** 交易平台的订单编号，同一交易平台的订单编号应唯一。订单编号长度不能超过60位。 */
    @TableField(value = "order_no")
    private String orderNo;

    /** 交易平台支付号 */
    @TableField(value = "pay_order_no")
    private String payOrderNo;

    /** 电商企业原始订单号 */
    @TableField(value = "ebc_order_no")
    private String ebcOrderNo;

    /** 业务类型：I10直邮，I20保税 */
    @TableField(value = "biz_type_code")
    private String bizTypeCode;

    /** 电商平台的海关注册登记编号；电商平台未在海关注册登记，由电商企业发送订单的，以中国电子口岸发布的电商平台标识编号为准。 */
    @TableField(value = "ebp_code")
    private String ebpCode;

    /** 电商平台名称 */
    @TableField(value = "ebp_name")
    private String ebpName;

    /** 电商企业代码 */
    @TableField(value = "ebc_code")
    private String ebcCode;

    /** 电商企业名称 */
    @TableField(value = "ebc_name")
    private String ebcName;

    /** 商品价格 */
    @TableField(value = "goods_value")
    private BigDecimal goodsValue;

    /** 运杂费 */
    @TableField(value = "freight")
    private BigDecimal freight;

    /** 非现金抵扣金额 */
    @TableField(value = "discount")
    private BigDecimal discount;

    /** 代扣税金 */
    @TableField(value = "tax_total")
    private BigDecimal taxTotal;

    /** 实际支付金额 */
    @TableField(value = "actural_paid")
    private BigDecimal acturalPaid;

    /** 币制 */
    @TableField(value = "currency")
    private String currency;

    /** 订购人注册号 */
    @TableField(value = "buyer_reg_no")
    private String buyerRegNo;

    /** 订购人姓名 */
    @TableField(value = "buyer_name")
    private String buyerName;

    /** 订购人证件类型 */
    @TableField(value = "buyer_id_type")
    private String buyerIdType;

    /** 订购人证件号码 */
    @TableField(value = "buyer_id_number")
    private String buyerIdNumber;

    /** 订购人电话号码 */
    @TableField(value = "buyer_telephone")
    private String buyerTelephone;

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

    /** 商品批次号 */
    @TableField(value = "batch_numbers")
    private String batchNumbers;

    /** 收货人 */
    @TableField(value = "consignee")
    private String consignee;

    /** 收货人电话 */
    @TableField(value = "consignee_telephone")
    private String consigneeTelephone;

    /** 收货地址 */
    @TableField(value = "consignee_address")
    private String consigneeAddress;

    /** 收货地址行政区划代码 */
    @TableField(value = "consignee_ditrict")
    private String consigneeDitrict;

    /** 传输企业代码 */
    @TableField(value = "cop_code")
    private String copCode;

    /** 传输企业名称 */
    @TableField(value = "cop_name")
    private String copName;

    /** 报文传输模式 */
    @TableField(value = "dxp_mode")
    private String dxpMode;

    /** 报文传输编号 */
    @TableField(value = "dxp_id")
    private String dxpId;

    /** 备注 */
    @TableField(value = "note")
    private String note;

    /** 订单状态 */
    @TableField(value = "order_status")
    private Integer orderStatus;

    /** 拆单标记,T为拆单 */
    @TableField(value = "split")
    private String split;

}
