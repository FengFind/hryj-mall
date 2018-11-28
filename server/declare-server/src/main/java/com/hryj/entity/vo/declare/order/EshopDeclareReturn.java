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
@XmlRootElement(name="DeclareReturn")
@XmlAccessorType(XmlAccessType.FIELD)
public class EshopDeclareReturn {

    /**
     * 申报状态
     */
    private Integer declareStatus;
    /**
     * 回执信息
     */
    private String returnInfo;
    /**
     * 错误信息
     */
    private String errorInfo;
    /**
     * 回执编码
     */
    private String returnCode;
    /**
     * 回执时间
     */
    private String returnTime;
}
