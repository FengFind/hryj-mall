package com.hryj.entity.bo.declare;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 白飞
 * @className: CebProduct
 * @description:
 * @create 2018/10/8 10:16
 **/
@Data
@TableName("dec_ceb_product")
public class CebProduct extends BaseEntity{

    /** 净含量单位枚举 */
    public enum Gunit{
        /** 片、张 */
        sheet,
        /** 克 */
        gram,
        /** 毫升 */
        ml
    }

    /** 企业海关唯一备案号 */
    @TableField(value = "ebc_code")
    private String ebcCode;
    /** 企业商品货号 */
    @TableField(value = "item_no")
    private String itemNo;
    /** 企业商品品名 */
    @TableField(value = "item_name")
    private String itemName;
    /** 商品编码-HSCODE */
    @TableField(value = "gcode")
    private String gcode;
    /** 商品名称 */
    @TableField(value = "gname")
    private String gname;
    /** 商品规格型号 */
    @TableField(value = "gmodel")
    private String gmodel;
    /** 商品描述 */
    @TableField(value = "item_describe")
    private String itemDescribe;
    /** 条码 */
    @TableField(value = "bar_code")
    private String barCode;
    /** 原产国（地区） */
    @TableField(value = "country")
    private String country;
    /** 账册备案料号 */
    @TableField(value = "item_record_no")
    private String itemRecordNno;
    /** 贸易国 */
    @TableField(value = "trade_country")
    private String tradeCountry;
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
    /** 进口日期 */
    @TableField(value = "imported_date")
    private Date importedDate;
    /** 净含量 */
    @TableField(value = "net_contents")
    private BigDecimal netContents;
    /** 净含量单位 */
    @TableField(value = "gunit")
    private Integer gunit;

}
