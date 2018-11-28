package com.hryj.entity.vo.declare.order;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

/**
 * @author 白飞
 * @className: EshopOrder
 * @description:
 * @create 2018/10/12 9:58
 **/
@Data
@XmlRootElement(name="Order")
@XmlAccessorType(XmlAccessType.FIELD)
public class EshopOrder {

    /** 电商企业海关备案编码 */
    private String ebcCode;
    /** 电商企业海关备案名称 */
    private String ebcName;
    /** 申报订单号 */
    private String orderNo;
    /** 申报内部号 */
    private String copNo;
    /** 清单号 */
    private String invtNo;
    /** 预录入号 */
    private String preNo;
    /** 申报金额 */
    private BigDecimal orderAmount;
    /** 申报税金 */
    private BigDecimal taxAmount;
    /** 备注 */
    private String note;
}
