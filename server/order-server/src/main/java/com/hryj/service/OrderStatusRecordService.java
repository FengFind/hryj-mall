package com.hryj.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.entity.bo.order.OrderStatusRecord;
import com.hryj.mapper.OrderStatusRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author 白飞
 * @className: OrderStatusRecordService
 * @description:
 * @create 2018/9/25 14:11
 **/
@Slf4j
@Service
public class OrderStatusRecordService extends ServiceImpl<OrderStatusRecordMapper, OrderStatusRecord> {
}
