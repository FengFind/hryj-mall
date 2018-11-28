package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/*****************
 * 
 * 签名用户证书信息节点
 * 
 * @author BF
 *
 */
@XmlRootElement(name = "CertInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class CertInfo {

	private String certType= "";// 证书类型 C1 是 E-电子口岸CA
	private String certNo= "";// 证书编号 C..100 是 证书编号
	private String certKey= "";// 证书内容 C..1000 否 包含公钥的证书信息(BASE64编码)

	public String getCertType() {
		return certType;
	}

	public void setCertType(String certType) {
		this.certType = certType;
	}

	public String getCertNo() {
		return certNo;
	}

	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}

	public String getCertKey() {
		return certKey;
	}

	public void setCertKey(String certKey) {
		this.certKey = certKey;
	}

}
