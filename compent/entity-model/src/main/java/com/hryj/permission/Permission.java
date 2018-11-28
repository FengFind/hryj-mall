package com.hryj.permission;

import com.hryj.entity.bo.staff.role.PermResource;
import com.hryj.utils.UtilMisc;
import com.hryj.utils.UtilValidate;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author 王光银
 * @className: Permission
 * @description:
 * @create 2018/9/10 0010 9:32
 **/
@Data
public class Permission {

    private String permission_type;

    private Long permission_id;

    private String description;

    private String permission_type_id;

    private Long parent_permission_id;

    private List<Permission> children;

    public PermResource convertToPermRes() {
        PermResource pr = new PermResource();
        pr.setId(this.permission_id);
        pr.setPerm_flag(this.description);
        pr.setPerm_name(this.description);
        pr.setPerm_pid(this.parent_permission_id);
        return pr;
    }

    public List<PermResource> convertToPermResList() {
        List<PermResource> list = new LinkedList<>();
        list.add(this.convertToPermRes());
        if (UtilValidate.isNotEmpty(this.children)) {
            for (Permission child : this.children) {
                list.addAll(child.convertToPermResList());
            }
        }
        return list;
    }

    public Set<Long> convertToPermissionSet() {
        Set<Long> set = UtilMisc.toSet(this.permission_id);
        if (UtilValidate.isNotEmpty(this.children)) {
            for (Permission child : this.children) {
                set.addAll(child.convertToPermissionSet());
            }
        }
        return set;
    }
}
