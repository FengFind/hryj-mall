package com.hryj.entity.bo.declare;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 白飞
 * @className: CebInvtDetail
 * @description:
 * @create 2018/9/26 11:03
 **/
@Data
@TableName("dec_ceb_invt_detail")
public class CebInvtDetail extends BaseEntity{

    /** 清单ID */
    @TableField(value = "invt_id")
    private Long invtId;

    /** 商品序号 */
    @TableField(value = "gnum")
    private Integer gnum;

    /** 账册备案号 */
    @TableField(value = "item_record_no")
    private String itemRecordNo;

    /** 企业商品货号 */
    @TableField(value = "item_no")
    private String itemNo;

    /** 企业商品品名 */
    @TableField(value = "item_name")
    private String itemName;

    /** 商品编码 */
    @TableField(value = "gcode")
    private String gcode;

    /** 商品名称 */
    @TableField(value = "gname")
    private String gname;

    /** 商品规格型号 */
    @TableField(value = "gmodel")
    private String gmodel;

    /** 条码 */
    @TableField(value = "bar_code")
    private String barCode;

    /** 原产国(地区) */
    @TableField(value = "country")
    private String country;

    /** 币制 */
    @TableField(value = "currency")
    private String currency;

    /** 数量 */
    @TableField(value = "qty")
    private Integer qty;

    /** 法定数量 */
    @TableField(value = "qty1")
    private BigDecimal qty1;

    /** 第二数量 */
    @TableField(value = "qty2")
    private BigDecimal qty2;

    /** 计量单位 */
    @TableField(value = "unit")
    private String unit;

    /** 法定计量单位 */
    @TableField(value = "unit1")
    private String unit1;

    /** 第二计量单位 */
    @TableField(value = "unit2")
    private String unit2;

    /** 单价 */
    @TableField(value = "price")
    private BigDecimal price;

    /** 总价 */
    @TableField(value = "total_price")
    private BigDecimal totalPrice;

    /** 备注 */
    @TableField(value = "note")
    private String note;

    /** 贸易国 */
    @TableField(value = "trade_country")
    private String tradeCountry;
}
