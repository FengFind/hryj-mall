package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.order.OrderType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 叶方宇
 * @className: OrderTypeRoleMapper
 * @description:
 * @create 2018/9/10 0010 16:40
 **/
@Component
public interface OrderTypeMapper extends BaseMapper<OrderType> {
    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 查询所有orderType
     * @param:
     * @return
     * @create 2018-09-19 10:37
     **/
    ArrayList<OrderType> findOrderTypeList();
}
