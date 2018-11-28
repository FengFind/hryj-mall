package com.hryj.entity.vo.declare.yunda;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author 白飞
 * @className: YunDaResponse
 * @description:
 * @create 2018/9/28 10:11
 **/
@Data
@XmlRootElement(name = "Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class YunDaResponse {

    /** 订单号 */
    private String hawbno = "";

    /** 运单号 */
    private String mail_no = "";

    /** 可打印信息 */
    private String pdf_info = "";

    /** 状态标识 */
    private  String code = "";

    /** 提示信息 */
    private String msg = "";
}
