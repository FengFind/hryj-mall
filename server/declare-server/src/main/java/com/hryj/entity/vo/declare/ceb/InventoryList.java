package com.hryj.entity.vo.declare.ceb;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

/******************
 * 
 * 
 * 进口清单表体
 * 
 * @author BF
 *
 */
@XmlRootElement(name = "InventoryList",namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class InventoryList {

	@XmlElement(name="gnum",namespace="http://www.chinaport.gov.cn/ceb")
	private Integer gnum = 1;// 序号 N4 是 从1开始连续序号（一一对应关联电子订单）
	
	@XmlElement(name="itemRecordNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String itemRecordNo = "";// 账册备案料号 C..30 否 1210必填
	
	@XmlElement(name="itemNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String itemNo = "";// 企业商品货号 C..30 否 电商平台自定义的商品货号（SKU）
	
	@XmlElement(name="itemName",namespace="http://www.chinaport.gov.cn/ceb")
	private String itemName = "";// 企业商品品名 C..250 否 电商平台的商品品名
	
	@XmlElement(name="gcode",namespace="http://www.chinaport.gov.cn/ceb")
	private String gcode = "";// 商品编码 C10 是海关对进出口货物规定的类别标识代码，采用海关综合分类表的标准分类，总长度为18位数字代码，前8位由国务院关税税则委员会确定，后2 位由海关根据代征税、暂定税率和贸易管制的需要增设的
	
	@XmlElement(name="gname",namespace="http://www.chinaport.gov.cn/ceb")
	private String gname="";// 商品名称 C..250 是 同一类商品的名称。任何一种具体商品可以并只能归入表中的一个条目
	
	@XmlElement(name="gmodel",namespace="http://www.chinaport.gov.cn/ceb")
	private String gmodel="";// 商品规格型号 C..250 是满足海关归类、审价以及监管的要求为准。包括：品名、牌名、规格、型号、成份、含量、等级等
	
	@XmlElement(name="barCode",namespace="http://www.chinaport.gov.cn/ceb")
	private String barCode="";// 条形码 C..50 否商品条形码一般由前缀部分、制造厂商代码、商品代码和校验码组成。没有条形码填“无”
	
	@XmlElement(name="country",namespace="http://www.chinaport.gov.cn/ceb")
	private String country="";// 原产国（地区） C3 是 海关标准的参数代码 《JGS-20 海关业务代码集》国家（地区）代码表填写代码。

	@XmlElement(name="tradeCountry",namespace="http://www.chinaport.gov.cn/ceb")
	private String tradeCountry ="";// 贸易国（地区） C3	否	按海关规定的《国别（地区）代码表》选择填报相应的贸易国（地区）代码

	@XmlElement(name="currency",namespace="http://www.chinaport.gov.cn/ceb")
	private String currency="";// 币制 C3 是 海关标准的参数代码 《JGS-20 海关业务代码集》- 货币代码
	
	@XmlElement(name="qty",namespace="http://www.chinaport.gov.cn/ceb")
	private Integer qty=0;// 数量 N19,5 是
	
	@XmlElement(name="unit",namespace="http://www.chinaport.gov.cn/ceb")
	private String unit="";// 计量单位 C3 是 海关标准的参数代码 《JGS-20 海关业务代码集》- 计量单位代码
	
	@XmlElement(name="qty1",namespace="http://www.chinaport.gov.cn/ceb")
	private BigDecimal qty1;// 法定数量 N19,5 是
	
	@XmlElement(name="unit1",namespace="http://www.chinaport.gov.cn/ceb")
	private String unit1="";// 法定计量单位 C3 是 海关标准的参数代码 《JGS-20 海关业务代码集》- 计量单位代码
	
	@XmlElement(name="qty2",namespace="http://www.chinaport.gov.cn/ceb")
	private BigDecimal qty2;// 第二数量 N19,5 否
	
	@XmlElement(name="unit2",namespace="http://www.chinaport.gov.cn/ceb")
	private String unit2="";// 第二计量单位 C3 否 海关标准的参数代码 《JGS-20 海关业务代码集》- 计量单位代码
	
	@XmlElement(name="price",namespace="http://www.chinaport.gov.cn/ceb")
	private BigDecimal price;// 单价 N19,5 是 成交单价
	
	@XmlElement(name="totalPrice",namespace="http://www.chinaport.gov.cn/ceb")
	private BigDecimal totalPrice;// 总价 N19,5 是
	
	@XmlElement(name="note",namespace="http://www.chinaport.gov.cn/ceb")
	private String note="";// 备注 C..1000 否
	
	@XmlElement(name="goodsRoughWeight",namespace="http://www.chinaport.gov.cn/ceb")
	private String goodsRoughWeight;//
	
	@XmlElement(name="websiteHref",namespace="http://www.chinaport.gov.cn/ceb")
	private String websiteHref;//

	
}
