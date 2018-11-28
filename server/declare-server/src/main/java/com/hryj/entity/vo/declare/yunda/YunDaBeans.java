package com.hryj.entity.vo.declare.yunda;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author 白飞
 * @className: YunDaBeans
 * @description:
 * @create 2018/9/28 10:06
 **/
@Data
@XmlRootElement(name = "beans")
@XmlAccessorType(XmlAccessType.FIELD)
public class YunDaBeans {

    /** 请求类型 */
    private String req_type = "create_order";

    private YunDaHawbs hawbs = new YunDaHawbs();
}
