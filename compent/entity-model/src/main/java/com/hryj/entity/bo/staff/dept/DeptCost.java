package com.hryj.entity.bo.staff.dept;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 部门组织成本表
 *
 * @author daitingbo
 * @since 2018-07-03
 */
@Data
@TableName("sf_dept_cost")
public class DeptCost extends Model<DeptCost> {


    @TableId(value="id",type = IdType.ID_WORKER)
    private Long id;
    /**
     * 部门id
     */
    private Long dept_id;
    /**
     * 成本名称
     */
    private String cost_name;
    /**
     * 成本金额
     */
    private BigDecimal cost_amt;
    /**
     * 数据状态:1-正常,0-删除
     */
    private Integer data_status;
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
