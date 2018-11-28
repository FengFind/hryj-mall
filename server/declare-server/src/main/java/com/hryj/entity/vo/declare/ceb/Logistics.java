package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/*********************************
 * 
 * 物流
 * 
 * @author BF
 *
 **********************************/
@XmlRootElement(name = "Logistics",namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
public class Logistics {

	@XmlElement(name = "LogisticsHead",namespace="http://www.chinaport.gov.cn/ceb")
	private LogisticsHead logisticsHead = new LogisticsHead();

	public Logistics(){
		
	}
	
	public Logistics(LogisticsHead logisticsHead) {
		super();
		this.logisticsHead = logisticsHead;
	}

	public LogisticsHead getLogisticsHead() {
		return logisticsHead;
	}

	public void setLogisticsHead(LogisticsHead logisticsHead) {
		this.logisticsHead = logisticsHead;
	}

}
