package com.hryj.entity.vo.declare.ceb;

public class SignedInfo {

	private String CanonicalizationMethod;
	private String SignatureMethod;
	private Reference Reference;
	public String getCanonicalizationMethod() {
		return CanonicalizationMethod;
	}
	public void setCanonicalizationMethod(String canonicalizationMethod) {
		CanonicalizationMethod = canonicalizationMethod;
	}
	public String getSignatureMethod() {
		return SignatureMethod;
	}
	public void setSignatureMethod(String signatureMethod) {
		SignatureMethod = signatureMethod;
	}
	public Reference getReference() {
		return Reference;
	}
	public void setReference(Reference reference) {
		Reference = reference;
	} 
	
}
