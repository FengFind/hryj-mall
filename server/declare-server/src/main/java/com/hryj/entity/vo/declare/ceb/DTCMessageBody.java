package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/// 信息体
@XmlRootElement(name="MessageBody")
@XmlAccessorType(XmlAccessType.FIELD)
public class DTCMessageBody {

	private DTCFlow DTCFlow;

	public DTCFlow getDTCFlow() {
		return DTCFlow;
	}

	public void setDTCFlow(DTCFlow dTCFlow) {
		DTCFlow = dTCFlow;
	}
}
