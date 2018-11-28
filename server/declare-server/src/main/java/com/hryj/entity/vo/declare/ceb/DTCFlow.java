package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DTCFlow")
@XmlAccessorType(XmlAccessType.FIELD)
public class DTCFlow {
	
	
	//标准入库回执
	private DTCFlowMESASKINFO MES_ASK_INFO;

	public DTCFlowMESASKINFO getMES_ASK_INFO() {
		return MES_ASK_INFO;
	}

	public void setMES_ASK_INFO(DTCFlowMESASKINFO mES_ASK_INFO) {
		MES_ASK_INFO = mES_ASK_INFO;
	}
	
	
}
