package com.hryj.mapper.prodtype;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.permission.ProductTypeRole;
import com.hryj.entity.vo.product.prodtype.ProductTypeRoleAndProductType;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 王光银
 * @className: ProductTypeRoleMapper
 * @description:
 * @create 2018/9/10 0010 11:23
 **/
@Component
public interface ProductTypeRoleMapper extends BaseMapper<ProductTypeRole> {

    List<ProductTypeRoleAndProductType> findRolePermission(@Param(value = "role_id") Long role_id);

    List<ProductTypeRoleAndProductType> findStaffPermission(@Param(value = "staff_id") Long staff_id);
}
