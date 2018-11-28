package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/****************************
 * 
 * 物流运单状态回执
 * 
 * @author BF
 *
 ****************************/
@XmlRootElement(name = "CEB514Message")
@XmlAccessorType(XmlAccessType.FIELD)
public class CEB514Message {
	
	@XmlAttribute(name="guid")
	private String guid;
	@XmlAttribute(name="version")
	private String version;
	@XmlElement(name = "LogisticsStatusReturn")
	private LogisticsStatusReturn logisticsStatusReturn;

	public LogisticsStatusReturn getLogisticsStatusReturn() {
		return logisticsStatusReturn;
	}

	public void setLogisticsStatusReturn(
			LogisticsStatusReturn logisticsStatusReturn) {
		this.logisticsStatusReturn = logisticsStatusReturn;
	}

}
