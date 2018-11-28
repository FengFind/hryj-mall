package com.hryj.gc;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author 叶方宇
 * @className: GCOrderBodyGoods
 * @description:
 * @create 2018/9/11 0011 14:12
 **/
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class GCOrderBodyOther {
    @XmlElement
    private String out_trade_no;
    @XmlElement
    private String trade_no;
    @XmlElement
    private String order_amount;
    @XmlElement
    private String order_tax;
    @XmlElement
    private String payment_code;
    @XmlElement
    private String payment_time;
    @XmlElement
    private String name;
    @XmlElement
    private String note;

}
