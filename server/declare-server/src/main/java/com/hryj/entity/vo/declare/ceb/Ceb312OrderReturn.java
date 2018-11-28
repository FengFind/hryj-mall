package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/***********************
 * 
 * 订单回执消息体
 * 
 * @author BF
 *
 */
@XmlRootElement(name = "OrderReturn")
@XmlAccessorType(XmlAccessType.FIELD)
public class Ceb312OrderReturn {
	
	private String guid = "";//	系统唯一序号	C36	是	企业系统生成36位唯一序号（英文字母大写）
	private String ebpCode	 = "";//电商平台代码	C18	是	电商平台的海关备案编码（18位）
	private String ebcCode = "";//	电商企业代码	C18	是	电商企业的海关备案编码(18位)
	private String orderNo = "";//	订单编号	C..60	是	原始交易平台的原始订单编号
	private String returnStatus = "";//	回执状态	C1..10	是	操作结果（2电子口岸申报中/3发送海关成功/4发送海关失败/100海关退单/399海关审结）,若小于0数字表示处理异常回执
	private String returnTime = "";//	回执时间	C14	是	操作时间(格式:YYYYMMDDhhmmss)
	private String returnInfo = "";//	回执信息	C..1000	是	备注（如:退单原因）
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
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
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
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
