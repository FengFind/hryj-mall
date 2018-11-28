package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


/***
 * 
 * 扩展消息
 * 
 * @author BF
 *
 */
@XmlRootElement(name="ExtendMessage",namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExtendMessage {
	
	private String name= "";// 自定义报文名称 C..30 是 自定义报文名称
	private String version= "";// 自定义报文版本 C..30 是 自定义报文版本
	private String message= "";// 自定义报文实体 xml节点 是 自定义报文实体

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
