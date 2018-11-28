package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/***************************************
 * 
 * 退货申请单表头
 * 
 * @author BF
 *
 ***************************************/
@XmlRootElement(name = "InvtRefundHead",namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvtRefundHead {
	
	@XmlElement(name = "guid",namespace="http://www.chinaport.gov.cn/ceb")
	private String guid = "";// 系统唯一序号 C36 是 企业系统生成36位唯一序号（英文字母大写）
	
	@XmlElement(name = "appType",namespace="http://www.chinaport.gov.cn/ceb")
	private String appType = "";// 申报类型 C1 是 申报类型:1-新增 2-变更 3-删除,默认为1
	
	@XmlElement(name = "appTime",namespace="http://www.chinaport.gov.cn/ceb")
	private String appTime = "";// 申报时间 C14 是 申报时间以海关入库反馈时间为准,:格式:YYYYMMDDhhmmss
	
	@XmlElement(name = "appStatus",namespace="http://www.chinaport.gov.cn/ceb")
	private String appStatus = "";// 业务状态 C..3 是 业务状态:1-暂存,2-申报,默认为2
	
	@XmlElement(name = "customsCode",namespace="http://www.chinaport.gov.cn/ceb")
	private String customsCode = "";// 申报海关代码 C4 是
	
	@XmlElement(name = "orderNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String orderNo = "";// 订单编号 C..60 是 电商平台的原始订单编号
	
	@XmlElement(name = "ebpCode",namespace="http://www.chinaport.gov.cn/ceb")
	private String ebpCode = "";// 电商平台代码 C18 是 电商平台的海关备案编码（18位）
	
	@XmlElement(name = "ebpName",namespace="http://www.chinaport.gov.cn/ceb")
	private String ebpName = "";// 电商平台名称 C..100 是 电商平台的海关备案名称
	
	@XmlElement(name = "ebcCode",namespace="http://www.chinaport.gov.cn/ceb")
	private String ebcCode = "";// 电商企业代码 C18 是 电商企业的海关备案编码(18位)
	
	@XmlElement(name = "ebcName",namespace="http://www.chinaport.gov.cn/ceb")
	private String ebcName = "";// 电商企业名称 C..100 是 电商企业的海关备案名称
	
	@XmlElement(name = "logisticsNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String logisticsNo = "";// 物流运单编号 C..60 是 物流企业的运单包裹面单号
	
	@XmlElement(name = "logisticsCode",namespace="http://www.chinaport.gov.cn/ceb")
	private String logisticsCode = "";// 物流企业代码 C18 是 物流企业的海关备案编码（18位）
	
	@XmlElement(name = "logisticsName",namespace="http://www.chinaport.gov.cn/ceb")
	private String logisticsName = "";// 物流企业名称 C..100 是 物流企业的海关备案名称
	
	@XmlElement(name = "copNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String copNo = "";// 企业内部编号 C..20 否 企业内部的清单编号
	
	@XmlElement(name = "preNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String preNo = "";// 预录入编号 C18 否 电子口岸的清单编号（B+8位年月日+9位流水号）
	
	@XmlElement(name = "invtNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String invtNo = "";// 清单编号 C18 是 海关审结的清单编号
	
	@XmlElement(name = "buyerIdType",namespace="http://www.chinaport.gov.cn/ceb")
	private String buyerIdType = "";// 订购人证件类型 C1 是 1-身份证；2-其它
	
	@XmlElement(name = "buyerIdNumber",namespace="http://www.chinaport.gov.cn/ceb")
	private String buyerIdNumber = "";// 订购人证件号码 C..20 是 海关监控对象的身份证号
	
	@XmlElement(name = "buyerName",namespace="http://www.chinaport.gov.cn/ceb")
	private String buyerName = "";// 订购人姓名 C..60 是 海关监控对象的姓名
	
	@XmlElement(name = "buyerTelephone",namespace="http://www.chinaport.gov.cn/ceb")
	private String buyerTelephone = "";// 订购人电话 C..30 是 海关监管对象的电话
	
	@XmlElement(name = "agentCode",namespace="http://www.chinaport.gov.cn/ceb")
	private String agentCode = "";// 申报企业代码 C18 是 申报单位的海关备案代码(18位)
	
	@XmlElement(name = "agentName",namespace="http://www.chinaport.gov.cn/ceb")
	private String agentName = "";// 申报企业名称 C..100 是 申报单位的海关备案名称
	
	@XmlElement(name = "reason",namespace="http://www.chinaport.gov.cn/ceb")
	private String reason = "";// 退货原因 C..1000 是 撤单原因
	
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

	public String getCustomsCode() {
		return customsCode;
	}

	public void setCustomsCode(String customsCode) {
		this.customsCode = customsCode;
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

	public String getEbcCode() {
		return ebcCode;
	}

	public void setEbcCode(String ebcCode) {
		this.ebcCode = ebcCode;
	}

	public String getEbcName() {
		return ebcName;
	}

	public void setEbcName(String ebcName) {
		this.ebcName = ebcName;
	}

	public String getLogisticsNo() {
		return logisticsNo;
	}

	public void setLogisticsNo(String logisticsNo) {
		this.logisticsNo = logisticsNo;
	}

	public String getLogisticsCode() {
		return logisticsCode;
	}

	public void setLogisticsCode(String logisticsCode) {
		this.logisticsCode = logisticsCode;
	}

	public String getLogisticsName() {
		return logisticsName;
	}

	public void setLogisticsName(String logisticsName) {
		this.logisticsName = logisticsName;
	}

	public String getCopNo() {
		return copNo;
	}

	public void setCopNo(String copNo) {
		this.copNo = copNo;
	}

	public String getPreNo() {
		return preNo;
	}

	public void setPreNo(String preNo) {
		this.preNo = preNo;
	}

	public String getInvtNo() {
		return invtNo;
	}

	public void setInvtNo(String invtNo) {
		this.invtNo = invtNo;
	}

	public String getBuyerIdType() {
		return buyerIdType;
	}

	public void setBuyerIdType(String buyerIdType) {
		this.buyerIdType = buyerIdType;
	}

	public String getBuyerIdNumber() {
		return buyerIdNumber;
	}

	public void setBuyerIdNumber(String buyerIdNumber) {
		this.buyerIdNumber = buyerIdNumber;
	}

	public String getBuyerName() {
		return buyerName;
	}

	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	public String getBuyerTelephone() {
		return buyerTelephone;
	}

	public void setBuyerTelephone(String buyerTelephone) {
		this.buyerTelephone = buyerTelephone;
	}

	public String getAgentCode() {
		return agentCode;
	}

	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
