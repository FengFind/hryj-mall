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
 * @className: DeptGrossProfitBalance
 * @description: 部门毛利结算表
 * @create 2018/8/16 11:09
 **/
@Data
@TableName("pr_dept_gross_profit_balance")
public class DeptGrossProfitBalance extends Model<DeptGrossProfitBalance> {


    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 结算月份
     */
    private String balance_month;
    /**
     * 员工id
     */
    private Long staff_id;
    /**
     * 部门id
     */
    private Long dept_id;
    /**
     * 部门名称
     */
    private String dept_name;
    /**
     * 部门路径
     */
    private String dept_path;
    /**
     * 门店或者仓库id
     */
    private Long party_id;
    /**
     * 所分毛利金额
     */
    private BigDecimal gross_profit_amt;
    /**
     * 创建时间
     */
    private Date create_time;
    /**
     * 更新时间
     */
    private Date update_time;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
