package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/***
 * 订单
 * 
 * @author BF
 *
 */
@XmlRootElement(name="Order",namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
public class CEB311Order {
	
	//订单头
	@XmlElement(name = "OrderHead",namespace="http://www.chinaport.gov.cn/ceb")
	private OrderHead orderHead = new OrderHead();
	
	//订单明细
	@XmlElement(name = "OrderList",namespace="http://www.chinaport.gov.cn/ceb")
	private List<OrderList> orderLists = new ArrayList<OrderList>();
	
	public CEB311Order() {
	}

	public CEB311Order(OrderHead orderHead, List<OrderList> orderLists) {
		super();
		this.orderHead = orderHead;
		this.orderLists = orderLists;
	}

	public OrderHead getOrderHead() {
		return orderHead;
	}

	public void setOrderHead(OrderHead orderHead) {
		this.orderHead = orderHead;
	}

	public List<OrderList> getOrderLists() {
		return orderLists;
	}

	public void setOrderLists(List<OrderList> orderLists) {
		this.orderLists = orderLists;
	}
	
}
