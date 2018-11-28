package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/******************************
 * 
 * 进口清单回执消息
 * 
 * @author BF
 *
 */
@XmlRootElement(name = "CEB622Message")
@XmlAccessorType(XmlAccessType.FIELD)
public class CEB622Message {
	
	@XmlAttribute(name="guid")
	private String guid;
	@XmlAttribute(name="version")
	private String version;
	
	@XmlElement(name = "InventoryReturn")
	private InventoryReturn inventoryReturn;

	public InventoryReturn getInventoryReturn() {
		return inventoryReturn;
	}

	public void setInventoryReturn(InventoryReturn inventoryReturn) {
		this.inventoryReturn = inventoryReturn;
	}

}
