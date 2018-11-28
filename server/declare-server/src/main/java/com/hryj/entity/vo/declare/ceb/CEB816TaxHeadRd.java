package com.hryj.entity.vo.declare.ceb;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author 白飞
 * @className: CEB816TaxHeadRd
 * @description:
 * @create 2018/10/11 14:29
 **/
@XmlRootElement(name="TaxHeadRd")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CEB816TaxHeadRd {

    /** UUID */
    private String guid;
    /** 回执时间 */
    private String returnTime;
    /** 清单报文 */
    private String invtNo;
    /** 税金编号 */
    private String taxNo;
    /** 关税 */
    private String customsTax;
    /** 增值税 */
    private String valueAddedTax;
    /** 消费税 */
    private String consumptionTax;
    /** 状态 */
    private String status;
    /** 税号 */
    private String entDutyNo;
    /** 备注 */
    private String note;
    /** 担保企业 */
    private String assureCode;
    /** 电商代码 */
    private String ebcCode;
    /** 物流编码 */
    private String logisticsCode;
    /** 申报企业代码 */
    private String agentCode;
    /** 关区 */
    private String customsCode;

}
