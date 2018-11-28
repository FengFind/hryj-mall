package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/*******************
 * 
 * 
 * 退货申请单回执内容
 * 
 * @author BF
 *
 */
@XmlRootElement(name = "RefundReturn")
@XmlAccessorType(XmlAccessType.FIELD)
public class RefundReturn {
	private String guid = "";// 系统唯一序号 C36 是 电子口岸生成36位唯一序号（英文字母大写）
	private String copNo = "";// 企业内部编号 C..20 是 企业内部生成标识清单的编号
	private String preNo = "";// 预录入编号 C18 是 电子口岸生成标识清单的编号
	private String invtNo = "";// 清单编号 C18 否 海关审批生成标识清单的编号
	private String returnStatus = "";// 回执状态 C1..10 是
								// 操作结果（1电子口岸已暂存/2电子口岸申报中/3发送海关成功/4发送海关失败/100海关退单/399海关审结）,若小于0数字表示处理异常回执
	private String returnTime = "";// 回执时间 C14 是 操作时间(格式:YYYYMMDDhhmmss)
	private String returnInfo = "";// 回执信息 C..1000 是 备注（如不通过原因）
	
	private String customsCode = "";//申报海关代码
	private String ebpCode = "";//电商平台代码
	private String ebcCode = "";//电商企业代码
	private String agentCode = "";//申报企业代码

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
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

	public String getCustomsCode() {
		return customsCode;
	}

	public void setCustomsCode(String customsCode) {
		this.customsCode = customsCode;
	}

	public String getEbpCode() {
		return ebpCode;
	}

	public void setEbpCode(String ebpCode) {
		this.ebpCode = ebpCode;
	}

	public String getEbcCode() {
		return ebcCode;
	}

	public void setEbcCode(String ebcCode) {
		this.ebcCode = ebcCode;
	}

	public String getAgentCode() {
		return agentCode;
	}

	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}
	
}
