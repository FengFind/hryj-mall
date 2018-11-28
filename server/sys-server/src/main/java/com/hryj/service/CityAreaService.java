package com.hryj.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.cache.CacheGroup;
import com.hryj.cache.RedisService;
import com.hryj.entity.bo.sys.CityArea;
import com.hryj.entity.vo.sys.response.CityAreaVO;
import com.hryj.mapper.CityAreaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 李道云
 * @className: CityAreaService
 * @description: 城市区域Service
 * @create 2018/6/25 17:17
 **/
@Service
public class CityAreaService extends ServiceImpl<CityAreaMapper,CityArea> {

    @Autowired
    private RedisService redisService;

    /**
     * @author 李道云
     * @description: 刷新城市区域缓存
     * @param: []
     * @return void
     * @create 2018-06-25 17:59
     **/
    public void flushCityAreaCache(){
        //刷新城市区域缓存,按级别
        List<CityAreaVO> cityList1 = baseMapper.findCityListByClevel(1);
        redisService.put(CacheGroup.CITY_LEVEL.getValue(),"1",JSON.toJSONString(cityList1),null);
        List<CityAreaVO> cityList2 = baseMapper.findCityListByClevel(2);
        redisService.put(CacheGroup.CITY_LEVEL.getValue(),"2",JSON.toJSONString(cityList2),null);
        List<CityAreaVO> cityList3 = baseMapper.findCityListByClevel(3);
        redisService.put(CacheGroup.CITY_LEVEL.getValue(),"3",JSON.toJSONString(cityList3),null);

        //刷新城市区域缓存,按上下级
        redisService.put(CacheGroup.CITY_CHAILD.getValue(),"S100000",JSON.toJSONString(cityList1),null);
        for (CityAreaVO cityAreaVO1 : cityList1){
            Long city_id1 = cityAreaVO1.getCity_id();
            List<CityAreaVO> child_List2 = baseMapper.findCityListByPid(city_id1);
            redisService.put(CacheGroup.CITY_CHAILD.getValue(),"S"+city_id1,JSON.toJSONString(child_List2),null);
            for(CityAreaVO cityAreaVO2 :child_List2){
                Long city_id2 = cityAreaVO2.getCity_id();
                List<CityAreaVO> child_List3 = baseMapper.findCityListByPid(city_id2);
                redisService.put(CacheGroup.CITY_CHAILD.getValue(),"S"+city_id2,JSON.toJSONString(child_List3),null);
            }
        }
    }
}
