package com.hryj.pay.vo;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author 白飞
 * @className: AliRequest
 * @description:
 * @create 2018/9/30 14:30
 **/

@XmlRootElement(name="AliRequest")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class AliRequest {

    private  String  merchant_customs_name ="";
    private  String  amount="";
    private  String  _input_charset="";
    private  String  is_split="";
    private  String  sign="";
    private  String  sub_out_biz_no="";
    private  String  buyer_name="";
    private  String  partner="";
    private  String  service="";
    private  String  trade_no="";
    private  String  customs_place="";
    private  String  merchant_customs_code="";
    private  String  buyer_id_no="";
    private  String  out_request_no="";
    private  String  sign_type="";
}
