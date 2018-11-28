package com.hryj.gc;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author 叶方宇
 * @className: GCOrderHead
 * @description:
 * @create 2018/9/11 0011 14:06
 **/
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class GCOrderHead {
    @XmlElement
    private String action;
    @XmlElement
    private String return_type;
    @XmlElement
    private String partner_id;


}
