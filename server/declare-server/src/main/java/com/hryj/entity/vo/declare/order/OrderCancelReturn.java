package com.hryj.entity.vo.declare.order;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author 白飞
 * @className: OrderCancelReturn
 * @description: 取消响应
 * @create 2018/10/12 10:29
 **/
@Data
@XmlRootElement(name="OrderCancelReturn")
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderCancelReturn {

    @XmlElement(name = "Order")
    private EshopOrder order;

    @XmlElement(name = "DeclareReturn")
    private EshopDeclareReturn declareReturn;
}
