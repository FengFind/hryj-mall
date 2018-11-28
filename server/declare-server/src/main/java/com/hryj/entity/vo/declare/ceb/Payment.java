package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/***
 * 支付单头
 * 
 * @author BF
 *
 */
@XmlRootElement(name = "Payment",namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
public class Payment {

	@XmlElement(name = "PaymentHead",namespace="http://www.chinaport.gov.cn/ceb")
	private PaymentHead paymentHead = new PaymentHead();

	public PaymentHead getPaymentHead() {
		return paymentHead;
	}

	public void setPaymentHead(PaymentHead paymentHead) {
		this.paymentHead = paymentHead;
	}
	
}
