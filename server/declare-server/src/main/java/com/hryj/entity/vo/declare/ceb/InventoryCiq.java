package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/***
 * 
 * 进口清单国检信息
 * 
 * @author YUANLIU
 *
 */
@XmlRootElement(name = "InventoryCiq",namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
public class InventoryCiq {
	
	@XmlElement(name = "sender",namespace="http://www.chinaport.gov.cn/ceb")
	private String sender;//发件人
	
	@XmlElement(name = "senderCity",namespace="http://www.chinaport.gov.cn/ceb")
	private String senderCity;//发件城市
	
	@XmlElement(name = "senderCountryCode",namespace="http://www.chinaport.gov.cn/ceb")
	private String senderCountryCode;//发件国别编码
	
	@XmlElement(name = "majorGoodsName",namespace="http://www.chinaport.gov.cn/ceb")
	private String majorGoodsName;//主要货物名称
	
	@XmlElement(name = "checklistType",namespace="http://www.chinaport.gov.cn/ceb")
	private String checklistType;//清单类型
	
	@XmlElement(name = "tradeMode",namespace="http://www.chinaport.gov.cn/ceb")
	private String tradeMode;//贸易方式
	
	@XmlElement(name = "goodsYardCode",namespace="http://www.chinaport.gov.cn/ceb")
	private String goodsYardCode;//码头/货场代码
	
	@XmlElement(name = "packTypeCode",namespace="http://www.chinaport.gov.cn/ceb")
	private String packTypeCode;//包装种类代码
	
	@XmlElement(name = "arrivedPortCode",namespace="http://www.chinaport.gov.cn/ceb")
	private String arrivedPortCode;//装货港.抵运港代码
	
	@XmlElement(name = "inspOrgCode",namespace="http://www.chinaport.gov.cn/ceb")
	private String inspOrgCode;//清单所属(施检)机构
	
	@XmlElement(name = "monitorFlag",namespace="http://www.chinaport.gov.cn/ceb")
	private String monitorFlag;//清单监管标记
	
	public InventoryCiq() {
		
	}
	
	public InventoryCiq(String sender, String senderCity,
			String senderCountryCode, String majorGoodsName,
			String checklistType, String tradeMode, String goodsYardCode,
			String packTypeCode, String arrivedPortCode, String inspOrgCode,
			String monitorFlag) {
		super();
		this.sender = sender;
		this.senderCity = senderCity;
		this.senderCountryCode = senderCountryCode;
		this.majorGoodsName = majorGoodsName;
		this.checklistType = checklistType;
		this.tradeMode = tradeMode;
		this.goodsYardCode = goodsYardCode;
		this.packTypeCode = packTypeCode;
		this.arrivedPortCode = arrivedPortCode;
		this.inspOrgCode = inspOrgCode;
		this.monitorFlag = monitorFlag;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getSenderCity() {
		return senderCity;
	}

	public void setSenderCity(String senderCity) {
		this.senderCity = senderCity;
	}

	public String getSenderCountryCode() {
		return senderCountryCode;
	}

	public void setSenderCountryCode(String senderCountryCode) {
		this.senderCountryCode = senderCountryCode;
	}

	public String getMajorGoodsName() {
		return majorGoodsName;
	}

	public void setMajorGoodsName(String majorGoodsName) {
		this.majorGoodsName = majorGoodsName;
	}

	public String getChecklistType() {
		return checklistType;
	}

	public void setChecklistType(String checklistType) {
		this.checklistType = checklistType;
	}

	public String getTradeMode() {
		return tradeMode;
	}

	public void setTradeMode(String tradeMode) {
		this.tradeMode = tradeMode;
	}

	public String getGoodsYardCode() {
		return goodsYardCode;
	}

	public void setGoodsYardCode(String goodsYardCode) {
		this.goodsYardCode = goodsYardCode;
	}

	public String getPackTypeCode() {
		return packTypeCode;
	}

	public void setPackTypeCode(String packTypeCode) {
		this.packTypeCode = packTypeCode;
	}

	public String getArrivedPortCode() {
		return arrivedPortCode;
	}

	public void setArrivedPortCode(String arrivedPortCode) {
		this.arrivedPortCode = arrivedPortCode;
	}

	public String getInspOrgCode() {
		return inspOrgCode;
	}

	public void setInspOrgCode(String inspOrgCode) {
		this.inspOrgCode = inspOrgCode;
	}

	public String getMonitorFlag() {
		return monitorFlag;
	}

	public void setMonitorFlag(String monitorFlag) {
		this.monitorFlag = monitorFlag;
	}
	
}
