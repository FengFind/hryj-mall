package com.hryj.entity.vo.declare.ceb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/*********************************
 * 
 * 入库明细单数据
 * 
 * @author BF
 *
 **********************************/
@XmlRootElement(name = "Delivery",namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
public class Delivery {
	@XmlElement(name = "DeliveryHead",namespace="http://www.chinaport.gov.cn/ceb")
	private DeliveryHead deliveryHead = new DeliveryHead();
	@XmlElement(name = "DeliveryList",namespace="http://www.chinaport.gov.cn/ceb")
	private List<DeliveryList> deliveryLists = new ArrayList<DeliveryList>();

	public DeliveryHead getDeliveryHead() {
		return deliveryHead;
	}

	public void setDeliveryHead(DeliveryHead deliveryHead) {
		this.deliveryHead = deliveryHead;
	}

	public List<DeliveryList> getDeliveryLists() {
		return deliveryLists;
	}

	public void setDeliveryLists(List<DeliveryList> deliveryLists) {
		this.deliveryLists = deliveryLists;
	}
}
