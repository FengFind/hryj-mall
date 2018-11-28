package com.hryj.entity.bo.profit;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 李道云
 * @className: HelpOrderDetail
 * @description: 代下单数据明细
 * @create 2018/7/16 21:13
 **/
@Data
@TableName("pr_help_order_detail")
public class HelpOrderDetail extends Model<HelpOrderDetail> {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 统计日期
     */
    private String statis_date;
    /**
     * 订单id
     */
    private Long order_id;
    /**
     * 订单金额
     */
    private BigDecimal order_amt;
    /**
     * 订单商品数量
     */
    private Integer order_product_num;
    /**
     * 订单毛利
     */
    private BigDecimal order_profit;
    /**
     * 代下单提成比例
     */
    private BigDecimal help_order_ratio;
    /**
     * 员工id
     */
    private Long staff_id;
    /**
     * 员工订单分润
     */
    private BigDecimal staff_order_profit;
    /**
     * 门店id
     */
    private Long store_id;
    /**
     * 店长id
     */
    private Long store_staff_id;
    /**
     * 店长订单分润
     */
    private BigDecimal store_staff_order_profit;
    /**
     * 创建时间
     */
    private Date create_time;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
