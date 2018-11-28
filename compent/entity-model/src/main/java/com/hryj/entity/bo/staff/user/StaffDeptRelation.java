package com.hryj.entity.bo.staff.user;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 员工组织关系表
 *
 * @author daitingbo
 * @since 2018-07-03
 */
@Data
@TableName("sf_staff_dept_relation")
public class StaffDeptRelation extends Model<StaffDeptRelation> {


    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 员工id
     */
    private Long staff_id;
    /**
     * 部门id
     */
    private Long dept_id;
    /**
     * 员工岗位
     */
    private String staff_job;
    /**
     * 工资金额
     */
    private BigDecimal salary_amt;
    /**
     * 代下单提成比例
     */
    private BigDecimal help_order_ratio;
    /**
     * 服务提成比例
     */
    private BigDecimal service_ratio;
    /**
     * 成本分摊比例
     */
    private BigDecimal share_cost_ratio;

    /**
     * 配送提成
     */
    private BigDecimal distribution_amt=new BigDecimal(0);
    /**
     * 员工状态:1-正常,0-离职
     */
    private Boolean staff_status;
    /**
     * 操作人id
     */
    private Long operator_id;
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
