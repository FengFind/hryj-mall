package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/***
 * 
 * 进口国检清单回执
 * 
 * @author YUANLIU
 *
 */
@XmlRootElement(name="CEB700Message")
@XmlAccessorType(XmlAccessType.FIELD)
public class CEB700Message {
	
	@XmlAttribute(name = "guid")
    private String guid;
    @XmlAttribute(name = "version")
    private String version;
	
	@XmlElement(name = "InventoryReturn")
    private InventoryReturn inventoryReturn;//进口国检清单回执明细

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

	public InventoryReturn getInventoryReturn() {
		return inventoryReturn;
	}

	public void setInventoryReturn(InventoryReturn inventoryReturn) {
		this.inventoryReturn = inventoryReturn;
	}
	
	
}
