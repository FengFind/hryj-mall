package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/*********************************
 * 
 * 入库明细单数据体
 * 
 * @author BF
 *
 **********************************/
@XmlRootElement(name = "DeliveryList",namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeliveryList {
	
	@XmlElement(name = "gnum",namespace="http://www.chinaport.gov.cn/ceb")
	private Integer gnum = 1;// 序号 N4 是 从1开始的递增序号
	
	@XmlElement(name = "logisticsNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String logisticsNo = "";// 物流运单编号 C..60 是 物流企业的运单包裹面单号
	
	@XmlElement(name = "note",namespace="http://www.chinaport.gov.cn/ceb")
	private String note = "";// 备注 C..1000 否

	public Integer getGnum() {
		return gnum;
	}

	public void setGnum(Integer gnum) {
		this.gnum = gnum;
	}

	public String getLogisticsNo() {
		return logisticsNo;
	}

	public void setLogisticsNo(String logisticsNo) {
		this.logisticsNo = logisticsNo;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
