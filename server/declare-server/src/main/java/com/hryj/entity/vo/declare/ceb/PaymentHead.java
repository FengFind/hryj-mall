package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/************************************
 * 支付单头
 * 
 * @author BF
 *
 */
@XmlRootElement(name = "PaymentHead",namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentHead {

	@XmlElement(name = "guid",namespace="http://www.chinaport.gov.cn/ceb")
	private String guid = "";// 系统唯一序号 C36 是 企业系统生成36位唯一序号（英文字母大写）
	
	@XmlElement(name = "appType",namespace="http://www.chinaport.gov.cn/ceb")
	private String appType = "";// 申报类型 C1 是 申报类型:1-新增 2-变更 3-删除,默认为1
	
	@XmlElement(name = "appTime",namespace="http://www.chinaport.gov.cn/ceb")
	private String appTime = "";// 申报时间 C14 是 申报时间以海关入库反馈时间为准,:格式:YYYYMMDDhhmmss
	
	@XmlElement(name = "appStatus",namespace="http://www.chinaport.gov.cn/ceb")
	private String appStatus = "";// 业务状态 C..3 是 业务状态:1-暂存,2-申报,默认为2
	
	@XmlElement(name = "payCode",namespace="http://www.chinaport.gov.cn/ceb")
	private String payCode = "";// 支付企业代码 C18 是 支付企业的海关备案编码
	
	@XmlElement(name = "payName",namespace="http://www.chinaport.gov.cn/ceb")
	private String payName = "";// 支付企业名称 C..100 是 支付企业的海关备案名称
	
	@XmlElement(name = "payTransactionId",namespace="http://www.chinaport.gov.cn/ceb")
	private String payTransactionId = "";// 支付交易编号 C..60 是 支付企业的订单交易编号
	
	@XmlElement(name = "orderNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String orderNo = "";// 订单编号 C..60 是 电商平台的原始订单编号
	
	@XmlElement(name = "ebpCode",namespace="http://www.chinaport.gov.cn/ceb")
	private String ebpCode = "";// 电商平台代码 C18 是 电商平台的海关备案编码（18位）
	
	@XmlElement(name = "ebpName",namespace="http://www.chinaport.gov.cn/ceb")
	private String ebpName = "";// 电商平台名称 C..100 是 电商平台的海关备案名称
	
	@XmlElement(name = "payerIdType",namespace="http://www.chinaport.gov.cn/ceb")
	private String payerIdType = "";// 支付人证件类型 C1 是 1-身份证,2-其它
	
	@XmlElement(name = "payerIdNumber",namespace="http://www.chinaport.gov.cn/ceb")
	private String payerIdNumber = "";// 支付人证件号码 C..60 是 身份证编号
	
	@XmlElement(name = "payerName",namespace="http://www.chinaport.gov.cn/ceb")
	private String payerName = "";// 支付人姓名 C..60 是
	
	@XmlElement(name = "telephone",namespace="http://www.chinaport.gov.cn/ceb")
	private String telephone = "";// 支付人电话 C..60 否
	
	@XmlElement(name = "amountPaid",namespace="http://www.chinaport.gov.cn/ceb")
	private Double amountPaid = 0d;// 支付金额 N19,5 是 支付企业的订单交易金额
	
	@XmlElement(name = "currency",namespace="http://www.chinaport.gov.cn/ceb")
	private String currency = "";// 支付币制 C3 是 海关标准的参数代码 《JGS-20 海关业务代码集》- 货币代码
	
	@XmlElement(name = "payTime",namespace="http://www.chinaport.gov.cn/ceb")
	private String payTime = "";// 支付时间 C14 是
	
	@XmlElement(name = "note",namespace="http://www.chinaport.gov.cn/ceb")
	private String note = "";// 备注 C..1000 否

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	public String getAppTime() {
		return appTime;
	}

	public void setAppTime(String appTime) {
		this.appTime = appTime;
	}

	public String getAppStatus() {
		return appStatus;
	}

	public void setAppStatus(String appStatus) {
		this.appStatus = appStatus;
	}

	public String getPayCode() {
		return payCode;
	}

	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}

	public String getPayName() {
		return payName;
	}

	public void setPayName(String payName) {
		this.payName = payName;
	}

	public String getPayTransactionId() {
		return payTransactionId;
	}

	public void setPayTransactionId(String payTransactionId) {
		this.payTransactionId = payTransactionId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getEbpCode() {
		return ebpCode;
	}

	public void setEbpCode(String ebpCode) {
		this.ebpCode = ebpCode;
	}

	public String getEbpName() {
		return ebpName;
	}

	public void setEbpName(String ebpName) {
		this.ebpName = ebpName;
	}

	public String getPayerIdType() {
		return payerIdType;
	}

	public void setPayerIdType(String payerIdType) {
		this.payerIdType = payerIdType;
	}

	public String getPayerIdNumber() {
		return payerIdNumber;
	}

	public void setPayerIdNumber(String payerIdNumber) {
		this.payerIdNumber = payerIdNumber;
	}

	public String getPayerName() {
		return payerName;
	}

	public void setPayerName(String payerName) {
		this.payerName = payerName;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public Double getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(Double amountPaid) {
		this.amountPaid = amountPaid;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getPayTime() {
		return payTime;
	}

	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
