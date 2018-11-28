package com.hryj.entity.vo.declare.order;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author 白飞
 * @className: EshopOrderNotify
 * @description:
 * @create 2018/9/29 9:04
 **/
@Data
@XmlRootElement(name="OrderNotify")
@XmlAccessorType(XmlAccessType.FIELD)
public class EshopOrderNotify {

    @XmlElement(name = "Order")
    private EshopOrder order;

    @XmlElement(name = "Waybill")
    private EshopWaybill waybill;

    @XmlElement(name = "DeclareReturn")
    private EshopDeclareReturn declareReturn;
}
