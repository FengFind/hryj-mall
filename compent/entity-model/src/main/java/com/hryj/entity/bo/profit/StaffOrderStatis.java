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
 * @className: StaffOrderStatis
 * @description: 员工订单数据统计
 * @create 2018/7/11 16:17
 **/
@Data
@TableName("pr_staff_order_statis")
public class StaffOrderStatis extends Model<StaffOrderStatis> {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 统计日期
     */
    private String statis_date;
    /**
     * 员工id
     */
    private Long staff_id;
    /**
     * 门店id
     */
    private Long store_id;
    /**
     * 部门名称
     */
    private String dept_name;
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
     * 配送分润
     */
    private BigDecimal distribution_profit;
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
     * 代下单分润
     */
    private BigDecimal help_order_profit;
    /**
     * 服务分润
     */
    private BigDecimal service_profit;
    /**
     * 推荐注册用户数
     */
    private Integer referral_register_num;
    /**
     * 创建时间
     */
    private Date create_time;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
