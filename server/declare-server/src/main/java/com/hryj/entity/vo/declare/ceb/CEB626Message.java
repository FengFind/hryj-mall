package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/*******************
 * 
 * 
 * 退货申请单回执
 * 
 * @author BF
 *
 */
@XmlRootElement(name = "CEB626Message")
@XmlAccessorType(XmlAccessType.FIELD)
public class CEB626Message {
	
	@XmlAttribute(name="guid")
	private String guid;
	@XmlAttribute(name="version")
	private String version;
	
	@XmlElement(name = "RefundReturn")
	private RefundReturn refundReturn;

	public RefundReturn getRefundReturn() {
		return refundReturn;
	}

	public void setRefundReturn(RefundReturn refundReturn) {
		this.refundReturn = refundReturn;
	}

}
