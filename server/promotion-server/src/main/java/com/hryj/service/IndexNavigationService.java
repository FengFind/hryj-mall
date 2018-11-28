package com.hryj.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.promotion.IndexNavigationConfig;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.promotion.indexNavigation.request.IndexNavigationListRequestVO;
import com.hryj.entity.vo.promotion.indexNavigation.request.IndexNavigationRequestVO;
import com.hryj.entity.vo.promotion.indexNavigation.response.IndexNavigationResponseVO;
import com.hryj.mapper.IndexNavigationMapper;
import com.hryj.service.util.PromotionRedisCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 汪豪
 * @className: IndexNavigationService
 * @description: 后台管理首页导航Controller
 * @create 2018/8/23 0023 14:52
 **/
@Slf4j
@Service
public class IndexNavigationService extends ServiceImpl<IndexNavigationMapper,IndexNavigationConfig> {

    @Autowired
    private IndexNavigationMapper indexNavigationMapper;

    /**
     * @author 汪豪
     * @methodName: createOrUpdateIndexNavigation
     * @methodDesc:
     * @description:
     * @param: [indexNavigationListRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-08-23 14:15
     **/
    public Result createOrUpdateIndexNavigation(IndexNavigationListRequestVO indexNavigationListRequestVO) {
        log.info("后台管理修改首页导航：requestVO====="+JSON.toJSONString(indexNavigationListRequestVO));
        if (CollectionUtil.isEmpty(indexNavigationListRequestVO.getNavigation_list())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"首页导航配置信息不能为空");
        }
        for (IndexNavigationRequestVO indexNavigationRequestVO : indexNavigationListRequestVO.getNavigation_list()) {
            if(StrUtil.isEmpty(indexNavigationRequestVO.getNavigation_name())){
                return new Result<>(CodeEnum.FAIL_PARAMCHECK,"导航名称不能为空");
            }
            if(StrUtil.isEmpty(indexNavigationRequestVO.getNavigation_icon())){
                return new Result<>(CodeEnum.FAIL_PARAMCHECK,"导航图标不能为空");
            }
            if(StrUtil.isEmpty(indexNavigationRequestVO.getNavigation_link())){
                return new Result<>(CodeEnum.FAIL_PARAMCHECK,"导航链接不能为空");
            }
            if(indexNavigationRequestVO.getSort_num() == null){
                return new Result<>(CodeEnum.FAIL_PARAMCHECK,"位置序号不能为空");
            }
        }
        for (IndexNavigationRequestVO vo : indexNavigationListRequestVO.getNavigation_list()) {
            if(vo.getIndex_navigation_id() != null && vo.getIndex_navigation_id() != 0){
                //更新导航配置
                IndexNavigationConfig navigation = new IndexNavigationConfig();
                navigation.setId(vo.getIndex_navigation_id());
                navigation.setNavigation_name(vo.getNavigation_name());
                navigation.setNavigation_icon(vo.getNavigation_icon());
                navigation.setNavigation_link(vo.getNavigation_link());
                navigation.setSort_num(vo.getSort_num());
                indexNavigationMapper.updateById(navigation);
            }else {
                //新增导航配置
                IndexNavigationConfig navigation = new IndexNavigationConfig();
                navigation.setNavigation_name(vo.getNavigation_name());
                navigation.setNavigation_icon(vo.getNavigation_icon());
                navigation.setNavigation_link(vo.getNavigation_link());
                navigation.setSort_num(vo.getSort_num());
                indexNavigationMapper.insert(navigation);
            }
        }
        //更新导航配置后清缓存
        PromotionRedisCacheUtil.NavigationCacheUtil.cleanNavigationInCache();
        return new Result<>(CodeEnum.SUCCESS);
    }

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
        List<IndexNavigationResponseVO> indexNavigation = indexNavigationMapper.findIndexNavigation();
        ListResponseVO vo  = new ListResponseVO();
        vo.setRecords(indexNavigation);
        log.info("后台管理展示首页导航：result====="+JSON.toJSONString(indexNavigation));
        return new Result<>(CodeEnum.SUCCESS,vo);
    }
}
