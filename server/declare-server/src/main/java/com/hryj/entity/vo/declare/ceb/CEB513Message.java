package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/****************************
 * 
 * 物流运单状态
 * 
 * @author BF
 *
 ****************************/
@XmlRootElement(name = "CEB513Message",namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
public class CEB513Message {
	
	@XmlAttribute(name = "guid")
    private String guid;

    @XmlAttribute(name = "version")
    private String version;
    
	@XmlElement(name = "LogisticsStatus",namespace="http://www.chinaport.gov.cn/ceb")
	private LogisticsStatus logisticsStatus = new LogisticsStatus();
	
	@XmlElement(name = "BaseTransfer",namespace="http://www.chinaport.gov.cn/ceb")
	private BaseTransfer baseTransfer = new BaseTransfer();
	
	public LogisticsStatus getLogisticsStatus() {
		return logisticsStatus;
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

	public void setLogisticsStatus(LogisticsStatus logisticsStatus) {
		this.logisticsStatus = logisticsStatus;
	}

	public BaseTransfer getBaseTransfer() {
		return baseTransfer;
	}

	public void setBaseTransfer(BaseTransfer baseTransfer) {
		this.baseTransfer = baseTransfer;
	}
}
