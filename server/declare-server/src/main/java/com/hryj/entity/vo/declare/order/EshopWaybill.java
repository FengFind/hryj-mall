package com.hryj.entity.vo.declare.order;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author 白飞
 * @className: EshopOrder
 * @description:
 * @create 2018/10/12 9:58
 **/
@Data
@XmlRootElement(name="Waybill")
@XmlAccessorType(XmlAccessType.FIELD)
public class EshopWaybill {

    /** 物流编码-如：yunda、ems等 */
    private String waybillKey;
    /** 物流名称 */
    private String waybillName;
    /** 物流单号 */
    private String waybill;
}
