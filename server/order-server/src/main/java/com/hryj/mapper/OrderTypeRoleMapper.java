package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.permission.OrderTypeRole;
import com.hryj.entity.vo.order.ordertype.OrderTypeRoleAndOrderTypeVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 叶方宇
 * @className: OrderTypeRoleMapper
 * @description:
 * @create 2018/9/10 0010 16:40
 **/
@Component
public interface OrderTypeRoleMapper extends BaseMapper<OrderTypeRole> {

    List<OrderTypeRoleAndOrderTypeVO> findRolePermission(@Param(value = "role_id") Long role_id);

    List<OrderTypeRoleAndOrderTypeVO> findStaffPermission(@Param(value = "staff_id") Long staff_id);
}
