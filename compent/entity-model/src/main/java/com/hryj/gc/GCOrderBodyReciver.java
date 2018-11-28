package com.hryj.gc;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author 叶方宇
 * @className: GCOrderBodyReciver
 * @description:
 * @create 2018/9/11 0011 14:09
 **/
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class GCOrderBodyReciver {
    @XmlElement
    private String buy_name;
    @XmlElement
    private String buy_mobile;
    @XmlElement
    private String idnum;
    @XmlElement
    private String take_name;
    @XmlElement
    private String take_mobile;
    @XmlElement
    private Integer province_id;
    @XmlElement
    private Integer city_id;
    @XmlElement
    private Integer area_id;
    @XmlElement
    private String area_info;
    @XmlElement
    private String address;
}
