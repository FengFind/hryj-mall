package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/***
 * 
 * 商品消息回执
 * 
 * @author YUANLIU
 *
 */
@XmlRootElement(name="CEB300Message")
@XmlAccessorType(XmlAccessType.FIELD)
public class CEB300Message {
	
	@XmlAttribute(name = "guid")
    private String guid;

    @XmlAttribute(name = "version")
    private String version;
	
	@XmlElement(name = "SkuReturn")
	private SkuReturn skuReturn = new SkuReturn();

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

	public SkuReturn getSkuReturn() {
		return skuReturn;
	}

	public void setSkuReturn(SkuReturn skuReturn) {
		this.skuReturn = skuReturn;
	}


	
	

}
