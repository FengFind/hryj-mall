package com.hryj.entity.vo.declare.yunda;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author 白飞
 * @className: YunDaHawbs
 * @description:
 * @create 2018/9/28 10:05
 **/
@Data
@XmlRootElement(name = "Hawbs")
@XmlAccessorType(XmlAccessType.FIELD)
public class YunDaHawbs {

    private YunDaHawb hawb = new YunDaHawb();
}
