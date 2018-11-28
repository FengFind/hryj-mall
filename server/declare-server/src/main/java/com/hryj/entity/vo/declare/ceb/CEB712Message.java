package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**************************************************************
 * 
 * 入库明细单回执
 * 
 * @author BF
 *
 **************************************************************/
@XmlRootElement(name = "CEB712Message")
@XmlAccessorType(XmlAccessType.FIELD)
public class CEB712Message {
	
	@XmlAttribute(name="guid")
	private String guid;
	@XmlAttribute(name="version")
	private String version;
	
	@XmlElement(name = "DeliveryReturn")
	private DeliveryReturn deliveryReturn;

	public DeliveryReturn getDeliveryReturn() {
		return deliveryReturn;
	}

	public void setDeliveryReturn(DeliveryReturn deliveryReturn) {
		this.deliveryReturn = deliveryReturn;
	}

}
