package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Signature",namespace="http://www.w3.org/2000/09/xmldsig#")
@XmlAccessorType(XmlAccessType.FIELD)
public class Signature {

	private SignedInfo SignedInfo;
	private String SignatureValue;
	private KeyInfo KeyInfo;
	public SignedInfo getSignedInfo() {
		return SignedInfo;
	}
	public void setSignedInfo(SignedInfo signedInfo) {
		SignedInfo = signedInfo;
	}
	public String getSignatureValue() {
		return SignatureValue;
	}
	public void setSignatureValue(String signatureValue) {
		SignatureValue = signatureValue;
	}
	public KeyInfo getKeyInfo() {
		return KeyInfo;
	}
	public void setKeyInfo(KeyInfo keyInfo) {
		KeyInfo = keyInfo;
	}
	
}
