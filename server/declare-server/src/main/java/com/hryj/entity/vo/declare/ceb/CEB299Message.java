package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/***
 * 
 * 商品报文头
 * 
 * @author YUANLIU
 *
 */
@XmlRootElement(name="CEB299Message",namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
public class CEB299Message {
	
	@XmlAttribute(name = "guid")
    private String guid;

    @XmlAttribute(name = "version")
    private String version;
	
	@XmlElement(name = "Sku",namespace="http://www.chinaport.gov.cn/ceb")
	private Sku sku = new Sku();

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Sku getSku() {
		return sku;
	}

	public void setSku(Sku sku) {
		this.sku = sku;
	}
	
	

}
