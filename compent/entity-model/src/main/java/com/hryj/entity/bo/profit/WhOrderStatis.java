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
 * @className: WhOrderStatis
 * @description: 仓库订单数据统计
 * @create 2018/7/23 21:06
 **/
@Data
@TableName("pr_wh_order_statis")
public class WhOrderStatis extends Model<WhOrderStatis> {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 统计日期
     */
    private String statis_date;
    /**
     * 仓库id
     */
    private Long wh_id;
    /**
     * 仓库名称
     */
    private String wh_name;
    /**
     * 部门路径
     */
    private String dept_path;
    /**
     * 仓库订单总数
     */
    private Integer wh_order_num;
    /**
     * 仓库订单总额
     */
    private BigDecimal wh_order_amt;
    /**
     * 仓库订单商品数量
     */
    private Integer wh_order_product_num;
    /**
     * 仓库订单分润
     */
    private BigDecimal wh_order_profit;
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
