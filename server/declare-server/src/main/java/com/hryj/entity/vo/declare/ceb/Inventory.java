package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "Inventory",namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
public class Inventory {
	@XmlElement(name="InventoryHead",namespace="http://www.chinaport.gov.cn/ceb")
	private InventoryHead inventoryHead = new InventoryHead();//进口清单表头
	
	/*@XmlElement(name="InventoryCiq",namespace="http://www.chinaport.gov.cn/ceb")
	private InventoryCiq inventoryCiq = new InventoryCiq();*///进口清单国检信息
	
	@XmlElement(name="InventoryList",namespace="http://www.chinaport.gov.cn/ceb")
	private List<InventoryList> inventoryLists = new ArrayList<InventoryList>();//进口清单表体
	
	public InventoryHead getInventoryHead() {
		return inventoryHead;
	}

	public void setInventoryHead(InventoryHead inventoryHead) {
		this.inventoryHead = inventoryHead;
	}

	public List<InventoryList> getInventoryLists() {
		return inventoryLists;
	}

	public void setInventoryLists(List<InventoryList> inventoryLists) {
		this.inventoryLists = inventoryLists;
	}
}
