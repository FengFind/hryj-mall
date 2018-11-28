package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/*********************************
 * 
 * 物流
 * 
 * @author BF
 *
 **********************************/
@XmlRootElement(name = "CEB511Message",namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
public class CEB511Message {
	
	@XmlAttribute(name = "guid")
    private String guid;

    @XmlAttribute(name = "version")
    private String version = "1.0";
	
	@XmlElement(name = "Logistics",namespace="http://www.chinaport.gov.cn/ceb")
	private Logistics logistics = new Logistics();
	
	@XmlElement(name = "BaseTransfer",namespace="http://www.chinaport.gov.cn/ceb")
	private BaseTransfer baseTransfer = new BaseTransfer();

	public CEB511Message(){
		
	}
	
	public CEB511Message(String guid, String version, Logistics logistics, BaseTransfer baseTransfer) {
		super();
		this.guid = guid;
		this.version = version;
		this.logistics = logistics;
		this.baseTransfer = baseTransfer;
	}

	public BaseTransfer getBaseTransfer() {
		return baseTransfer;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setBaseTransfer(BaseTransfer baseTransfer) {
		this.baseTransfer = baseTransfer;
	}

	public Logistics getLogistics() {
		return logistics;
	}

	public void setLogistics(Logistics logistics) {
		this.logistics = logistics;
	}

}
