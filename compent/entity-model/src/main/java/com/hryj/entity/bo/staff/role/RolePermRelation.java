package com.hryj.entity.bo.staff.role;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色权限关系表
 *
 * @author daitingbo
 * @since 2018-07-03
 */
@Data
@TableName("sf_role_perm_relation")
public class RolePermRelation extends Model<RolePermRelation> {


    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 角色id
     */
    private Long role_id;
    /**
     * 权限id
     */
    private Long perm_id;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }


}
