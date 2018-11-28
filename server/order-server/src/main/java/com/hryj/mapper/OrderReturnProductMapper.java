package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.order.ReturnProduct;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 叶方宇
 * @className: OrderReturnProductMapper
 * @description: 订单退货商品信息
 * @create 2018/7/11 0011 21:41
 **/
@Component
public interface OrderReturnProductMapper extends BaseMapper<ReturnProduct> {

    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 根据订单ID 查询订单退货商品信息
     * @param:
     * @return
     * @create 2018-07-11 21:44
     **/
    List<ReturnProduct> getReturnProductList(Long orderId);

}
