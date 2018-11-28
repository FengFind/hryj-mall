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
 * @className: StoreOrderStatis
 * @description: 门店订单数据统计
 * @create 2018/7/11 16:52
 **/
@Data
@TableName("pr_store_order_statis")
public class StoreOrderStatis extends Model<StoreOrderStatis> {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 统计日期
     */
    private String statis_date;
    /**
     * 门店id
     */
    private Long store_id;
    /**
     * 门店名称
     */
    private String store_name;
    /**
     * 部门路径
     */
    private String dept_path;
    /**
     * 已配送数量
     */
    private Integer distribution_num;
    /**
     * 已取件数量
     */
    private Integer take_back_num;
    /**
     * 超时配送数量
     */
    private Integer timeout_distribution_num;
    /**
     * 配送成本
     */
    private BigDecimal distribution_cost;
    /**
     * 代下订单总数
     */
    private Integer help_order_num;
    /**
     * 代下订单总额
     */
    private BigDecimal help_order_amt;
    /**
     * 代下订单商品总数
     */
    private Integer help_order_product_num;
    /**
     * 门店订单总数
     */
    private Integer store_order_num;
    /**
     * 门店订单总额
     */
    private BigDecimal store_order_amt;
    /**
     * 门店订单毛利,不包含代下单
     */
    private BigDecimal store_order_profit;
    /**
     * 门店订单商品总数
     */
    private Integer store_order_product_num;
    /**
     * 推荐注册用户数
     */
    private Integer referral_register_num;
    /**
     * 新增交易用户数
     */
    private Integer new_trade_user_num;
    /**
     * 创建时间
     */
    private Date create_time;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
