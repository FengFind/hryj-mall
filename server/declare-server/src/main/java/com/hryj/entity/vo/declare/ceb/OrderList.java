package com.hryj.entity.vo.declare.ceb;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

/**
 * 电子订单商品表体
 * 
 * @author BF
 *
 */
@XmlRootElement(namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class OrderList {
	
	/** 商品序号 、 N4 、 是 、 从1开始的递增序号（关联对应清单商品项）*/
	@XmlElement(name = "gnum",namespace="http://www.chinaport.gov.cn/ceb")
	private Integer gnum= 0;
	
	/** 企业商品货号 、 C..30 、 否 、 电商平台自定义的商品货号（SKU）**/
	@XmlElement(name = "itemNo",namespace="http://www.chinaport.gov.cn/ceb")
	private String itemNo= "";
	
	/** 企业商品名称 、 C..250 、 是 、 电商平台上架的商品名称**/
	@XmlElement(name = "itemName",namespace="http://www.chinaport.gov.cn/ceb")
	private String itemName= "";
	
	/**企业商品描述 、 C..1000 、 否 、 电商平台上架的商品描述宣传信息*/
	@XmlElement(name = "itemDescribe",namespace="http://www.chinaport.gov.cn/ceb")
	private String itemDescribe= "";
	
	/** 条形码 、 C..50 、 否 、 商品条形码一般由前缀部分、制造厂商代码、商品代码和校验码组成。*/
	@XmlElement(name = "barCode",namespace="http://www.chinaport.gov.cn/ceb")
	private String barCode= "";
	
	/** 单位 、 C3 、 是 、 海关标准的参数代码 海关标准的参数代码 《JGS-20海关业务代码集》-计量单位代码*/
	@XmlElement(name = "unit",namespace="http://www.chinaport.gov.cn/ceb")
	private String unit= "";
	
	/** 数量 、 N19,5 、 是 、*/
	@XmlElement(name = "qty",namespace="http://www.chinaport.gov.cn/ceb")
	private Integer qty= 0;
	
	/** 单价 、 N19,5 、 是 、*/
	@XmlElement(name = "price",namespace="http://www.chinaport.gov.cn/ceb")
	private BigDecimal price= BigDecimal.ZERO;
	
	/** 总价 、 N19,5 、 是 、*/
	@XmlElement(name = "totalPrice",namespace="http://www.chinaport.gov.cn/ceb")
	private BigDecimal totalPrice= BigDecimal.ZERO;
	
	/** 币制 、 C3 、 是 、 海关标准的参数代码 海关标准的参数代码《JGS-20海关业务代码集》- 货币代码*/
	@XmlElement(name = "currency",namespace="http://www.chinaport.gov.cn/ceb")
	private String currency= "";
	
	/**原产国 、 C3 、 是 、*/
	@XmlElement(name = "country",namespace="http://www.chinaport.gov.cn/ceb")
	private String country= "";

	/** 备注 、 C..1000 、 否 、*/
	@XmlElement(name = "note",namespace="http://www.chinaport.gov.cn/ceb")
	private String note= "";

	/** 规格型号 、 C..510 、 是 、*/
	@XmlElement(name = "gmodel",namespace="http://www.chinaport.gov.cn/ceb")
	private String gmodel= "";

}
