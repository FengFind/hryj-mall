package com.hryj.service;

import cn.hutool.core.util.RandomUtil;
import com.hryj.cache.CacheGroup;
import com.hryj.cache.RedisService;
import com.hryj.mapper.OrderSelfPickMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 叶方宇
 * @className: OrderSelfPickServivce
 * @description: 自提信息管理
 * @create 2018/7/11 0011 19:49
 **/
@Slf4j
@Service
public class OrderSelfPickService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private OrderSelfPickMapper orderSelfPickMapper;

    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 生成自提码, 6位随机数
     * @param:
     * @create 2018-07-11 19:55
     **/
    public String createSelfPickCode() {
        log.info("生成自提码createSelfPickCode方法");
        String numstr = RandomUtil.randomNumbers(6);
        if (testSelfPickCode(numstr)) {
            log.info("生成自提码==="+numstr);
            return numstr;
        } else {
            return createSelfPickCode();
        }
    }


    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 判断唯一性, 放入redis，key=value
     * @param:
     * @create 2018-07-11 20:02
     **/
    public boolean testSelfPickCode(String code) {
        Long test = redisService.setnx1(CacheGroup.SELF_PICK_CODE.getValue(), code, code);
        if (test == 1) {
            return true;
        }
        return false;
    }


    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 判断自提码是否存在
     * @param:
     * @create 2018-07-13 11:44
     **/
    public boolean existsSelfPickKey(String key) {
        return redisService.existsKey1(CacheGroup.SELF_PICK_CODE.getValue(), key);
    }

}
