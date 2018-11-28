package com.hryj.pay.vo;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author 白飞
 * @className: AliResponse
 * @description:
 * @create 2018/9/30 14:34
 **/
@XmlRootElement(name="AliResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class AliResponse {

    private ResponseAlipay alipay;
}
