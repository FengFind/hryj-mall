package com.hryj.entity.vo.declare.ceb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/***************************************
 * 
 * 退货申请单表体
 * 
 * @author BF
 *
 ***************************************/
@XmlRootElement(name="InvtRefundList",namespace="http://www.chinaport.gov.cn/ceb")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvtRefundList {

	@XmlElement(name = "gnum",namespace="http://www.chinaport.gov.cn/ceb")
	private Integer gnum = 1;// 序号 N4 是 对应原清单表体序号（退货项序号）
	
	@XmlElement(name = "gcode",namespace="http://www.chinaport.gov.cn/ceb")
	private String gcode = "";// 商品编码 C10 是海关对进出口货物规定的类别标识代码，采用海关综合分类表的标准分类，总长度为18位数字代码，前8位由国务院关税税则委员会确定，后2 位由海关根据代征税、暂定税率和贸易管制的需要增设的
	
	@XmlElement(name = "gname",namespace="http://www.chinaport.gov.cn/ceb")
	private String gname = "";// 商品名称 C..250 是 同一类商品的名称。任何一种具体商品可以并只能归入表中的一个条目
	
	@XmlElement(name = "qty",namespace="http://www.chinaport.gov.cn/ceb")
	private Double qty = 0d;// 数量 N19,5 是 退货数量
	
	@XmlElement(name = "unit",namespace="http://www.chinaport.gov.cn/ceb")
	private String unit = "";// 计量单位 C3 是 海关标准的参数代码 《JGS-20 海关业务代码集》- 计量单位代码
	
	@XmlElement(name = "note",namespace="http://www.chinaport.gov.cn/ceb")
	private String note = "";// 备注 C..1000 否
	public Integer getGnum() {
		return gnum;
	}
	public void setGnum(Integer gnum) {
		this.gnum = gnum;
	}
	public String getGcode() {
		return gcode;
	}
	public void setGcode(String gcode) {
		this.gcode = gcode;
	}
	public String getGname() {
		return gname;
	}
	public void setGname(String gname) {
		this.gname = gname;
	}
	public Double getQty() {
		return qty;
	}
	public void setQty(Double qty) {
		this.qty = qty;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}

}
