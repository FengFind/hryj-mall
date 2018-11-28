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
@XmlRootElement(name = "Receiver")
@XmlAccessorType(XmlAccessType.FIELD)
public class YunDaReceiver {

    /** 公司信息 Y*/
    private String company = "";
    /** 发出城市 */
    private String city="";
    /** 联系人 */
    private String contacts="";
    /** 邮编信息 Y */
    private String postal_code="";
    /** 联系电话 */
    private String rec_tele="";
    /** 电子邮箱 */
    private String e_mail="";
    /** 收货地址 */
    private String address="";



    public YunDaReceiver(){
    }

    public YunDaReceiver(String address, String contacts, String rec_tele){
        this.address = address;
        this.contacts  = contacts;
        this.rec_tele = rec_tele;
    }
}
