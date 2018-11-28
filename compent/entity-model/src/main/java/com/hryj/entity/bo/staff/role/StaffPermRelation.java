package com.hryj.entity.bo.staff.role;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

/**
 * 员工权限关系表
 *
 * @author daitingbo
 * @since 2018-07-03
 */
@Data
@TableName("sf_staff_perm_relation")
public class StaffPermRelation extends Model<StaffPermRelation> {


    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 员工id
     */
    private Long staff_id;
    /**
     * 权限id
     */
    private Long perm_id;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }


}
