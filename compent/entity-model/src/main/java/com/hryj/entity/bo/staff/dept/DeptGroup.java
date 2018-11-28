package com.hryj.entity.bo.staff.dept;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 部门组织表
 *
 * @author daitingbo
 * @since 2018-07-03
 */
@Data
@TableName("sf_dept_group")
public class DeptGroup extends Model<DeptGroup> {


    @TableId(value = "id", type = IdType.INPUT)
    private Long id;
    /**
     * 部门名称
     */
    private String dept_name;
    /**
     * 部门上级id
     */
    private Long dept_pid;
    /**
     * 部门级别
     */
    private Integer dept_level;
    /**
     * 部门类型:01-门店,02-仓库,03-普通部门
     */
    private String dept_type;
    /**
     * 是否为区域公司:1-是,0-否
     */
    private Integer company_flag;
    /**
     * 是否末级节点:1-是,0-否
     */
    private Integer end_flag;
    /**
     * 部门状态:1-正常,0-关闭
     */
    private Integer dept_status;
    /**
     * 组织路径:","分隔
     */
    private String dept_path;
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
