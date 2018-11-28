package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/***
 * 
 * 商品消息回执
 * 
 * @author YUANLIU
 *
 */
@XmlRootElement(name="SkuReturn")
@XmlAccessorType(XmlAccessType.FIELD)
public class SkuReturn {
	
	private String guid;// 系统唯一序号 C36 是 电子口岸生成36位唯一序号（英文字母大写）
	
	private String ebpCode;//电商平台代码
	
	private String ebcCode;//电商企业代码
	
	private String sku;//商品编码
	
	private String ieFlag;//进出口标记 C1 是 I进口E出口
	
	private String returnStatus;// 回执状态 C1..10 是
								// 操作结果（1电子口岸已暂存/2电子口岸申报中/3发送海关成功/4发送海关失败/100海关退单/399海关审结/800实货放行/899结关/501扣留移送通关/502扣留移送缉私/503扣留移送法规/599其它扣留）,若小于0数字表示处理异常回执
	
	private String returnTime;// 回执时间 C14 是 操作时间(格式:YYYYMMDDhhmmss)
	
	private String returnInfo;// 回执信息 C..1000 是 备注（如不通过原因）

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

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getIeFlag() {
		return ieFlag;
	}

	public void setIeFlag(String ieFlag) {
		this.ieFlag = ieFlag;
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
