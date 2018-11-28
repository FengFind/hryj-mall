package com.hryj.pay.vo;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author 白飞
 * @className: ResponseAlipay
 * @description:
 * @create 2018/9/30 14:33
 **/
@XmlRootElement(name="ResponseAlipay")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class ResponseAlipay {
    private String alipay_declare_no="";
    private String identity_check="";
    private String out_request_no="";
    private String result_code="";
    private String trade_no="";
    private  String detail_error_des;
    private  String detail_error_code;
}
