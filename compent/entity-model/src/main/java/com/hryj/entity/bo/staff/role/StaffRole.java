package com.hryj.entity.bo.staff.role;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 员工角色表
 *
 * @author daitingbo
 * @since 2018-07-03
 */
@Data
@TableName("sf_staff_role")
public class StaffRole extends Model<StaffRole> {


    @TableId(value = "id",type = IdType.ID_WORKER)
    private Long id;
    /**
     * 角色名称
     */
    private String role_name;
    /**
     * 角色状态:1-正常,0-停用
     */
    private Integer role_status;
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
