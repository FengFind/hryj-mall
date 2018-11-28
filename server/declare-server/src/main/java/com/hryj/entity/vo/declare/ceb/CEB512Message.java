package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/*********************************
 * 
 * 物流回执消息
 * 
 * @author BF
 *
 **********************************/
@XmlRootElement(name = "CEB512Message")
@XmlAccessorType(XmlAccessType.FIELD)
public class CEB512Message {
	
	@XmlAttribute(name="guid")
	private String guid;
	@XmlAttribute(name="version")
	private String version;
	
	@XmlElement(name = "LogisticsReturn")
	private LogisticsReturn logisticsReturn;

	public LogisticsReturn getLogisticsReturn() {
		return logisticsReturn;
	}

	public void setLogisticsReturn(LogisticsReturn logisticsReturn) {
		this.logisticsReturn = logisticsReturn;
	}

}
