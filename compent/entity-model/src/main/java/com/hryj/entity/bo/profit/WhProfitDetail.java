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
 * @className: WhProfitDetail
 * @description: 仓库分润明细
 * @create 2018/7/23 21:15
 **/
@Data
@TableName("pr_wh_profit_detail")
public class WhProfitDetail extends Model<WhOrderStatis> {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 分润月份
     */
    private String profit_month;
    /**
     * 仓库id
     */
    private Long wh_id;
    /**
     * 仓库名称
     */
    private String wh_name;
    /**
     * 部门id
     */
    private Long dept_id;
    /**
     * 员工id
     */
    private Long dept_staff_id;
    /**
     * 分润金额
     */
    private BigDecimal profit_amt;
    /**
     * 创建时间
     */
    private Date create_time;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
