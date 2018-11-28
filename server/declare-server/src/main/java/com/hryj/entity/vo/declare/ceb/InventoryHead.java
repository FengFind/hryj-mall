package com.hryj.entity.vo.declare.ceb;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@XmlRootElement(name="InventoryHead",namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class InventoryHead {
	
	@XmlElement(name="guid",namespace="http://www.chinaport.gov.cn/ceb")
	private String guid = "";// 系统唯一序号 C36 是 企业系统生成36位唯一序号（英文字母大写）
	
	@XmlElement(name="appType",namespace="http://www.chinaport.gov.cn/ceb")
	private String appType = "";// 申报类型 C1 是 申报类型:1-新增 2-变更 3-删除,默认为1
	
	@XmlElement(name="appTime",namespace="http://www.chinaport.gov.cn/ceb")
	private String appTime = "";// 申报时间 C14 是 申报时间以海关入库反馈时间为准,:格式:YYYYMMDDhhmmss
	
	@XmlElement(name="appStatus",namespace="http://www.chinaport.gov.cn/ceb")
	private String appStatus = "";// 业务状态 C..3 是 业务状态:1-暂存,2-申报,默认为2
	
	@XmlElement(name="orderNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String orderNo = "";// 订单编号 C..60 是 电商平台的原始订单编号
	
	@XmlElement(name="ebpCode",namespace="http://www.chinaport.gov.cn/ceb")
	private String ebpCode = "";// 电商平台代码 C18 是 电商平台的海关备案编码（18位）
	
	@XmlElement(name="ebpName",namespace="http://www.chinaport.gov.cn/ceb")
	private String ebpName = "";// 电商平台名称 C..100 是 电商平台的海关备案名称
	
	@XmlElement(name="ebcCode",namespace="http://www.chinaport.gov.cn/ceb")
	private String ebcCode = "";// 电商企业代码 C18 是 电商企业的海关备案编码(18位)
	
	@XmlElement(name="ebcName",namespace="http://www.chinaport.gov.cn/ceb")
	private String ebcName = "";// 电商企业名称 C..100 是 电商企业的海关备案名称
	
	@XmlElement(name="logisticsNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String logisticsNo = "";// 物流运单编号 C..60 是 物流企业的运单包裹面单号
	
	@XmlElement(name="logisticsCode",namespace="http://www.chinaport.gov.cn/ceb")
	private String logisticsCode = "";// 物流企业代码 C18 是 物流企业的海关备案编码（18位）
	
	@XmlElement(name="logisticsName",namespace="http://www.chinaport.gov.cn/ceb")
	private String logisticsName = "";// 物流企业名称 C..100 是 物流企业的海关备案名称
	
	@XmlElement(name="copNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String copNo = "";// 企业内部编号 C..20 否 企业内部的清单编号
	
	@XmlElement(name="preNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String preNo = "";// 预录入编号 C18 否 电子口岸的清单编号（B+8位年月日+9位流水号）
	
	@XmlElement(name="assureCode",namespace="http://www.chinaport.gov.cn/ceb")
	private String assureCode = "";// 担保编号 C..30 否 指定税款担保账号或专用缴税账号，不同前缀区分。
	
	@XmlElement(name="emsNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String emsNo = "";// 账册编号 C..30 否 保税模式填写具体账号，用于保税进口业务在特殊区域辅助系统记账（二线出区核减）
	
	@XmlElement(name="invtNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String invtNo = "";// 清单编号 C18 否 海关审结的清单编号（4位关区+4位年+1位进出口标记+9位流水号），
	
	@XmlElement(name="ieFlag",namespace="http://www.chinaport.gov.cn/ceb")
	private String ieFlag = "";// 进出口标记 C1 是 I-进口,E-出口
	
	@XmlElement(name="declTime",namespace="http://www.chinaport.gov.cn/ceb")
	private String declTime = "";// 申报日期 C8 是 申报时间以海关入库反馈时间为准,:格式:YYYYMMDD
	
	@XmlElement(name="customsCode",namespace="http://www.chinaport.gov.cn/ceb")
	private String customsCode = "";// 申报海关代码 C4 是
	
	@XmlElement(name="portCode",namespace="http://www.chinaport.gov.cn/ceb")
	private String portCode = "";// 口岸海关代码 C4 是 商品实际出我国关境口岸海关的关区代码 JGS/T 18《海关关区代码》
	
	@XmlElement(name="ieDate",namespace="http://www.chinaport.gov.cn/ceb")
	private String ieDate = "";// 进口日期 C8 是 时间格式:YYYYMMDD
	
	@XmlElement(name="buyerIdType",namespace="http://www.chinaport.gov.cn/ceb")
	private String buyerIdType = "";// 订购人证件类型 C1 是 1-身份证；2-其它
	
	@XmlElement(name="buyerIdNumber",namespace="http://www.chinaport.gov.cn/ceb")
	private String buyerIdNumber = "";// 订购人证件号码 C..60 是 海关监控对象的身份证号
	
	@XmlElement(name="buyerName",namespace="http://www.chinaport.gov.cn/ceb")
	private String buyerName = "";// 订购人姓名 C..60 是 海关监控对象的姓名
	
	@XmlElement(name="buyerTelephone",namespace="http://www.chinaport.gov.cn/ceb")
	private String buyerTelephone = "";// 订购人电话 C..30 是 海关监管对象的电话
	
	@XmlElement(name="consigneeAddress",namespace="http://www.chinaport.gov.cn/ceb")
	private String consigneeAddress = "";// 收件人地址 C..200 是 收件人的地址
	
	@XmlElement(name="agentCode",namespace="http://www.chinaport.gov.cn/ceb")
	private String agentCode = "";// 申报企业代码 C18 是 申报单位的海关备案代码(18位)
	
	@XmlElement(name="agentName",namespace="http://www.chinaport.gov.cn/ceb")
	private String agentName = "";// 申报企业名称 C..100 是 申报单位的海关备案名称
	
	@XmlElement(name="areaCode",namespace="http://www.chinaport.gov.cn/ceb")
	private String areaCode = "";// 区内企业代码 C18 否 保税业务区内仓储企业代码，应各地保税进出口和杭州建议，增加区内企业核扣账册
	
	@XmlElement(name="areaName",namespace="http://www.chinaport.gov.cn/ceb")
	private String areaName = "";// 区内企业名称 C..100 否 保税业务区内仓储企业名称
	
	@XmlElement(name="tradeMode",namespace="http://www.chinaport.gov.cn/ceb")
	private String tradeMode = "";// 贸易方式 C4 是 默认为9610/1210,区分保税或一般模式。
	
	@XmlElement(name="trafMode",namespace="http://www.chinaport.gov.cn/ceb")
	private String trafMode = "";// 运输方式 C1 是 海关标准的参数代码 《JGS-20 海关业务代码集》-运输方式代码。直购指跨境段物流运输方式。网购保税按二线出区
	
	@XmlElement(name="trafNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String trafNo = "";// 运输工具编号 C..100 否直购进口必填。货物进出境的运输工具的名称或运输工具编号。填报内容应与运输部门向海关申报的载货清单所列相应内容一致；同报关单填制规范。
	
	@XmlElement(name="voyageNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String voyageNo = "";// 航班航次号 C..32 否 直购进口必填。货物进出境的运输工具的航次编号
	
	@XmlElement(name="billNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String billNo = "";// 提运单号 C..37 否 直购进口必填。货物提单或运单的编号，直购必填
	
	@XmlElement(name="loctNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String loctNo = "";// 监管场所代码 C..10 否 针对同一申报地海关下有多个跨境电子商务的监管场所,需要填写区分
	
	@XmlElement(name="licenseNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String licenseNo = "";// 许可证号 C..19 否 商务主管部门及其授权发证机关签发的进出口货物许可证的编号
	
	@XmlElement(name="country",namespace="http://www.chinaport.gov.cn/ceb")
	private String country = "";// 启运国（地区） C3 是 直购模式填写
	
	@XmlElement(name="freight",namespace="http://www.chinaport.gov.cn/ceb")
	private BigDecimal freight = BigDecimal.ZERO;// 运费 N19,5 是 物流企业实际收取的运输费用
	
	@XmlElement(name="insuredFee",namespace="http://www.chinaport.gov.cn/ceb")
	private BigDecimal insuredFee = BigDecimal.ZERO;// 保费 N19,5 是 物流企业实际收取的商品保价费用
	
	@XmlElement(name="currency",namespace="http://www.chinaport.gov.cn/ceb")
	private String currency = "";// 币制 C3 是 海关标准的参数代码 海关标准的参数代码 《JGS-20 海关业务代码集》-货币代码
	
	@XmlElement(name="wrapType",namespace="http://www.chinaport.gov.cn/ceb")
	private String wrapType = "";// 包装种类代码 C1 否 海关对进出口货物实际采用的外部包装方式的标识代码，采用1位数字表示，如：木箱、纸箱、桶装、散装、托盘、包、油罐车等
	
	@XmlElement(name="packNo",namespace="http://www.chinaport.gov.cn/ceb")
	private Integer packNo = 0;// 件数 N9 是 件数(包裹数量)
	
	@XmlElement(name="grossWeight",namespace="http://www.chinaport.gov.cn/ceb")
	private BigDecimal grossWeight;// 毛重（公斤） N19,5 是 货物及其包装材料的重量之和，计量单位为千克
	
	@XmlElement(name="netWeight",namespace="http://www.chinaport.gov.cn/ceb")
	private BigDecimal netWeight;// 净重（公斤） N19,5 是货物的毛重减去外包装材料后的重量，即货物本身的实际重量，计量单位为千克
	
	@XmlElement(name="note",namespace="http://www.chinaport.gov.cn/ceb")
	private String note = "";// 备注 C..1000 否
	
	@XmlElement(name="sortlineId",namespace="http://www.chinaport.gov.cn/ceb")
	private String sortlineId = "";//分拣线ID；分拣线标识：SORTLINE01：代表寸滩空港；SORTLINE02：代表重庆西永；SORTLINE03：代表寸滩水港；SORTLINE04：代表邮政EMS；SORTLINE05：代表潍坊分拣线01；SORTLINE06：代表南彭保仓分拣线
	//机构代码
	@XmlElement(name="orgCode",namespace="http://www.chinaport.gov.cn/ceb")
	private String orgCode;

}
