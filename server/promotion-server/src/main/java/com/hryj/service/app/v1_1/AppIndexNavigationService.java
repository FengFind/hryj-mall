package com.hryj.service.app.v1_1;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.promotion.indexNavigation.response.IndexNavigationResponseVO;
import com.hryj.mapper.IndexNavigationMapper;
import com.hryj.service.util.PromotionRedisCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 汪豪
 * @className: AppIndexNavigationService
 * @description: app端首页导航Service
 * @create 2018/8/24 0024 10:36
 **/
@Slf4j
@Service("v1.1-AppIndexNavigationService")
public class AppIndexNavigationService {

    @Autowired
    private IndexNavigationMapper indexNavigationMapper;

    /**
     * @author 汪豪
     * @methodName: showIndexNavigation
     * @methodDesc:
     * @description:
     * @param: []
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.promotion.indexNavigation.response.IndexNavigationResponseVO>>
     * @create 2018-08-23 14:43
     **/
    public Result<ListResponseVO<IndexNavigationResponseVO>> showIndexNavigation() {
        log.info("app端加载导航位");
        //从缓存中获取首页导航信息
        List<IndexNavigationResponseVO> records = PromotionRedisCacheUtil.NavigationCacheUtil.getCacheData();
        //如果从缓存中获取的为空，那从数据库中获取，再将数据放入缓存中
        if(CollectionUtil.isEmpty(records)){
            records = indexNavigationMapper.findIndexNavigation();
            PromotionRedisCacheUtil.NavigationCacheUtil.setCacheData(records);
        }
        ListResponseVO vo  = new ListResponseVO();
        vo.setRecords(records);
        log.info("app端加载导航位：result====="+JSON.toJSONString(records));
        return new Result<>(CodeEnum.SUCCESS,vo);
    }
}
