package com.hryj.entity.bo.declare;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

/**
 * @author 白飞
 * @className: CebPayment
 * @description:
 * @create 2018/9/26 15:14
 **/
@Data
@TableName("dec_ceb_payment")
public class CebPayment extends BaseEntity{

    /** 支付企业备案代码 */
    @TableField(value = "pay_code")
    private String payCode;

    /** 电商企业备案代码 */
    @TableField(value = "ebc_code")
    private String ebcCode;

    /** 支付企业备案名称 */
    @TableField(value = "pay_name")
    private String payName;

    /** 支付编号 */
    @TableField(value = "pay_no")
    private String payNo;
}
