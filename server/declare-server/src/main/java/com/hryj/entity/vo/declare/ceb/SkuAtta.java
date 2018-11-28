package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/***
 * 
 * 商品信息附件
 * 
 * @author YUANLIU
 *
 */
@XmlRootElement(name="SkuAtta",namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
public class SkuAtta {
	
	@XmlElement(name = "BIZ_TYPE_CODE",namespace="http://www.chinaport.gov.cn/ceb")
	private String bizTypeCode = "";//业务类型编码
	
	@XmlElement(name = "ATTACHED_SEQ_NO",namespace="http://www.chinaport.gov.cn/ceb")
	private String attachedSeqNo = "";//附件序列号
	
	@XmlElement(name = "CERT_TYPE_CODE",namespace="http://www.chinaport.gov.cn/ceb")
	private String certTypeCode = "";//证书类型编号
	
	@XmlElement(name = "FILE_NAME",namespace="http://www.chinaport.gov.cn/ceb")
	private String fileName = "";//文件名
	
	@XmlElement(name = "FILE_TYPE",namespace="http://www.chinaport.gov.cn/ceb")
	private String fileType = "";//文件类型
	
	@XmlElement(name = "STORE_DATE",namespace="http://www.chinaport.gov.cn/ceb")
	private String storeDate = "";//储存日期
	
	
	
	public SkuAtta() {
		
	}

	public SkuAtta(String bizTypeCode, String attachedSeqNo,
			String certTypeCode, String fileName, String fileType,
			String storeDate) {
		super();
		this.bizTypeCode = bizTypeCode;
		this.attachedSeqNo = attachedSeqNo;
		this.certTypeCode = certTypeCode;
		this.fileName = fileName;
		this.fileType = fileType;
		this.storeDate = storeDate;
	}

	public String getBizTypeCode() {
		return bizTypeCode;
	}

	public void setBizTypeCode(String bizTypeCode) {
		this.bizTypeCode = bizTypeCode;
	}

	public String getAttachedSeqNo() {
		return attachedSeqNo;
	}

	public void setAttachedSeqNo(String attachedSeqNo) {
		this.attachedSeqNo = attachedSeqNo;
	}

	public String getCertTypeCode() {
		return certTypeCode;
	}

	public void setCertTypeCode(String certTypeCode) {
		this.certTypeCode = certTypeCode;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getStoreDate() {
		return storeDate;
	}

	public void setStoreDate(String storeDate) {
		this.storeDate = storeDate;
	}
	
	
}
