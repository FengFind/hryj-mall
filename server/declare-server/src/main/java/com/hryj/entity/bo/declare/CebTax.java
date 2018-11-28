package com.hryj.entity.bo.declare;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

/**
 * @author 白飞
 * @className: CebTax
 * @description: 税金
 * @create 2018/10/11 15:08
 **/
@Data
@TableName("dec_ceb_tax")
public class CebTax extends BaseEntity{

    /** UUID */
    @TableField(value = "guid")
    private String guid;
    /** 回执时间 */
    @TableField(value = "return_time")
    private String returnTime;
    /** 清单报文 */
    @TableField(value = "invt_no")
    private String invtNo;
    /** 税金编号 */
    @TableField(value = "tax_no")
    private String taxNo;
    /** 关税 */
    @TableField(value = "customs_tax")
    private String customsTax;
    /** 增值税 */
    @TableField(value = "value_added_tax")
    private String valueAddedTax;
    /** 消费税 */
    @TableField(value = "consumption_tax")
    private String consumptionTax;
    /** 状态 */
    @TableField(value = "status")
    private String status;
    /** 税号 */
    @TableField(value = "ent_duty_no")
    private String entDutyNo;
    /** 备注 */
    @TableField(value = "note")
    private String note;
    /** 担保企业 */
    @TableField(value = "assure_code")
    private String assureCode;
    /** 电商代码 */
    @TableField(value = "ebc_code")
    private String ebcCode;
    /** 物流编码 */
    @TableField(value = "logistics_code")
    private String logisticsCode;
    /** 申报企业代码 */
    @TableField(value = "agent_code")
    private String agentCode;
    /** 关区 */
    @TableField(value = "customs_code")
    private String customsCode;
}
