package com.hryj.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.cache.CacheGroup;
import com.hryj.cache.RedisService;
import com.hryj.entity.bo.order.OrderType;
import com.hryj.mapper.OrderTypeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @author 叶方宇
 * @className: OrderTypeService
 * @description:
 * @create 2018/9/10 0010 16:38
 **/
@Slf4j
@Service
public class OrderTypeService extends ServiceImpl<OrderTypeMapper, OrderType> {

    @Autowired
    private OrderTypeMapper orderTypeMapper;

    @Autowired
    private RedisService redisService;

    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 查询orderType列表
     * @param:
     * @return
     * @create 2018-09-19 10:39
     **/
    public ArrayList<OrderType> findOrderTypeList(){
        return orderTypeMapper.findOrderTypeList();
    }

    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 将list放入redis
     * @param:
     * @return
     * @create 2018-09-19 10:41
     **/
    public ArrayList<OrderType> putOrderTypeToCache(){
        return redisService.setSerialization(findOrderTypeList(),CacheGroup.ORDER_TYPE_GROUP.getValue(),CacheGroup.ORDER_TYPE_GROUP_KEY.getValue());
    }

    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 去缓存拿出list
     * @param:
     * @return
     * @create 2018-09-19 11:52
     **/
    public ArrayList<OrderType> getOrderTypeList(){
        ArrayList<OrderType> list = redisService.getSerialization(CacheGroup.ORDER_TYPE_GROUP.getValue(),CacheGroup.ORDER_TYPE_GROUP_KEY.getValue());
        if(list==null||list.size()<1){
            list = putOrderTypeToCache();
        }
       return list;
    }
}
