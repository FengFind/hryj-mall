package com.hryj.entity.vo.declare.yunda;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author 白飞
 * @className: YunDaReceiver
 * @description:
 * @create 2018/9/28 9:44
 **/
@Data
@XmlRootElement(name = "Sender")
@XmlAccessorType(XmlAccessType.FIELD)
public class YunDaSender {

    /** 公司信息 Y*/
    private String company = "";
    /** 发出城市 */
    private String city="";
    /** 联系人 */
    private String contacts="";
    /** 发件地址 */
    private String address="";
    /** 发件人电话 Y*/
    private String sender_tele="";
    /** 邮编信息 */
    private String postal_code;
    /** 电子邮箱 Y*/
    private String e_mail ="";

    public  YunDaSender(){

    }
    public YunDaSender(String company, String contacts, String sender_tele, String address){
        this.company = company;
        this.contacts  = contacts;
        this.sender_tele  = sender_tele;
        this.address  = address;
    }
}
