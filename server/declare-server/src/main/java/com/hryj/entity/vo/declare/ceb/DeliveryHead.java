package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/*********************************
 * 
 * 入库明细单数据头
 * 
 * @author BF
 *
 **********************************/
@XmlRootElement(name = "DeliveryHead",namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeliveryHead {
	
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
	
	@XmlElement(name = "copNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String copNo = "";// 企业内部编号 C..20 否 企业内部的入库单编号
	
	@XmlElement(name = "preNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String preNo = "";// 预录入编号 C18 否 电子口岸的入库单编号（R+8位年月日+9位流水号）
	
	@XmlElement(name = "rkdNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String rkdNo = "";// 入库单编号 C18 否 海关审结的入库单编号= 4位海关代码+8位年月日+6流水号
	
	@XmlElement(name = "operatorCode",namespace="http://www.chinaport.gov.cn/ceb")
	private String operatorCode = "";// 监管场所经营人代码 C18 是 监管场所经营人作为第三方对入场所的大包开拆核实包裹
	
	@XmlElement(name = "operatorName",namespace="http://www.chinaport.gov.cn/ceb")
	private String operatorName = "";// 监管场所经营人名称 C..100 是 监管场所经营人作为第三方对入场所的大包开拆核实包裹
	
	@XmlElement(name = "ieFlag",namespace="http://www.chinaport.gov.cn/ceb")
	private String ieFlag = "";// 进出口标记 C1 是 I进口E出口
	
	@XmlElement(name = "trafMode",namespace="http://www.chinaport.gov.cn/ceb")
	private String trafMode = "";// 运输方式 C1 是 用于对应和核销舱单主单的信息
	
	@XmlElement(name = "trafNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String trafNo = "";// 运输工具编号 C..100 是
	
	@XmlElement(name = "voyageNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String voyageNo = "";// 航班航次号 C..32 是
	
	@XmlElement(name = "billNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String billNo = "";// 提运单号 C..37 是
	
	@XmlElement(name = "logisticsCode",namespace="http://www.chinaport.gov.cn/ceb")
	private String logisticsCode = "";// 物流企业代码 C18 是 物流企业的海关备案编码（18位）
	
	@XmlElement(name = "logisticsName",namespace="http://www.chinaport.gov.cn/ceb")
	private String logisticsName = "";// 物流企业名称 C..100 是 物流企业的海关备案名称
	
	@XmlElement(name = "unloadLocation",namespace="http://www.chinaport.gov.cn/ceb")
	private String unloadLocation = "";// 卸货库位 C..100 否 货物卸货的仓储存放位置
	
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

	public String getRkdNo() {
		return rkdNo;
	}

	public void setRkdNo(String rkdNo) {
		this.rkdNo = rkdNo;
	}

	public String getOperatorCode() {
		return operatorCode;
	}

	public void setOperatorCode(String operatorCode) {
		this.operatorCode = operatorCode;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public String getIeFlag() {
		return ieFlag;
	}

	public void setIeFlag(String ieFlag) {
		this.ieFlag = ieFlag;
	}

	public String getTrafMode() {
		return trafMode;
	}

	public void setTrafMode(String trafMode) {
		this.trafMode = trafMode;
	}

	public String getTrafNo() {
		return trafNo;
	}

	public void setTrafNo(String trafNo) {
		this.trafNo = trafNo;
	}

	public String getVoyageNo() {
		return voyageNo;
	}

	public void setVoyageNo(String voyageNo) {
		this.voyageNo = voyageNo;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
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

	public String getUnloadLocation() {
		return unloadLocation;
	}

	public void setUnloadLocation(String unloadLocation) {
		this.unloadLocation = unloadLocation;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
