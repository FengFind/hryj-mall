package com.hryj.entity.bo.profit;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 李道云
 * @className: StaffDeptChangeRecord
 * @description: 员工部门变动记录表
 * @create 2018/7/19 20:24
 **/
@Data
@TableName("pr_staff_dept_change_record")
public class StaffDeptChangeRecord extends Model<StaffDeptChangeRecord> {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;

    /**
     * 员工id
     */
    private Long staff_id;
    /**
     * 员工名称
     */
    private String staff_name;
    /**
     * 变动前部门id
     */
    private Long pre_dept_id;
    /**
     * 变动前部门名称
     */
    private String pre_dept_name;
    /**
     * 变动后部门id
     */
    private Long after_dept_id;
    /**
     * 变动后部门名称
     */
    private String after_dept_name;
    /**
     * 变动时间
     */
    private Date change_time;
    /**
     * 变动类型：01-员工离职，02-转移部门
     */
    private String change_type;
    /**
     * 结算标识：0-未结算，1-已结算
     */
    private Integer balance_flag;
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
