package com.hryj.entity.vo.declare.ceb;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/***
 * 
 * 订阅
 * 
 * @author BF
 *
 */
@XmlRootElement(name="BaseSubscribe",namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class BaseSubscribe {
	
	private String status= "";// 订阅状态 C..100 是 用户订阅单证业务状态的信息, ALL-订阅数据和回执, // DATA-只订阅数据,RET- 只订阅回执
	private String dxpMode= "";// 订阅方传输模式 C3 是 DXP-电子口岸交换平台
	private String dxpAddress= "";// 订阅方传输地址 C..100 是 订阅方的传输编号或地址（如：dxp编号)
	private String note= "";// 备注 C..1000 否

}
