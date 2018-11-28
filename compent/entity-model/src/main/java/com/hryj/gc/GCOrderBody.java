package com.hryj.gc;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author 叶方宇
 * @className: GCOrderBody
 * @description:
 * @create 2018/9/11 0011 14:08
 **/
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class GCOrderBody {

    @XmlElement
    private GCOrderBodyGoods goods;

    @XmlElement
    private GCOrderBodyReciver reciver;

    @XmlElement
    private GCOrderBodyOther other;

}
