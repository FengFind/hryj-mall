package com.hryj.entity.bo.product;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author 汪豪
 * @className: TaxRate
 * @description: 跨境商品基础税率
 * @create 2018/9/10 0010 16:51
 **/
@Data
@TableName("p_tax_rate")
public class TaxRate extends Model<TaxRate> {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * HSCODE
     */
    private String hs_code;

    /**
     * 商品编码8位
     */
    private String goods_code;

    /**
     * 商品编码附加码
     */
    private String goods_code_add;

    /**
     * 商品名称
     */
    private String goods_name;


    /**
     * 关税-低税率
     */
    private BigDecimal low_rate;


    /**
     * 关税-关税-高税率
     */
    private BigDecimal high_rate;

    /**
     * 消费税率
     */
    private BigDecimal consume_tax;

    /**
     * 增值税率
     */
    private BigDecimal increment_tax;

    /**
     * 第一法定单位
     */
    private String unit_1;

    /**
     * 第二法定单位
     */
    private String unit_2;

    /**
     * 备注
     */
    private String note;

    /**
     * 行邮税率
     */
    private BigDecimal shuilv;

    /**
     * 是否有消费税,  Y有，N没有， 默认N
     */
    private String has_consume_tax;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
