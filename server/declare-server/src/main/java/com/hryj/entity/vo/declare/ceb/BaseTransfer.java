package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/***
 * 
 * 传输
 * 
 * @author BF
 *
 */
@XmlRootElement(name = "BaseTransfer",namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
public class BaseTransfer {

	/** 传输企业代码 C18 是 报文传输的企业代码（需要与接入客户端的企业身份一致）*/
	@XmlElement(name = "copCode",namespace="http://www.chinaport.gov.cn/ceb")
	private String copCode= "";
	
	/** 传输企业名称 C..100 是 报文传输的企业名称*/
	@XmlElement(name = "copName",namespace="http://www.chinaport.gov.cn/ceb")
	private String copName= "";
	
	/** 报文传输模式 C3 是 默认填写DXP-电子口岸交换平台*/
	@XmlElement(name = "dxpMode",namespace="http://www.chinaport.gov.cn/ceb")
	private String dxpMode= "DXP";
	
	/**报文传输编号 C..30 是 向中国电子口岸数据中心申请接入账号的编号*/
	@XmlElement(name = "dxpId",namespace="http://www.chinaport.gov.cn/ceb")
	private String dxpId= "";
	
	/** 备注 C..1000 否*/
	@XmlElement(name = "note",namespace="http://www.chinaport.gov.cn/ceb")
	private String note= "";
	
	/**
	 * 海关报文头 
	 * 
	 * @param senderId
	 * 					备案编号
	 * @param userNo
	 * 				备案用户名
	 * @return
	 */
	public static BaseTransfer NewHeader(String copCode,String copName,String dxpMode, String dxpId) {
		BaseTransfer bt = new BaseTransfer();
		bt.copCode = copCode;
		bt.copName = copName;
		bt.dxpMode = dxpMode;
		bt.dxpId = dxpId;
		return bt;
	}
	
	/**
	 * 海关报文头 
	 * 
	 * @param senderId
	 * 					备案编号
	 * @param userNo
	 * 				备案用户名
	 * @return
	 */
	public static BaseTransfer NewHeader(String copCode,String copName,String dxpId) {
		BaseTransfer bt = new BaseTransfer();
		bt.copCode = copCode;
		bt.copName = copName;
		bt.dxpId = dxpId;
		return bt;
	}
	
	/**
	 * 海关报文头 
	 * 
	 * @param senderId
	 * 					备案编号
	 * @param userNo
	 * 				备案用户名
	 * @return
	 */
	public static BaseTransfer NewHeader() {
		BaseTransfer bt = new BaseTransfer();
		return bt;
	}

	public String getCopCode() {
		return copCode;
	}

	public void setCopCode(String copCode) {
		this.copCode = copCode;
	}

	public String getCopName() {
		return copName;
	}

	public void setCopName(String copName) {
		this.copName = copName;
	}

	public String getDxpMode() {
		return dxpMode;
	}

	public void setDxpMode(String dxpMode) {
		this.dxpMode = dxpMode;
	}

	public String getDxpId() {
		return dxpId;
	}

	public void setDxpId(String dxpId) {
		this.dxpId = dxpId;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
