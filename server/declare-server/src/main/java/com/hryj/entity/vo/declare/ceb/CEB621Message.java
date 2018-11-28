package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/****************************
 * 
 * 进口清单数据
 * 
 * @author BF
 *
 ****************************/
@XmlRootElement(name = "CEB621Message",namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
public class CEB621Message {
	
	@XmlAttribute(name = "guid")
    private String guid;

    @XmlAttribute(name = "version")
    private String version = "1.0";
    
	@XmlElement(name = "Inventory",namespace="http://www.chinaport.gov.cn/ceb")
	private Inventory inventory = new Inventory();
	
	@XmlElement(name = "BaseTransfer",namespace="http://www.chinaport.gov.cn/ceb")
	private BaseTransfer baseTransfer = new BaseTransfer();
	
	public CEB621Message(){
	}
	
	public CEB621Message(String guid, String version, Inventory inventory, BaseTransfer baseTransfer) {
		super();
		this.guid = guid;
		this.version = version;
		this.inventory = inventory;
		this.baseTransfer = baseTransfer;
	}

	public Inventory getInventory() {
		return inventory;
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

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public BaseTransfer getBaseTransfer() {
		return baseTransfer;
	}

	public void setBaseTransfer(BaseTransfer baseTransfer) {
		this.baseTransfer = baseTransfer;
	}

}
