package com.hryj.entity.bo.declare;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 白飞
 * @className: CebTaxDetail
 * @description:
 * @create 2018/10/11 15:20
 **/
@Data
@TableName("dec_ceb_tax_detail")
public class CebTaxDetail extends BaseEntity{


    /** 税金ID */
    @TableField(value = "tax_id")
    private Long taxId;
    /** 序号 */
    @TableField(value = "gnum")
    private Integer gnum;
    /** 商品编码-hscode */
    @TableField(value = "gcode")
    private String gcode;
    /** 商品价格 */
    @TableField(value = "tax_price")
    private BigDecimal taxPrice;
    /** 关税 */
    @TableField(value = "customs_tax")
    private BigDecimal customsTax;
    /** 增值税 */
    @TableField(value = "value_added_tax")
    private BigDecimal valueAddedTax;
    /** 消费税 */
    @TableField(value = "consumption_tax")
    private BigDecimal consumptionTax;
}
