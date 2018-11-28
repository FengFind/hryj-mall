package com.hryj.entity.vo.order.ordertype;

import lombok.Data;

/**
 * @author 叶方宇
 * @className: OrderTypeRoleAndOrderTypeVO
 * @description:
 * @create 2018/9/10 0010 16:54
 **/
@Data
public class OrderTypeRoleAndOrderTypeVO {

    private Long id;

    private Long staff_id;

    private Long role_id;

    private String order_type_id;

    private String description;
}
