package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**************************************************************
 * 
 * 支付回执
 * 
 * @author BF
 *
 **************************************************************/
@XmlRootElement(name="CEB412Message")
@XmlAccessorType(XmlAccessType.FIELD)
public class CEB412Message {
	@XmlAttribute(name="guid")
	private String guid;
	@XmlAttribute(name="version")
	private String version;
	@XmlElement(name="PaymentReturn")
	private PaymentReturn paymentReturn;

	public PaymentReturn getPaymentReturn() {
		return paymentReturn;
	}

	public void setPaymentReturn(PaymentReturn paymentReturn) {
		this.paymentReturn = paymentReturn;
	}

}
