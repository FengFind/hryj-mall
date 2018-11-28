package com.hryj.entity.vo.declare.ceb;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author 白飞
 * @className: CEB816Message
 * @description:
 * @create 2018/10/11 14:25
 **/
@XmlRootElement(name="CEB816Message")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CEB816Message implements Serializable {

    @XmlElement(name = "Tax")
    private CEB816Tax tax;
}
