package com.hryj.gc;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * @author 叶方宇
 * @className: GCOrderBodyGoods
 * @description:
 * @create 2018/9/11 0011 14:12
 **/
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class GCOrderBodyGoods {

    @XmlElement
    private List<GCOrderBodyGoodsDetail> goods;

    @XmlElement
    private GCOrderBodyOther other;

    @XmlElement
    private GCOrderBodyReciver reciver;

}
