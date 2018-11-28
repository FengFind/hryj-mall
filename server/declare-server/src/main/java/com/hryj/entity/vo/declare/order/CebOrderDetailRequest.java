/*
 * 
 * 
 * 
 */
package com.hryj.entity.vo.declare.order;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
/**
 * Entity -
 * 
 * 
 * 
 */
@XmlRootElement(name = "CebOrderDetail")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CebOrderDetailRequest{
	
	/** 企业商品货号 */
	private String itemNo = "";
	
	/** 企业商品名称 */
	private String itemName = "";
	
	/** 商品数量 */
	private Integer qty = 0;
	
	/** 单价 */
	private BigDecimal price = BigDecimal.ZERO;

}