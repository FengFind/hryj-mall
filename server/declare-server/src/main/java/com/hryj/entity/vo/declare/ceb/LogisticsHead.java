package com.hryj.entity.vo.declare.ceb;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/*********************************
 * 
 * 物流报文头
 * 
 * @author BF
 *
 **********************************/
@XmlRootElement(name = "LogisticsHead",namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class LogisticsHead {
	
	@XmlElement(name = "guid",namespace="http://www.chinaport.gov.cn/ceb")
	private String guid = "";// 系统唯一序号 C36 是 企业系统生成36位唯一序号（英文字母大写）
	
	@XmlElement(name = "appType",namespace="http://www.chinaport.gov.cn/ceb")
	private String appType = "";// 申报类型 C1 是 申报类型:1-新增 2-变更 3-删除,默认为1
	
	@XmlElement(name = "appTime",namespace="http://www.chinaport.gov.cn/ceb")
	private String appTime = "";// 申报时间 C14 是 申报时间以海关入库反馈时间为准,:格式:YYYYMMDDhhmmss
	
	@XmlElement(name = "appStatus",namespace="http://www.chinaport.gov.cn/ceb")
	private String appStatus = "";// 业务状态 C..3 是 业务状态:1-暂存,2-申报,默认为2
	
	@XmlElement(name = "logisticsCode",namespace="http://www.chinaport.gov.cn/ceb")
	private String logisticsCode = "";// 物流企业代码 C18 是 物流企业的海关备案编码（18位）
	
	@XmlElement(name = "logisticsName",namespace="http://www.chinaport.gov.cn/ceb")
	private String logisticsName = "";// 物流企业名称 C..100 是 物流企业的海关备案名称
	
	@XmlElement(name = "logisticsNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String logisticsNo = "";// 物流运单编号 C..60 是 物流企业的运单包裹面单号

	@XmlElement(name = "orderNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String orderNo = "";// 订单编号C..60 是 交易平台的订单编号，同一交易平台的订单编号应唯一。订单编号长度不能超过60位。

	@XmlElement(name = "billNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String billNo = "";// 提运单号 C..37 否 直购进口为海运提单或空运总单
	
	@XmlElement(name = "freight",namespace="http://www.chinaport.gov.cn/ceb")
	private Double freight = 0d;// 运费 N19,5 否 货物运输费用
	
	@XmlElement(name = "insuredFee",namespace="http://www.chinaport.gov.cn/ceb")
	private Double insuredFee = 0d;// 保价费 N19,5 否 货物保险费用
	
	@XmlElement(name = "currency",namespace="http://www.chinaport.gov.cn/ceb")
	private String currency = "";// 币制 C3 否 海关标准的参数代码 《JGS-20 海关业务代码集》- 货币代码
	
	@XmlElement(name = "weight",namespace="http://www.chinaport.gov.cn/ceb")
	private Double weight = 0d;// 毛重 N19,5 是 单位为千克
	
	@XmlElement(name = "packNo",namespace="http://www.chinaport.gov.cn/ceb")
	private Integer packNo = 0;// 件数 N9 是 单个运单下包裹数
	
	@XmlElement(name = "goodsInfo",namespace="http://www.chinaport.gov.cn/ceb")
	private String goodsInfo = "";// 主要货物信息 C..200 否 物流企业验视的商品信息
	
	@XmlElement(name = "consignee",namespace="http://www.chinaport.gov.cn/ceb")
	private String consingee = "";// 收货人姓名 C..100 是
	
	@XmlElement(name = "consigneeAddress",namespace="http://www.chinaport.gov.cn/ceb")
	private String consigneeAddress = "";// 收货人地址 C..200 是
	
	@XmlElement(name = "consigneeTelephone",namespace="http://www.chinaport.gov.cn/ceb")
	private String consigneeTelephone = "";// 收货人电话 C..50 是
	
	@XmlElement(name = "note",namespace="http://www.chinaport.gov.cn/ceb")
	private String note = "";// 备注 C..1000 否

}
