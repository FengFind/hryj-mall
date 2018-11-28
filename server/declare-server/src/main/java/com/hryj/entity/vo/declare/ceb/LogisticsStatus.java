package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**************************
 * 
 * 物流运单状态报体
 * 
 * @author BF
 *
 **************************/
@XmlRootElement(name = "LogisticsStatus",namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
public class LogisticsStatus {
	
	@XmlElement(name = "guid",namespace="http://www.chinaport.gov.cn/ceb")
	private String guid = "";// 系统唯一序号 C36 是 企业系统生成36位唯一序号（英文字母大写）
	
	@XmlElement(name = "appType",namespace="http://www.chinaport.gov.cn/ceb")
	private String appType = "";// 申报类型 C1 是 申报类型:1-新增 2-变更 3-删除,默认为1
	
	@XmlElement(name = "appTime",namespace="http://www.chinaport.gov.cn/ceb")
	private String appTime = "";// 申报时间 C14 是 申报时间以海关入库反馈时间为准,:格式:YYYYMMDDhhmmss
	
	@XmlElement(name = "appStatus",namespace="http://www.chinaport.gov.cn/ceb")
	private String appStatus = "";// 业务状态 C..3 是 业务状态:1-暂存,2-申报,默认为2
	
	@XmlElement(name = "logisticsCode",namespace="http://www.chinaport.gov.cn/ceb")
	private String logisticsCode = "";// 物流企业代码 C18 是 物流企业的海关备案编码（18位）
	
	@XmlElement(name = "logisticsName",namespace="http://www.chinaport.gov.cn/ceb")
	private String logisticsName = "";// 物流企业名称 C..100 是 物流企业的海关备案名称
	
	@XmlElement(name = "logisticsNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String logisticsNo = "";// 物流运单编号 C..60 是 物流企业的运单包裹面单号
	
	@XmlElement(name = "logisticsStatus",namespace="http://www.chinaport.gov.cn/ceb")
	private String logisticsStatus = "";// 物流运单状态 C1 是 物流状态 S-签收
	
	@XmlElement(name = "logisticsTime",namespace="http://www.chinaport.gov.cn/ceb")
	private String logisticsTime = "";// 物流状态时间 C14 是
	
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

	public String getLogisticsNo() {
		return logisticsNo;
	}

	public void setLogisticsNo(String logisticsNo) {
		this.logisticsNo = logisticsNo;
	}

	public String getLogisticsStatus() {
		return logisticsStatus;
	}

	public void setLogisticsStatus(String logisticsStatus) {
		this.logisticsStatus = logisticsStatus;
	}

	public String getLogisticsTime() {
		return logisticsTime;
	}

	public void setLogisticsTime(String logisticsTime) {
		this.logisticsTime = logisticsTime;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
