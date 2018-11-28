package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.sys.CityArea;
import com.hryj.entity.vo.sys.response.CityAreaVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 李道云
 * @className: CityAreaMapper
 * @description: 城市区域mapper
 * @create 2018/6/25 17:00
 **/
@Component
public interface CityAreaMapper extends BaseMapper<CityArea> {

    /**
     * @author 李道云
     * @description: 根据级别查询城市区域列表
     * @param: clevel
     * @return
     * @create 2018-06-25 17:15
     **/
    List<CityAreaVO> findCityListByClevel(@Param("clevel") Integer clevel);

    /**
     * @author 李道云
     * @description: 据父级id查询城市区域列表
     * @param: pid
     * @return
     * @create 2018-06-25 17:16
     **/
    List<CityAreaVO> findCityListByPid(@Param("pid") Long pid);

}
