package com.hryj.controller;

import com.alibaba.fastjson.JSON;
import com.hryj.cache.RedisService;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.feign.ProductFeignClient;
import com.hryj.service.CityAreaService;
import com.hryj.service.CodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author: 李道云
 * @className: CacheController
 * @description: 缓存controller
 * @create 2018/6/18 09:08
 **/
@Slf4j
@RestController
@RequestMapping(value = "/cache")
public class CacheController {

    @Autowired
    private CodeService codeService;

    @Autowired
    private CityAreaService cityAreaService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private ProductFeignClient productFeignClient;

    /**
     * @author 李道云
     * @description: 刷新code缓存
     * @param: []
     * @return com.hryj.common.Result
     * @create 2018-06-23 9:13
     **/
    @PostMapping("/flushCodeCache")
    public Result flushCodeCache(){
        log.info("================刷新code的缓存开始===================");
        codeService.flushCodeCache();
        log.info("================刷新code的缓存结束===================");
        return new Result(CodeEnum.SUCCESS);
    }

    /**
     * @author 李道云
     * @description: 刷新城市缓存
     * @param: []
     * @return com.hryj.common.Result
     * @create 2018-06-23 9:13
     **/
    @PostMapping("/flushCityCache")
    public Result flushCityCache(){
        log.info("================刷新城市的缓存开始===================");
        cityAreaService.flushCityAreaCache();
        log.info("================刷新城市的缓存结束===================");
        return new Result(CodeEnum.SUCCESS);
    }

    /**
     * @author 李道云
     * @description: 查看缓存数据
     * @param: [group_name, key_name]
     * @return com.hryj.common.Result
     * @create 2018-06-23 11:10
     **/
    @PostMapping("/getCache")
    public Result getCache(String group_name, String key_name){
        log.info("查看缓存数据:group_name={},key={}",group_name,key_name);
        String value = redisService.get(group_name,key_name);
        log.info("查看缓存数据:value ={}",value);
        Map map = new HashMap<>();
        map.put(group_name+"."+key_name, value);
        return new Result(CodeEnum.SUCCESS,map);
    }

    /**
     * @author 李道云
     * @description: 设置缓存数据
     * @param: [group_name, key_name, value, exT]
     * @return com.hryj.common.Result
     * @create 2018-06-23 11:14
     **/
    @PostMapping("/setCache")
    public Result setCache(String group_name, String key_name, String value, Integer exT){
        log.info("设置缓存数据:group_name={},key={},exT={}",group_name,key_name);
        redisService.put(group_name,key_name,value,exT);
        return new Result(CodeEnum.SUCCESS);
    }

    /**
     * @author 李道云
     * @methodName: getKeysByGroupName
     * @methodDesc:  根据group_name获取key集合
     * @description:
     * @param: [group_name]
     * @return com.hryj.common.Result
     * @create 2018-08-27 11:53
     **/
    @PostMapping("/getKeysByGroupName")
    public Result getKeysByGroupName(String group_name){
        Set<String> set = redisService.getKeysByGroupName(group_name);
        log.info("keys = " + JSON.toJSONString(set));
        return new Result(CodeEnum.SUCCESS,set);
    }

    /**
     * @author 王光银
     * @methodName: refreshProductBrandCache
     * @methodDesc: 刷新商品品牌缓存
     * @description:
     * @param: []
     * @return com.hryj.common.Result
     * @create 2018-10-08 10:19
     **/
    @PostMapping("/refreshProductBrandCache")
    public Result refreshProductBrandCache(){
        return productFeignClient.refreshProductBrandCache();
    }

    /**
     * @author 王光银
     * @methodName: refreshProductGeoCache
     * @methodDesc: 刷新商品产地缓存
     * @description:
     * @param: []
     * @return com.hryj.common.Result
     * @create 2018-10-08 10:20
     **/
    @PostMapping("/refreshProductGeoCache")
    public Result refreshProductGeoCache(){
        return productFeignClient.refreshProductGeoCache();
    }
}
