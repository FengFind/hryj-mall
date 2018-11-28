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
public class GCOrderBodyGoodsDetail {

    @XmlElement
    private String serial;
    @XmlElement
    private String price;
    @XmlElement
    private Integer num;
}
