package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.promotion.IndexNavigationConfig;
import com.hryj.entity.vo.promotion.indexNavigation.response.IndexNavigationResponseVO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 汪豪
 * @className: IndexNavigationMapper
 * @description: 首页导航Mapper
 * @create 2018/8/23 0023 14:49
 **/
@Component
public interface IndexNavigationMapper extends BaseMapper<IndexNavigationConfig> {

    List<IndexNavigationResponseVO> findIndexNavigation();
}
