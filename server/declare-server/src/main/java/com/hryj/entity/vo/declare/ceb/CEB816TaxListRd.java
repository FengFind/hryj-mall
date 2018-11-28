package com.hryj.entity.vo.declare.ceb;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

/**
 * @author 白飞
 * @className: CEB816TaxListRd
 * @description:
 * @create 2018/10/11 14:39
 **/
@XmlRootElement(name="TaxListRd")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CEB816TaxListRd {

    /** 序号 */
    private Integer gnum;
    /** 商品编码-hscode */
    private String gcode;
    /** 商品价格 */
    private BigDecimal taxPrice;
    /** 关税 */
    private BigDecimal customsTax;
    /** 增值税 */
    private BigDecimal valueAddedTax;
    /** 消费税 */
    private BigDecimal consumptionTax;

}
