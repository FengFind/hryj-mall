package com.hryj.entity.vo.declare.ceb;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 进口清单数据(加签)
 * 
 * @author WH
 *
 */
@XmlRootElement(name = "CEB621Message",namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CEB621MessageSign {
	
	@XmlAttribute(name = "guid")
    private String guid;

    @XmlAttribute(name = "version")
    private String version;
    
	@XmlElement(name = "Inventory",namespace="http://www.chinaport.gov.cn/ceb")
	private Inventory inventory = new Inventory();
	
	@XmlElement(name = "BaseTransfer",namespace="http://www.chinaport.gov.cn/ceb")
	private BaseTransfer baseTransfer = new BaseTransfer();
	
	@XmlElement(name = "Signature",namespace="http://www.w3.org/2000/09/xmldsig#")
	private Signature Signature = new Signature();

}
