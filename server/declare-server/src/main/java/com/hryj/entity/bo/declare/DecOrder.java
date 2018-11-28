/*
 * 
 * 
 * 
 */
package com.hryj.entity.bo.declare;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Entity - DecOrder
 * 
 * 
 * 
 */
@Data
public class DecOrder{

	
	/**
	 * 订单状态
	 * 
	 *
	 */
	public enum OrderStatuses{
		/** 未申报 */
		undeclared,
		/** 申报中 */
		declaration,
		/** 海关退单 */
		hsPush,
		/** 人工审核 */
		manualAudit,
		/** 待运抵 */
		transporting,
		/** 查验 */
		check,
		/** 放行 */
		release,
		/** 放行未退税 */
		unTaxRebates,
		/** 完结 */
		end,
		/** 异常订单 */
		abnormal,
		/** 撤单中 */
		cancelling,
		/** 已撤单 */
		cancellation
	}
	
	/**
	 * 仓库状态
	 *
	 *
	 */
	public enum WmsStatuses{
		/** 未发送 */
		unSend,
		/** 发送成功 */
		sendSuccess,
		/** 发送失败 */
		sendFail,
		/** 已撤单 */
		cancellation,
	}

	/** 回调状态 */
	public enum NotifyType{
		/** 清关成功回调 */
		clearSuccessCallBack,
		/** 海关查验回调*/
		hsCheckCallBack,
		/** 缺少支付单*/
		lackOfPayment,
		/** 订单货款,税款,运费不等订单金额*/
		missMatchOrderAmount,
		/** 支付单支付金额与订单支付金额不一致*/
		missMatchPayAndOrderAmount,
		/** 人工退单,收货地址不详*/
		manualWithdrawal,
		/** 订单购买人与支付单支付人名字或身份证号码不一致*/
		missMatchPersonalInformation,
		/** 公安身份认证不通过*/
		authenticationNotPass
	}
	
	/** 系统唯一编码 */
	private String guid;
	
	/** 电子订单类型 */
	private String orderType;
	
	/** 订单编号 */
	private String orderNo;
	
	/** 企业内部编号 */
	private String copNo;
	
	/** 电商平台代码 */
	private String ebpCode;
	
	/** 电商平台代码 */
	private String ebpName;
	
	/** 电商企业代码 */
	private String ebcCode;
	
	/** 电商企业名称 */
	private String ebcName;
	
	/** 关区代码 */
	private String customsCode;
	
	/** 分拣线标识 */
	private String sortlineId;
	
	/** 物流企业海关十位编码 */
	private String logisticsCode;
	
	/** 物流单号 */
	private String wayBill;
	
	/** 业务类型 */
	private String bizTypeCode;
	
	/** 仓库编码 */
	private String houseCode;
	
	/** 商品价格 */
	private BigDecimal goodsValue;
	
	/** 运杂费 */
	private BigDecimal freight;
	
	/** 非现金抵扣金额 */
	private BigDecimal discount;
	
	/** 代扣税款 */
	private BigDecimal taxTotal;
	
	/** 实际支付金额 */
	private BigDecimal acturalPaid;
	
	/** 币制 */
	private String currency;
	
	/** 订购人注册号 */
	private String buyerRegNo;
	
	/** 订购人姓名 */
	private String buyerName;
	
	/** 订购人证件类型 */
	private String buyerIdType;
	
	/** 订购人证件号码 */
	private String buyerIdNumber;
	
	/** 收货人姓名 */
	private String consignee;
	
	/** 收货人电话 */
	private String consigneeTelephone;
	
	/** 收货地址 */
	private String consigneeAddress;
	
	/** 支付企业代码 */
	private String payCode;
	
	/** 支付企业名称 */
	private String payName;
	
	/** 支付企业唯一的支付流水号 */
	private String payTransactionId;
	
	/** 支付单流水号 */
	private String serialNumber;
	
	/** 是否推送支付单 */
	private String isPushPay;
	
	/** 清单编号 */
	private String invtNo;
	
	/** 预录入编号 */
	private String preNo;
	
	/** 订单状态(0-未申报,1-申报中,2-海关退单,3-人工审核,4-待运抵,5-查验,6-放行,7-放行未退税,8-完结,9-人工分拣,10-异常处理) */
	private Integer orderStatus;
	
	/** 京东清关状态(0-清关中,1-清关成功2-清关失败,3-京东推单) */
	private Integer jdStatus;
	
	/** 仓库状态(0-未发送,1-发送成功,2-发送失败) */
	private Integer wmsStatus;
	
	/** 备注 */
	private String note;
}