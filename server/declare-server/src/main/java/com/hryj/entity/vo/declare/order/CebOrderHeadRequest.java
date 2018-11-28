package com.hryj.entity.vo.declare.order;

import com.google.common.collect.Lists;
import lombok.Data;

import javax.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author 白飞
 * @className: CebOrderHeadRequest
 * @description:
 * @create 2018/10/11 17:49
 **/
@XmlRootElement(name = "OrderHead")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CebOrderHeadRequest {

    /** 系统唯一编码 */
    private String guid = "";

    /** 海关申报订单编号 */
    private String orderNo = "";

    /** 商户支付号 */
    private String payOrderNo = "";

    /** 电商企业代码 */
    private String ebcCode = "";

    /** 电商企业名称 */
    private String ebcName = "";

    /** 关区代码 */
    private String customsCode = "";

    /** 分拣线标识 */
    private String sortlineId = "";

    /** 物流编码-由申报系统提供 */
    private String logisticsKey = "";

    /** 物流单号 */
    private String waybill = "";

    /** 业务类型 */
    private String bizTypeCode = "";

    /** 商品价格 */
    private BigDecimal goodsValue = BigDecimal.ZERO;

    /** 运杂费 */
    private BigDecimal freight = BigDecimal.ZERO;

    /** 非现金抵扣金额 */
    private BigDecimal discount = BigDecimal.ZERO;

    /** 代扣税款 */
    private BigDecimal taxTotal = BigDecimal.ZERO;

    /** 实际支付金额 */
    private BigDecimal acturalPaid = BigDecimal.ZERO;

    /** 币制 */
    private String currency = "142";

    /** 订购人注册号 */
    private String buyerRegNo = "";

    /** 订购人姓名 */
    private String buyerName = "";

    /** 订购人证件类型 */
    private String buyerIdType = "";

    /** 订购人证件号码 */
    private String buyerIdNumber = "";

    /** 订购人电话号码 */
    private String buyerTelephone = "";

    /** 收货人姓名 */
    private String consignee = "";

    /** 收货人电话 */
    private String consigneeTelephone = "";

    /** 收货地址 */
    private String consigneeAddress = "";

    /** 支付编码 -由申报系统提供 */
    private String payKey = "";

    /** 支付企业唯一的支付流水号 */
    private String payTransactionId = "";

    /** 支支付时间，格式:YYYYMMDDhhmmss */
    private String payTime = "";

    /** 收货地址行政区划代码 */
    private String consigneeDitrict = "";

    /** 区内账册编号,申报系统提供，为空系统会获取默认区内企业; 与emsNo两个必填一个 */
    private String sysAreaCode = "";

    /** 账册编号- 与sysCode两个必填一个*/
    private String emsNo = "";

    /** 运输方式 */
    private String trafModel = "";

    /** 毛重 */
    private BigDecimal grossWeight = BigDecimal.ZERO;

    /** 净重 */
    private BigDecimal netWeight = BigDecimal.ZERO;

    /** 拆单标记-为T时拆单 */
    private String split = "F";

    /** 支付标记：appKey + partyId  + paymentMethod */
    private String payFlag = "";
}
