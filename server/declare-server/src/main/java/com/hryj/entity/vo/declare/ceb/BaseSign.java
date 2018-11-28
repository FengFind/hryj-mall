package com.hryj.entity.vo.declare.ceb;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/***
 * 
 * 签名
 * 
 * @author BF
 *
 */
@XmlRootElement(name="BaseSign",namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class BaseSign {

	private String signData= "";// 签名数据 C..2000 是 商品表头所有业务数据值顺序连接而成的字符串
	private String signTime= "";// 签名时间 C..14 是 签名当前时间,格式YYYYMMDDhhmmss
	private String signResult= "";// 签名结果 C..2000 是  针对签名原始数据(signData+signTime连接串)签名后的结果(BASE64编码)
	@XmlElement(name = "UserInfo")
	private UserInfoSign userInfo= new UserInfoSign();// 签名用户基本信息节点 是
	@XmlElement(name = "CertInfo")
	private CertInfo certInfo= new CertInfo();// 签名用户证书信息节点 是

}
