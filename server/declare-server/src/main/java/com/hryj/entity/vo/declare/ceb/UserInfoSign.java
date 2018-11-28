package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/***************
 * 
 * 签名用户基本信息节点
 * 
 * @author BF
 *
 */
@XmlRootElement(name = "UserInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserInfoSign {
	
	private String userId	= "";//签名用户编号	C..20	是	电子口岸持卡人IC卡或UKEY编号
	private String userName= "";//	签名用户姓名	C..60	是	电子口岸持卡人姓名
	private String copCode	= "";//签名企业编号	C..20	是	签名企业编号
	private String copName	= "";//签名企业名称	C..100	是	签名企业名称
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getCopCode() {
		return copCode;
	}
	public void setCopCode(String copCode) {
		this.copCode = copCode;
	}
	public String getCopName() {
		return copName;
	}
	public void setCopName(String copName) {
		this.copName = copName;
	}
	
}
