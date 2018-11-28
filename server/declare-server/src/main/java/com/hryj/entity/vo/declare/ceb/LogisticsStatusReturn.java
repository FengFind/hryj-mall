package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/****************************
 * 
 * 物流运单状态回执明细
 * 
 * @author BF
 *
 ****************************/
@XmlRootElement(name = "LogisticsStatusReturn")
@XmlAccessorType(XmlAccessType.FIELD)
public class LogisticsStatusReturn {

	private String guid = "";// 系统唯一序号 C36 是 电子口岸生成36位唯一序号（英文字母大写）
	private String logisticsCode = "";// 物流企业代码 C18 是 物流企业的海关备案编码（10位）
	private String logisticsNo = "";// 物流运单编号 C..60 是 物流企业的运单包裹面单号
	private String logisticsStatus = "";// 物流状态 C1 是 物流状态,S-签收
	private String returnStatus = "";// 回执状态 C1..10 是
								// 操作结果（2电子口岸申报中/3发送海关成功/4发送海关失败/100海关退单/399海关审结）,若小于0数字表示处理异常回执
	private String returnTime = "";// 回执时间 C14 是 操作时间(格式:YYYYMMDDhhmmss)
	private String returnInfo = "";// 回执信息 C..1000 是 备注（如:退单原因）

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getLogisticsCode() {
		return logisticsCode;
	}

	public void setLogisticsCode(String logisticsCode) {
		this.logisticsCode = logisticsCode;
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

	public String getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(String returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getReturnTime() {
		return returnTime;
	}

	public void setReturnTime(String returnTime) {
		this.returnTime = returnTime;
	}

	public String getReturnInfo() {
		return returnInfo;
	}

	public void setReturnInfo(String returnInfo) {
		this.returnInfo = returnInfo;
	}

}
