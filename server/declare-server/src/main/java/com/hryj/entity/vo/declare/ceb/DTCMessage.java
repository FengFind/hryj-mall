package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@SuppressWarnings("serial")
@XmlRootElement(name="DTC_Message")
@XmlAccessorType(XmlAccessType.FIELD)
public class DTCMessage implements Serializable{

	private DTCMessageHead MessageHead;

	private DTCMessageBody MessageBody;

	public DTCFlow NewDTCFlow() {
		this.MessageBody = new DTCMessageBody();
        DTCFlow a = new DTCFlow();
        this.MessageBody.setDTCFlow(a);
        return a;
	}
	public DTCMessageHead getMessageHead() {
		return MessageHead;
	}
	public void setMessageHead(DTCMessageHead messageHead) {
		MessageHead = messageHead;
	}
	public DTCMessageBody getMessageBody() {
		return MessageBody;
	}
	public void setMessageBody(DTCMessageBody messageBody) {
		MessageBody = messageBody;
	}
}
