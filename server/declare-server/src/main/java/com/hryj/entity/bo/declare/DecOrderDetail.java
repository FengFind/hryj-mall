/*
 * 
 * 
 * 
 */
package com.hryj.entity.bo.declare;

import lombok.Data;

import java.math.BigDecimal;
/**
 * Entity - DecOrderDetail
 * 
 * 
 * 
 */
@Data
public class DecOrderDetail{

	
	/** 订单明细ID */
	private Long orderOrgDetailId;
	
	/** 系统唯一编码 */
	private String guid;
	
	/** 商品序号 */
	private Integer gnum;
	
	/** 企业商品货号 */
	private String itemNo;
	
	/** 企业商品名称 */
	private String itemName;
	
	/** 单位 */
	private String unit;
	
	/** 商品数量 */
	private Integer qty;
	
	/** 单价 */
	private BigDecimal price;
	
	/** 总价 */
	private BigDecimal totalPrice;
	
	/** 币制 */
	private String currency;
	
	/** 原产国 */
	private String country;
	
	/** 企业商品描述  */
	private String itemDescribe;
	
	/** 条形码 */
	private String barCode;
	
	/** 备注 */
	private String note;

}