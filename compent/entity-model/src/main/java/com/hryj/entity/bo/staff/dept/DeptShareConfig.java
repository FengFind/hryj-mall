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
 * 组织节点分润配置(末级节点组织不参与分润)
 *
 * @author daitingbo
 * @since 2018-07-03
 */
@Data
@TableName("sf_dept_share_config")
public class DeptShareConfig extends Model<DeptShareConfig> {


    @TableId(value = "id",type =IdType.ID_WORKER)
    private Long id;
    /**
     * 门店id
     */
    private Long store_id;
    /**
     * 部门id
     */
    private Long dept_id;
    /**
     * 分润的员工id
     */
    private Long staff_id;
    /**
     * 分润比例
     */
    private BigDecimal share_ratio;
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
