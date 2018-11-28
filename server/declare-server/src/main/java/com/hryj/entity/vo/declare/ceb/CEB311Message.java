package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/***
 * 
 * 电子订单数据
 * 
 * @author BF
 *
 */

@XmlRootElement(name="CEB311Message",namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
public class CEB311Message {
	
	@XmlAttribute(name = "guid")
    private String guid;

    @XmlAttribute(name = "version")
    private String version = "1.0";
	
	@XmlElement(name = "Order",namespace="http://www.chinaport.gov.cn/ceb")
	private CEB311Order order = new CEB311Order();
	
	@XmlElement(name = "BaseTransfer",namespace="http://www.chinaport.gov.cn/ceb")
	private BaseTransfer baseTransfer = new BaseTransfer();
	
	public CEB311Message() {
		super();
	}

	public CEB311Message(CEB311Order order, BaseTransfer baseTransfer) {
		super();
		this.order = order;
		this.baseTransfer = baseTransfer;
	}
	
	public CEB311Message(String guid, String version, CEB311Order order, BaseTransfer baseTransfer) {
		super();
		this.guid = guid;
		this.version = version;
		this.order = order;
		this.baseTransfer = baseTransfer;
	}

	public CEB311Order getOrder() {
		return order;
	}

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

	public void setOrder(CEB311Order order) {
		this.order = order;
	}

	public BaseTransfer getBaseTransfer() {
		return baseTransfer;
	}

	public void setBaseTransfer(BaseTransfer baseTransfer) {
		this.baseTransfer = baseTransfer;
	}

}
