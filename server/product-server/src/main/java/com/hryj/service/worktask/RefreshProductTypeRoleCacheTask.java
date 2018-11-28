package com.hryj.service.worktask;

import com.hryj.cache.ProductTypeCacheHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author 王光银
 * @className: RefreshProductTypeRoleCacheTask
 * @description: 刷新角色或者员工的商品类型权限缓存数据
 * @create 2018/8/31 0031 12:12
 **/
@Slf4j
public class RefreshProductTypeRoleCacheTask implements Runnable {

    private Long target_id;

    private List<String> product_type_permission_list;

    public RefreshProductTypeRoleCacheTask(Long target_id, List<String> product_type_permission_list) {
        this.target_id = target_id;
        this.product_type_permission_list = product_type_permission_list;
    }

    @Override
    public void run() {
        if (this.target_id == null || this.target_id <= 0L) {
            return;
        }
        ProductTypeCacheHandler.resetProductTypePermission(this.target_id, this.product_type_permission_list);
    }
}
