package com.hryj.pay.vo;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author 白飞
 * @className: Ali
 * @description:
 * @create 2018/9/30 14:36
 **/

@XmlRootElement(name="alipay")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Ali {
    private String is_success;
    private String sign;
    private String sign_type;
    private AliResponse response;
    private AliRequest request;
}
