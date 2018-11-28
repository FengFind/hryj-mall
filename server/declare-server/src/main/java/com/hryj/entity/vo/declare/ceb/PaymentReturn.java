package com.hryj.entity.vo.declare.ceb;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**************************************************************
 * 
 * 支付回执
 * 
 * @author BF
 *
 */
@XmlRootElement(name = "PaymentReturn")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class PaymentReturn {

	/** 系统唯一序号 C36 是 电子口岸生成36位唯一序号（英文字母大写） */
	private String guid = "";
	/** 支付企业代码 C18 是 支付企业的海关备案编码 */
	private String payCode = "";
 	/** 支付交易编号 C..60 是 支付企业的订单交易编号 */
	private String payTransactionId = "";
 	/** 回执状态 C1..10 是 操作结果（2电子口岸申报中/3发送海关成功/4发送海关失败/100海关退单/399海关审结）,若小于0数字表示处理异常回执 */
	private String returnStatus = "";
	 /** 回执时间 C14 是 操作时间(格式:YYYYMMDDhhmmss) */
	private String returnTime = "";
	 /** 回执信息 C..1000 是 备注（如:退单原因） */
	private String returnInfo = "";
	 /** 支付申报号 主要用于拆单 */
	private String payDeclareNo;
	/** 订单号 */
	private String orderNo;


}
