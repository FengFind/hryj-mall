/*
 * 
 * 
 * 
 */
package com.hryj.entity.vo.declare.order;

import com.google.common.collect.Lists;
import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Entity - 申报订单请求
 * 
 * 
 * 
 */
@XmlRootElement(name = "CebOrder")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CebOrderRequest{

	@XmlElement(name = "OrderHead")
	private CebOrderHeadRequest orderHead = new CebOrderHeadRequest();

	/** 明细 */
	@XmlElement(name = "OrderList")
	private List<CebOrderDetailRequest> orderList = Lists.newArrayList();

	/** 获取商品货号集合 */
	@XmlTransient
	public List<String> getItemNos(){
		List<String> itemNos = Lists.newArrayList();
		if(orderList == null || orderList.size() == 0){
			return null;
		}
		for (CebOrderDetailRequest cebOrderDetailRequest : this.getOrderList()){
			itemNos.add(cebOrderDetailRequest.getItemNo());
		}
		return  itemNos;
	}

}