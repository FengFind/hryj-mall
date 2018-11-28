package com.hryj.entity.vo.declare.ceb;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author 白飞
 * @className: CEB816Tax
 * @description:
 * @create 2018/10/11 14:28
 **/
@XmlRootElement(name="Tax")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CEB816Tax {

    @XmlElement(name = "TaxHeadRd")
    private CEB816TaxHeadRd taxHeadRd;

    @XmlElement(name = "TaxListRd")
    private List<CEB816TaxListRd> taxListRd;
}
