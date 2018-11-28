package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/******************************
 * 
 * 撤销申请单回执
 * 
 * @author BF
 *
 */
@XmlRootElement(name = "CEB624Message")
@XmlAccessorType(XmlAccessType.FIELD)
public class CEB624Message {
	
	@XmlAttribute(name="guid")
	private String guid;
	@XmlAttribute(name="version")
	private String version;
	
	@XmlElement(name = "InvtCancelReturn")
	private InvtCancelReturn invtCancelReturn;

	public InvtCancelReturn getInvtCancelReturn() {
		return invtCancelReturn;
	}

	public void setInvtCancelReturn(InvtCancelReturn invtCancelReturn) {
		this.invtCancelReturn = invtCancelReturn;
	}

}
