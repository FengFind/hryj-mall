package com.hryj.entity.vo.declare.ceb;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "MES_ASK_INFO")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class DTCFlowMESASKINFO {

	/** 报文类型*/
	public String MESSAGE_TYPE;
	/** 工作编号*/
	public String WORK_NO;
	/** 平台产生编号 */
	public String CREATED_NO;
	/** 操作时间  格式参考 2014-04-29T02:56:15*/
	public String OP_DATE;
	/** 入库成功标记 1、成功  0、失败*/
	public String SUCCESS;
	/** 备注*/
	public String MEMO;
	
}
