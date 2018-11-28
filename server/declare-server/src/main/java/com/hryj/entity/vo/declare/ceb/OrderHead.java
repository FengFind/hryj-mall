package com.hryj.entity.vo.declare.ceb;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

/***
 * 电子订单表头
 * 
 * @author BF
 *
 */
@XmlRootElement(namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class OrderHead {
	
	/** 系统唯一序号 、 C36 、 是 、 企业系统生成36位唯一序号（英文字母大写） */
	@XmlElement(name = "guid",namespace="http://www.chinaport.gov.cn/ceb")
	private String guid= "";
	
	/** 申报类型 、 C1 、 是 、 申报类型:1-新增 2-变更 3-删除,默认为1 */
	@XmlElement(name = "appType",namespace="http://www.chinaport.gov.cn/ceb")
	private String appType= "";
	
	/** 申报时间 、 C14 、 是 、 申报时间以海关入库反馈时间为准,:格式:YYYYMMDDhhmmss*/
	@XmlElement(name = "appTime",namespace="http://www.chinaport.gov.cn/ceb")
	private String appTime= "";
	
	/** 业务状态 、 C..3 、 是 、 业务状态:1-暂存,2-申报,默认为2*/
	@XmlElement(name = "appStatus",namespace="http://www.chinaport.gov.cn/ceb")
	private String appStatus= "";
	
	/** 订单类型 、 C1 、 是 、 电商平台的订单类型 I-进口商品订单；E-出口商品订单*/
	@XmlElement(name = "orderType",namespace="http://www.chinaport.gov.cn/ceb")
	private String orderType= "";
	
	/** 订单编号 、 C..60 、 是 、 原始交易平台的原始订单编号 */
	@XmlElement(name = "orderNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String orderNo= "";
	
	/**电商平台代码 、 C18 、 是 、 电商平台的海关备案编码（18位）*/
	@XmlElement(name = "ebpCode",namespace="http://www.chinaport.gov.cn/ceb")
	private String ebpCode= "";
	
	/** 电商平台名称 、 C..100 、 是 、 电商平台的海关备案名称（电子口岸校验名称）*/
	@XmlElement(name = "ebpName",namespace="http://www.chinaport.gov.cn/ceb")
	private String ebpName= "";
	
	/**电商企业代码 、 C18 、 是 、 电商企业的海关备案编码(18位)*/
	@XmlElement(name = "ebcCode",namespace="http://www.chinaport.gov.cn/ceb")
	private String ebcCode= "";
	
	/** 电商企业名称 、 C..100 、 是 、 电商企业的海关备案名称（电子口岸校验名称）**/
	@XmlElement(name = "ebcName",namespace="http://www.chinaport.gov.cn/ceb")
	private String ebcName= "";
	
	/** 关区代码 */
	private String customsCode;
	
	/** 分拣线标识 */
	private String sortlineId;
	
	/** 物流公司编码 */
	private String logisticscode;
	
	/** 物流单号 */
	private String wayBill;
	
	/** 货款金额 、 N19,5 、 是 、 商品实际成交价，不包括优惠减免 **/
	@XmlElement(name = "goodsValue",namespace="http://www.chinaport.gov.cn/ceb")
	private BigDecimal goodsValue = BigDecimal.ZERO;
	
	/** 运杂费 、 N19,5 、 是 、 无则填写"0" **/
	@XmlElement(name = "freight",namespace="http://www.chinaport.gov.cn/ceb")
	private BigDecimal freight = BigDecimal.ZERO;
	
	/** 优惠减免金额 、 N19,5 、 是 、 无则填写"0"*/
	@XmlElement(name = "discount",namespace="http://www.chinaport.gov.cn/ceb")
	private BigDecimal discount= BigDecimal.ZERO;
	
	/** 订单商品税款 、 N19,5 、 是 、 按照货款金额计算的税款*/
	@XmlElement(name = "taxTotal",namespace="http://www.chinaport.gov.cn/ceb")
	private BigDecimal taxTotal= BigDecimal.ZERO;

	/** 实际支付金额 、 N19,5 、 是 、  货款+运费+税款-优惠金额，与支付保持一致（精确到元）*/
	@XmlElement(name = "acturalPaid",namespace="http://www.chinaport.gov.cn/ceb")
	private BigDecimal acturalPaid= BigDecimal.ZERO;
	
	/** 币制 、 C3 、 是 、 海关标准的参数代码 《JGS-20 海关业务代码集》- 货币代码*/
	@XmlElement(name = "currency",namespace="http://www.chinaport.gov.cn/ceb")
	private String currency= "";
	
	/** 订购人注册号 、 C..60 、 是 、  订购人在交易平台唯一注册号，后续大数据分析使用，一个平台注册号对应一个身份证*/
	@XmlElement(name = "buyerRegNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String buyerRegNo= "";
	
	/** 订购人姓名 、 C..60 、 是 、 海关监管的对象，需要对个人消费额度和实名认证进行管控**/
	@XmlElement(name = "buyerName",namespace="http://www.chinaport.gov.cn/ceb")
	private String buyerName= "";
	
	/** 订购人证件类型 、 C1 、 是 、 1-身份证,2-其它 **/
	@XmlElement(name = "buyerIdType",namespace="http://www.chinaport.gov.cn/ceb")
	private String buyerIdType= "1";
	
	/** 订购人证件号码 、 C..60 、 是 、 默认为身份证号*/
	@XmlElement(name = "buyerIdNumber",namespace="http://www.chinaport.gov.cn/ceb")
	private String buyerIdNumber= "";

	/** 订购人电话.C..30	是	海关监管对象的电话，要求实际联系电话*/
	@XmlElement(name = "buyerTelephone",namespace="http://www.chinaport.gov.cn/ceb")
	private String buyerTelephone= "";

	/** 支付企业代码 、 C18 、 否 、 支付企业需在JC2006注册备案*/
	@XmlElement(name = "payCode",namespace="http://www.chinaport.gov.cn/ceb")
	private String payCode= "";
	
	/** 支付企业名称 、 C..100 、 否 、*/
	@XmlElement(name = "payName",namespace="http://www.chinaport.gov.cn/ceb")
	private String payName= "";
	
	/** 支付交易编号 、 C..60 、 否 、 支付企业唯一的支付流水号*/
	@XmlElement(name = "payTransactionId",namespace="http://www.chinaport.gov.cn/ceb")
	private String payTransactionId= "";
	
	/** 商品批次号 、 C..100 、 否 、*/
	@XmlElement(name = "batchNumbers",namespace="http://www.chinaport.gov.cn/ceb")
	private String batchNumbers= "";
	
	/** 收货人姓名 、 C..100 、 是 、 收货人信息，同运单*/
	@XmlElement(name = "consignee",namespace="http://www.chinaport.gov.cn/ceb")
	private String consignee= "";
	
	/** 收货人电话 、 C..50 、 是 、 收货人信息，同运单 */
	@XmlElement(name = "consigneeTelephone",namespace="http://www.chinaport.gov.cn/ceb")
	private String consigneeTelephone= "";
	
	/** 收货人地址 、 C..200 、 是 、 收货人信息，同运单*/
	@XmlElement(name = "consigneeAddress",namespace="http://www.chinaport.gov.cn/ceb")
	private String consigneeAddress= "";
	
	/** 收货人行政区划代码 、 C6 、 否 、 国家行政区划标准*/
	@XmlElement(name = "consigneeDitrict",namespace="http://www.chinaport.gov.cn/ceb")
	private String consigneeDitrict= "";
	
	/** 备注 、 C..1000 、 否 、*/
	@XmlElement(name = "note",namespace="http://www.chinaport.gov.cn/ceb")
	private String note= "";
}
