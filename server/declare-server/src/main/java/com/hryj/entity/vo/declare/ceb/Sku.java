package com.hryj.entity.vo.declare.ceb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/***
 * 
 * 商品
 * 
 * @author YUANLIU
 *
 */
@XmlRootElement(name="Sku",namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
public class Sku {
	
	@XmlElement(name = "SkuHead",namespace="http://www.chinaport.gov.cn/ceb")
	private SkuHead skuHead = new SkuHead();//商品信息头
	
	@XmlElement(name = "SkuAtta",namespace="http://www.chinaport.gov.cn/ceb")
	private List<SkuAtta> skuAttaList = new ArrayList<SkuAtta>();//商品信息附件
	
	public SkuHead getSkuHead() {
		return skuHead;
	}

	public void setSkuHead(SkuHead skuHead) {
		this.skuHead = skuHead;
	}

	public List<SkuAtta> getSkuAttaList() {
		return skuAttaList;
	}

	public void setSkuAttaList(List<SkuAtta> skuAttaList) {
		this.skuAttaList = skuAttaList;
	}
	
	
}
