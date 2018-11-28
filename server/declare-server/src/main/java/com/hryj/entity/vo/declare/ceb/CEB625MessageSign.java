package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/*******************
 * 
 * 
 * 退货申请单数据(加签)
 * 
 * @author BF
 *
 */
@XmlRootElement(name = "CEB625Message",namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
public class CEB625MessageSign {
	
	@XmlAttribute(name = "guid")
    private String guid;

    @XmlAttribute(name = "version")
    private String version;
    
	@XmlElement(name = "BaseTransfer",namespace="http://www.chinaport.gov.cn/ceb")
	private BaseTransfer baseTransfer = new BaseTransfer();

	@XmlElement(name = "InvtRefund",namespace="http://www.chinaport.gov.cn/ceb")
	private InvtRefund invtRefund = new InvtRefund();

	@XmlElement(name = "Signature",namespace="http://www.w3.org/2000/09/xmldsig#")
	private Signature Signature = new Signature();
	
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

	public BaseTransfer getBaseTransfer() {
		return baseTransfer;
	}

	public void setBaseTransfer(BaseTransfer baseTransfer) {
		this.baseTransfer = baseTransfer;
	}

	public InvtRefund getInvtRefund() {
		return invtRefund;
	}

	public void setInvtRefund(InvtRefund invtRefund) {
		this.invtRefund = invtRefund;
	}

	public Signature getSignature() {
		return Signature;
	}

	public void setSignature(Signature signature) {
		Signature = signature;
	}

}
