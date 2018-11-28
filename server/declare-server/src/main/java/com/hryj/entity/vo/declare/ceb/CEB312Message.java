package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.*;

/***********************
 * 
 * 订单回执头
 * 
 * @author BF
 *
 */
@XmlRootElement(name="CEB312Message")
@XmlAccessorType(XmlAccessType.FIELD)
public class CEB312Message {

	@XmlAttribute(name="guid")
	private String guid;
	@XmlAttribute(name="version")
	private String version;
	
	@XmlElement(name = "OrderReturn")
	private Ceb312OrderReturn orderReturn;

	public Ceb312OrderReturn getOrderReturn() {
		return orderReturn;
	}

	public void setOrderReturn(Ceb312OrderReturn orderReturn) {
		this.orderReturn = orderReturn;
	}

}
