package com.hryj.entity.bo.declare;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 白飞
 * @className: DecTaxRate
 * @description:
 * @create 2018/10/11 8:42
 **/
@Data
@TableName("p_tax_rate")
public class CebTaxRate extends BaseEntity {

    /** HSCODE */
    @TableField(value = "hs_code")
    private String hsCode;
    /** 商品编码-8位 */
    @TableField(value = "goods_code")
    private String goodsCode;
    /** 商品附加码-2位 */
    @TableField(value = "goods_code_add")
    private String goodsCodeAdd;
    /** 商品名称 */
    @TableField(value = "goods_name")
    private String goodsName;
    /** 低税率 */
    @TableField(value = "low_rate")
    private BigDecimal lowRate;
    /** 高税率 */
    @TableField(value = "high_rate")
    private BigDecimal highRate;
    /** 消费税 */
    @TableField(value = "consume_tax")
    private BigDecimal consumeTax;
    /** 增值税 */
    @TableField(value = "increment_tax")
    private BigDecimal incrementTax;
    /** 第一单位 */
    @TableField(value = "unit_1")
    private String firstUnit;
    /** 第二单位 */
    @TableField(value = "unit_2")
    private String secondUnit;
    /** 备注 */
    @TableField(value = "note")
    private String note;
    /** 行邮税率 */
    @TableField(value = "shuilv")
    private BigDecimal shuilv;
    /** 是否有消费税,  Y有，N没有， 默认N  */
    @TableField(value = "has_consume_tax")
    private String has_consume_tax;
}
