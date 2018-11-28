package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/***
 * 
 * 商品信息头
 * 
 * @author YUANLIU
 *
 */
@XmlRootElement(name="SkuHead",namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
public class SkuHead {
	
	@XmlElement(name = "ESHOP_ENT_CODE",namespace="http://www.chinaport.gov.cn/ceb")
	private String eshopEntCode = "";//企业编码
	
	@XmlElement(name = "ESHOP_ENT_NAME",namespace="http://www.chinaport.gov.cn/ceb")
	private String eshopEntName = "";//企业名称
	
	@XmlElement(name = "EXTERNAL_NO",namespace="http://www.chinaport.gov.cn/ceb")
	private String externalNo = "";//料号
	
	@XmlElement(name = "SKU",namespace="http://www.chinaport.gov.cn/ceb")
	private String sku = "";//商品编码
	
	@XmlElement(name = "CIQ_CODE",namespace="http://www.chinaport.gov.cn/ceb")
	private String ciqCode = "";//商检编码
	
	@XmlElement(name = "HS_CODE",namespace="http://www.chinaport.gov.cn/ceb")
	private String hsCode = "";//HS编码
	
	@XmlElement(name = "GOODS_NAME",namespace="http://www.chinaport.gov.cn/ceb")
	private String goodsName = "";//商品名称
	
	@XmlElement(name = "BIZ_TYPE",namespace="http://www.chinaport.gov.cn/ceb")
	private String bizType = "";//业务类型
	
	@XmlElement(name = "GOODS_SPEC",namespace="http://www.chinaport.gov.cn/ceb")
	private String goodsSpec = "";//商品规格
	
	@XmlElement(name = "DECLARE_UNIT",namespace="http://www.chinaport.gov.cn/ceb")
	private String declareUnit = "";//申报单位
	
	@XmlElement(name = "LEGAL_UNIT",namespace="http://www.chinaport.gov.cn/ceb")
	private String legalUnit = "";//法定单位
	
	@XmlElement(name = "CONV_LEGAL_UNIT_NUM",namespace="http://www.chinaport.gov.cn/ceb")
	private String convLegalUntiNum = "";//法定折算数量
	
	@XmlElement(name = "IN_AREA_UNIT",namespace="http://www.chinaport.gov.cn/ceb")
	private String inAreaUnit = "";//入区单位
	
	@XmlElement(name = "CONV_IN_AREA_UNIT_NUM",namespace="http://www.chinaport.gov.cn/ceb")
	private String convInAreaUnitNum = "";//入区折算数量
	
	@XmlElement(name = "ORIGIN_COUNTRY_CODE",namespace="http://www.chinaport.gov.cn/ceb")
	private String originCountryCode = "";//原产国编码
	
	@XmlElement(name = "SUPPLIER_NAME",namespace="http://www.chinaport.gov.cn/ceb")
	private String supplierName = "";//供货商名称
	
	@XmlElement(name = "PRODUCER_NAME",namespace="http://www.chinaport.gov.cn/ceb")
	private String producerName = "";//生产企业名称
	
	@XmlElement(name = "IS_CNCA_POR",namespace="http://www.chinaport.gov.cn/ceb")
	private String isCncaPor = "";//国外生产企业是否在中国注册备案
	
	@XmlElement(name = "BRAND",namespace="http://www.chinaport.gov.cn/ceb")
	private String brand = "";//品牌
	
	@XmlElement(name = "IS_LAW_REVIEW",namespace="http://www.chinaport.gov.cn/ceb")
	private String isLawReview = "";//合法证明
	
	@XmlElement(name = "PER_INDEX",namespace="http://www.chinaport.gov.cn/ceb")
	private String perIndex = "";//
	
	@XmlElement(name = "AUTH_INFO",namespace="http://www.chinaport.gov.cn/ceb")
	private String authInfo = "";//授权信息
	
	@XmlElement(name = "LICENSE_CODE",namespace="http://www.chinaport.gov.cn/ceb")
	private String licenseCode = "";//许可证编码
	
	@XmlElement(name = "BAR_CODE",namespace="http://www.chinaport.gov.cn/ceb")
	private String barCode = "";//海关编码
	
	@XmlElement(name = "CHECK_ORG_CODE",namespace="http://www.chinaport.gov.cn/ceb")
	private String checkOrgCode = "";//施检机构代码
	
	@XmlElement(name = "VALIDITY",namespace="http://www.chinaport.gov.cn/ceb")
	private String validity = "";//
	
	@XmlElement(name = "TRADE_MODE",namespace="http://www.chinaport.gov.cn/ceb")
	private String tradeMode = "";//贸易方式
	
	@XmlElement(name = "PURPOSE",namespace="http://www.chinaport.gov.cn/ceb")
	private String purpose = "";//
	
	@XmlElement(name = "STANDARD",namespace="http://www.chinaport.gov.cn/ceb")
	private String standard = "";//标准
	
	@XmlElement(name = "REMARK",namespace="http://www.chinaport.gov.cn/ceb")
	private String remark = "";//备注

	public String getEshopEntCode() {
		return eshopEntCode;
	}

	public void setEshopEntCode(String eshopEntCode) {
		this.eshopEntCode = eshopEntCode;
	}

	public String getEshopEntName() {
		return eshopEntName;
	}

	public void setEshopEntName(String eshopEntName) {
		this.eshopEntName = eshopEntName;
	}

	public String getExternalNo() {
		return externalNo;
	}

	public void setExternalNo(String externalNo) {
		this.externalNo = externalNo;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getCiqCode() {
		return ciqCode;
	}

	public void setCiqCode(String ciqCode) {
		this.ciqCode = ciqCode;
	}

	public String getHsCode() {
		return hsCode;
	}

	public void setHsCode(String hsCode) {
		this.hsCode = hsCode;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getBizType() {
		return bizType;
	}

	public void setBizType(String bizType) {
		this.bizType = bizType;
	}

	public String getGoodsSpec() {
		return goodsSpec;
	}

	public void setGoodsSpec(String goodsSpec) {
		this.goodsSpec = goodsSpec;
	}

	public String getDeclareUnit() {
		return declareUnit;
	}

	public void setDeclareUnit(String declareUnit) {
		this.declareUnit = declareUnit;
	}

	public String getLegalUnit() {
		return legalUnit;
	}

	public void setLegalUnit(String legalUnit) {
		this.legalUnit = legalUnit;
	}

	public String getConvLegalUntiNum() {
		return convLegalUntiNum;
	}

	public void setConvLegalUntiNum(String convLegalUntiNum) {
		this.convLegalUntiNum = convLegalUntiNum;
	}

	public String getInAreaUnit() {
		return inAreaUnit;
	}

	public void setInAreaUnit(String inAreaUnit) {
		this.inAreaUnit = inAreaUnit;
	}

	public String getConvInAreaUnitNum() {
		return convInAreaUnitNum;
	}

	public void setConvInAreaUnitNum(String convInAreaUnitNum) {
		this.convInAreaUnitNum = convInAreaUnitNum;
	}

	public String getOriginCountryCode() {
		return originCountryCode;
	}

	public void setOriginCountryCode(String originCountryCode) {
		this.originCountryCode = originCountryCode;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getProducerName() {
		return producerName;
	}

	public void setProducerName(String producerName) {
		this.producerName = producerName;
	}

	public String getIsCncaPor() {
		return isCncaPor;
	}

	public void setIsCncaPor(String isCncaPor) {
		this.isCncaPor = isCncaPor;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getIsLawReview() {
		return isLawReview;
	}

	public void setIsLawReview(String isLawReview) {
		this.isLawReview = isLawReview;
	}

	public String getPerIndex() {
		return perIndex;
	}

	public void setPerIndex(String perIndex) {
		this.perIndex = perIndex;
	}

	public String getAuthInfo() {
		return authInfo;
	}

	public void setAuthInfo(String authInfo) {
		this.authInfo = authInfo;
	}

	public String getLicenseCode() {
		return licenseCode;
	}

	public void setLicenseCode(String licenseCode) {
		this.licenseCode = licenseCode;
	}

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public String getCheckOrgCode() {
		return checkOrgCode;
	}

	public void setCheckOrgCode(String checkOrgCode) {
		this.checkOrgCode = checkOrgCode;
	}

	public String getValidity() {
		return validity;
	}

	public void setValidity(String validity) {
		this.validity = validity;
	}

	public String getTradeMode() {
		return tradeMode;
	}

	public void setTradeMode(String tradeMode) {
		this.tradeMode = tradeMode;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getStandard() {
		return standard;
	}

	public void setStandard(String standard) {
		this.standard = standard;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}
