package com.hryj.entity.vo.declare.ceb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/***************************************
 * 
 * 退货申请单
 * 
 * @author BF
 *
 ***************************************/
@XmlRootElement(name = "InvtRefund",namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvtRefund {
	@XmlElement(name = "InvtRefundHead",namespace="http://www.chinaport.gov.cn/ceb")
	private InvtRefundHead invtRefundHead = new InvtRefundHead();
	@XmlElement(name = "InvtRefundList",namespace="http://www.chinaport.gov.cn/ceb")
	private List<InvtRefundList> invtRefundLists = new ArrayList<InvtRefundList>();

	public InvtRefundHead getInvtRefundHead() {
		return invtRefundHead;
	}

	public void setInvtRefundHead(InvtRefundHead invtRefundHead) {
		this.invtRefundHead = invtRefundHead;
	}

	public List<InvtRefundList> getInvtRefundLists() {
		return invtRefundLists;
	}

	public void setInvtRefundLists(List<InvtRefundList> invtRefundLists) {
		this.invtRefundLists = invtRefundLists;
	}
}
