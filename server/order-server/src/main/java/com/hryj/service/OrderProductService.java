package com.hryj.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.entity.bo.order.OrderProduct;
import com.hryj.mapper.OrderProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author 白飞
 * @className: OrderProductService
 * @description:
 * @create 2018/9/25 14:29
 **/

@Slf4j
@Service
public class OrderProductService extends ServiceImpl<OrderProductMapper, OrderProduct> {
}
