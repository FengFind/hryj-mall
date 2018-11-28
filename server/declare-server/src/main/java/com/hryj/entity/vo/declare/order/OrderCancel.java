package com.hryj.entity.vo.declare.order;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author 白飞
 * @className: OrderCancel
 * @description:
 * @create 2018/10/12 10:24
 **/
@Data
@XmlRootElement(name="OrderCancel")
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderCancel {

    @XmlElement(name = "Order")
    private EshopOrder order;
}
