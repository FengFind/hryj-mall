package com.hryj.entity.bo.declare;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 白飞
 * @className: CebOrderDetail
 * @description: 订单申报明细
 * @create 2018/9/26 10:29
 **/
@Data
@TableName("dec_ceb_order_detail")
public class CebOrderDetail extends  BaseEntity{

    /** 订单ID */
    @TableField(value = "order_id")
    private Long orderId;

    /** 商品序号  */
    @TableField(value = "gnum")
    private Integer gnum;

    /** 企业商品货号 */
    @TableField(value = "item_no")
    private String itemNo;

    /** 企业商品名称 */
    @TableField(value = "item_name")
    private String itemName;

    /** 单位 */
    @TableField(value = "unit")
    private String unit;

    /** 企业商品描述 */
    @TableField(value = "item_describe")
    private String itemDescribe;

    /** 商品数量 */
    @TableField(value = "qty")
    private Integer qty;

    /** 单价 */
    @TableField(value = "price")
    private BigDecimal price;

    /** 总价 */
    @TableField(value = "total_price")
    private BigDecimal totalPrice;

    /** 币制  */
    @TableField(value = "currency")
    private String currency;

    /** 原产国 */
    @TableField(value = "country")
    private String country;

    /** 条形码  */
    @TableField(value = "bar_code")
    private String barCode;

    /** 备注 */
    @TableField(value = "note")
    private String note;

    /** 规格型号 */
    @TableField(value = "gmodel")
    private String gmodel;
}
