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
 * @className: GroupBalanceSummary
 * @description: 集团结算汇总表
 * @create 2018/7/23 21:25
 **/
@Data
@TableName("pr_group_balance_summary")
public class GroupBalanceSummary extends Model<GroupBalanceSummary> {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;

    /**
     * 分润月份
     */
    private String balance_month;
    /**
     * 实得分润
     */
    private BigDecimal actual_profit;
    /**
     * 门店分润总额
     */
    private BigDecimal store_profit;
    /**
     * 仓库分润总额
     */
    private BigDecimal wh_profit;
    /**
     * 创建时间
     */
    private Date create_time;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
