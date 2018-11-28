package com.hryj.entity.bo.staff.role;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

/**
 * 权限资源表
 *
 * @author daitingbo
 * @since 2018-07-03
 */
@Data
@TableName("sf_perm_resource")
public class PermResource extends Model<PermResource> {


    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 权限名称
     */
    private String perm_name;
    /**
     * 权限标识
     */
    private String perm_flag;
    /**
     * 权限url
     */
    private String perm_url;
    /**
     * 权限上级id
     */
    private Long perm_pid;
    /**
     * 权限资源路径
     */
    private String perm_path;
    /**
     * 权限排序
     */
    private Integer perm_sort;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }


}
