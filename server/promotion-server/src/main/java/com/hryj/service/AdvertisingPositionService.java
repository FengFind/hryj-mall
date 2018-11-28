package com.hryj.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.cache.LoginCache;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.promotion.ActivityInfo;
import com.hryj.entity.bo.promotion.AdvertisingJumpConfig;
import com.hryj.entity.bo.promotion.AdvertisingPosition;
import com.hryj.entity.bo.promotion.AdvertisingScopeItem;
import com.hryj.entity.bo.staff.store.StoreInfo;
import com.hryj.entity.bo.staff.warehouse.WarehouseInfo;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.promotion.activity.request.AdvertisingPositionIdRequestVO;
import com.hryj.entity.vo.promotion.activity.request.AdvertisingPositionScopeIdListRequestVO;
import com.hryj.entity.vo.promotion.activity.request.AdvertisingPositionScopeIdRequestVO;
import com.hryj.entity.vo.promotion.advertisingposition.request.*;
import com.hryj.entity.vo.promotion.advertisingposition.response.*;
import com.hryj.entity.vo.staff.dept.response.DeptGroupResponseVO;
import com.hryj.entity.vo.staff.team.StaffStoreWhVO;
import com.hryj.entity.vo.staff.user.StaffAdminLoginVO;
import com.hryj.entity.vo.user.UserLoginVO;
import com.hryj.entity.vo.user.UserPartyVO;
import com.hryj.entity.vo.user.UserServiceRangeVO;
import com.hryj.exception.ServerException;
import com.hryj.feign.DeptFeignClient;
import com.hryj.mapper.*;
import com.hryj.service.util.ServiceInvoker;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author 汪豪
 * @className: AdvertisingPositionService
 * @description: 广告相关Service
 * @create 2018/7/9 0009 19:07
 **/
@Slf4j
@Service
public class AdvertisingPositionService extends ServiceImpl<AdvertisingPositionMapper,AdvertisingPosition> {

    @Autowired
    private AdvertisingPositionMapper advertisingPositionMapper;

    @Autowired
    private AdvertisingJumpConfigMapper advertisingJumpConfigMapper;

    @Autowired
    private AdvertisingScopeItemMapper advertisingScopeItemMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private PStoreInfoMapper pStoreInfoMapper;

    @Autowired
    private PWarehouseInfoMapper pWarehouseInfoMapper;

    @Autowired
    private DeptFeignClient deptFeignClient;
    /**
     * @author 汪豪
     * @methodName: searchAdvertisingPositionPage
     * @methodDesc: 分页查询广告位
     * @description:
     * @param: [searchAdvertisingPositionRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.promotion.advertisingposition.response.AdvertisingPositionItemResponseVO>>
     * @create 2018-06-30 9:23
     **/
    public Result<PageResponseVO<AdvertisingPositionItemResponseVO>> searchAdvertisingPositionPage(SearchAdvertisingPositionRequestVO searchAdvertisingPositionRequestVO) {
        StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(searchAdvertisingPositionRequestVO.getLogin_token());
        if(staffAdminLoginVO == null){
            return new Result<>(CodeEnum.FAIL_BUSINESS,"获取用户登陆信息失败, token=" + searchAdvertisingPositionRequestVO.getLogin_token());
        }
        StaffStoreWhVO staffStoreWhVO;
        try {
            staffStoreWhVO = ServiceInvoker.findStaffStoreWhVO(deptFeignClient, staffAdminLoginVO.getStaff_id());
        }catch (ServerException e) {
            log.error("分页查询促销活动-调用接口获取员工权限下门店和仓库失败", e);
            return new Result<>(CodeEnum.FAIL_SERVER, "获取员工权限下门店和仓库失败");
        }
        if(staffStoreWhVO == null){
            return new Result<>(CodeEnum.FAIL_BUSINESS,"员工权限下门店和仓库为空");
        }

        List<String> s_w_id_list = new ArrayList<>();
        if(StrUtil.isNotEmpty(staffStoreWhVO.getStoreIdList())){
            log.info("门店id:"+staffStoreWhVO.getStoreIdList());
            List<String> store_id = Arrays.asList(staffStoreWhVO.getStoreIdList().split(","));
            s_w_id_list.addAll(store_id);
        }
        if(StrUtil.isNotEmpty(staffStoreWhVO.getWhIdList())){
            log.info("仓库id:"+staffStoreWhVO.getWhIdList());
            List<String> wh_id = Arrays.asList(staffStoreWhVO.getWhIdList().split(","));
            s_w_id_list.addAll(wh_id);
        }
        searchAdvertisingPositionRequestVO.setS_w_id_list(s_w_id_list);
        Date currDate = new Date();
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.lt("end_date",currDate);
        wrapper.eq("advertising_status",1);
        List<AdvertisingPosition> advertisingList = advertisingPositionMapper.selectList(wrapper);
        if(CollectionUtil.isNotEmpty(advertisingList)){
            for (AdvertisingPosition advertisingPosition : advertisingList) {
                AdvertisingPosition updateAdvertising = new AdvertisingPosition();
                updateAdvertising.setId(advertisingPosition.getId());
                updateAdvertising.setAdvertising_status(0);
                advertisingPositionMapper.updateById(updateAdvertising);
            }
        }
        log.info("分页查询广告位信息:searchAdvertisingPositionRequestVO====="+JSON.toJSONString(searchAdvertisingPositionRequestVO));
        Page page = new Page(searchAdvertisingPositionRequestVO.getPage_num(),searchAdvertisingPositionRequestVO.getPage_size());
        List<AdvertisingPositionItemResponseVO> records = advertisingPositionMapper.getAdvertisingPositionPageByCondition(searchAdvertisingPositionRequestVO,page);


        PageResponseVO pageResponseVO = new PageResponseVO();
        pageResponseVO.setTotal_page(page.getPages());
        pageResponseVO.setTotal_count(page.getTotal());
        pageResponseVO.setRecords(records);
        log.info("分页查询广告位信息:result====="+JSON.toJSONString(pageResponseVO));
        return new Result<>(CodeEnum.SUCCESS,pageResponseVO);
    }

    /**
     * @author 汪豪
     * @methodName: saveCreateAdvertisingPosition
     * @methodDesc: 保存创建一个新的广告位
     * @description: 新增的广告位状态为停用， 必须审核通过后才启用
     * @param: [advertisingPositionRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-27 22:56
     **/
    @Transactional
    public Result saveCreateAdvertisingPosition(AdvertisingPositionRequestVO advertisingPositionRequestVO) {
        log.info("保存创建一个新的广告位:advertisingPositionRequestVO====="+JSON.toJSONString(advertisingPositionRequestVO));
        if(StrUtil.isEmpty(advertisingPositionRequestVO.getAdvertising_name())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"广告位名称不能为空");
        }
        if(StrUtil.isEmpty(advertisingPositionRequestVO.getStart_date())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"展示开始时间不能为空");
        }
        if(StrUtil.isEmpty(advertisingPositionRequestVO.getEnd_date())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"展示结束时间不能为空");
        }
        if(StrUtil.isEmpty(advertisingPositionRequestVO.getAdvertising_image())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"广告位图片URL不能为空");
        }
        if(StrUtil.isEmpty(advertisingPositionRequestVO.getAdvertising_scope())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"广告位应用范围不能为空");
        }
        if(StrUtil.isEmpty(advertisingPositionRequestVO.getJump_type())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"跳转类型不能为空");
        }
        if("01".equals(advertisingPositionRequestVO.getJump_type()) && StrUtil.isEmpty(advertisingPositionRequestVO.getJump_value())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"跳转URL不能为空");
        }
        if("03".equals(advertisingPositionRequestVO.getJump_type()) && StrUtil.isEmpty(advertisingPositionRequestVO.getJump_value())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"请选择跳转活动");
        }
        if(advertisingPositionRequestVO.getParty_list() == null || advertisingPositionRequestVO.getParty_list().size() == 0){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"广告位的应用范围，门店或仓库集合数据");
        }
        if("01".equals(advertisingPositionRequestVO.getAdvertising_scope())){
            for (AdvertisingPositionScopeRequestVO advertisingPositionScopeRequestVO : advertisingPositionRequestVO.getParty_list() ) {
                WarehouseInfo warehouseInfo = pWarehouseInfoMapper.selectById(advertisingPositionScopeRequestVO.getParty_id());
                if(warehouseInfo == null){
                    return new Result<>(CodeEnum.FAIL_BUSINESS,"id为"+advertisingPositionScopeRequestVO.getParty_id()+"的仓库不存在");
                }
            }
        }else{
            for (AdvertisingPositionScopeRequestVO advertisingPositionScopeRequestVO : advertisingPositionRequestVO.getParty_list() ) {
                StoreInfo storeInfo = pStoreInfoMapper.selectById(advertisingPositionScopeRequestVO.getParty_id());
                if(storeInfo == null){
                    return new Result<>(CodeEnum.FAIL_BUSINESS,"id为"+advertisingPositionScopeRequestVO.getParty_id()+"的门店不存在");
                }
            }
        }
        //广告位基本信息
        AdvertisingPosition advertisingPosition = new AdvertisingPosition();
        advertisingPosition.setAdvertising_name(advertisingPositionRequestVO.getAdvertising_name());
        advertisingPosition.setStart_date(DateUtil.parse(advertisingPositionRequestVO.getStart_date(),"yyyy-MM-dd HH:mm:ss"));
        advertisingPosition.setEnd_date(DateUtil.parse(advertisingPositionRequestVO.getEnd_date(),"yyyy-MM-dd HH:mm:ss"));
        advertisingPosition.setAdvertising_image(advertisingPositionRequestVO.getAdvertising_image());
        advertisingPosition.setAdvertising_scope(advertisingPositionRequestVO.getAdvertising_scope());
        if(StrUtil.isNotEmpty(advertisingPositionRequestVO.getAdvertising_type())){
            advertisingPosition.setAdvertising_type(advertisingPositionRequestVO.getAdvertising_type());
        }else{
            advertisingPosition.setAdvertising_type("01");
        }

        StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(advertisingPositionRequestVO.getLogin_token());
        if(staffAdminLoginVO == null){
            return new Result<>(CodeEnum.FAIL_TOKEN_INVALID,"获取登录信息失败token："+advertisingPositionRequestVO.getLogin_token());
        }
        log.info("保存创建一个新的广告位:操作人====="+JSON.toJSONString(staffAdminLoginVO));
        advertisingPosition.setOperator_id(staffAdminLoginVO.getStaff_id());
        advertisingPosition.setOperator_name(staffAdminLoginVO.getStaff_name());
        advertisingPositionMapper.insert(advertisingPosition);
        //广告位跳转信息
        AdvertisingJumpConfig jumpConfig = new AdvertisingJumpConfig();
        jumpConfig.setAdvertising_id(advertisingPosition.getId());
        jumpConfig.setJump_type(advertisingPositionRequestVO.getJump_type());
        jumpConfig.setJump_value(advertisingPositionRequestVO.getJump_value());
        advertisingJumpConfigMapper.insert(jumpConfig);
        //广告位范围
        for(AdvertisingPositionScopeRequestVO vo : advertisingPositionRequestVO.getParty_list()){
            AdvertisingScopeItem scopeItem = new AdvertisingScopeItem();
            scopeItem.setAdvertising_id(advertisingPosition.getId());
            scopeItem.setParty_id(vo.getParty_id());
            if(StrUtil.isNotEmpty(vo.getStart_date())){
                scopeItem.setStart_date(DateUtil.parse(vo.getStart_date(),"yyyy-MM-dd HH:mm:ss"));
            }
            if(StrUtil.isNotEmpty(vo.getEnd_date())){
                scopeItem.setEnd_date(DateUtil.parse(vo.getEnd_date(),"yyyy-MM-dd HH:mm:ss"));
            }
            if(StrUtil.isNotEmpty(vo.getTop_flag())){
                scopeItem.setTop_flag(Integer.parseInt(vo.getTop_flag()));
            }else{
                scopeItem.setTop_flag(0);//默认不置顶
            }
            advertisingScopeItemMapper.insert(scopeItem);
        }

        return new Result<>(CodeEnum.SUCCESS);
    }

    /**
     * @author 汪豪
     * @methodName: updateAdvertisingPosition
     * @methodDesc: 保存修改一个广告位
     * @description: 广告位的应用范围（门店与仓库）会先删除后新增
     * @param: [advertisingPositionRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-27 23:01
     **/
    @Transactional
    public Result updateAdvertisingPosition(AdvertisingPositionRequestVO advertisingPositionRequestVO) {
        log.info("保存修改一个广告位:advertisingPositionRequestVO====="+JSON.toJSONString(advertisingPositionRequestVO));
        if(advertisingPositionRequestVO.getAdvertising_position_id() == null || advertisingPositionRequestVO.getAdvertising_position_id() <= 0L){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"广告位id不能为空");
        }
        if(StrUtil.isEmpty(advertisingPositionRequestVO.getAdvertising_name())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"广告位名称不能为空");
        }
        if(StrUtil.isEmpty(advertisingPositionRequestVO.getStart_date())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"展示开始时间不能为空");
        }
        if(StrUtil.isEmpty(advertisingPositionRequestVO.getEnd_date())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"展示结束时间不能为空");
        }
        if(StrUtil.isEmpty(advertisingPositionRequestVO.getAdvertising_image())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"广告位图片URL不能为空");
        }
        if(StrUtil.isEmpty(advertisingPositionRequestVO.getAdvertising_scope())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"广告位应用范围不能为空");
        }
        if(StrUtil.isEmpty(advertisingPositionRequestVO.getJump_type())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"跳转类型不能为空");
        }
        if("01".equals(advertisingPositionRequestVO.getJump_type()) && StrUtil.isEmpty(advertisingPositionRequestVO.getJump_value())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"跳转URL不能为空");
        }
        if("03".equals(advertisingPositionRequestVO.getJump_type()) && StrUtil.isEmpty(advertisingPositionRequestVO.getJump_value())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"请选择跳转活动");
        }
        if(CollectionUtil.isNotEmpty(advertisingPositionRequestVO.getParty_list())){
            if("01".equals(advertisingPositionRequestVO.getAdvertising_scope())){
                for (AdvertisingPositionScopeRequestVO advertisingPositionScopeRequestVO : advertisingPositionRequestVO.getParty_list() ) {
                    WarehouseInfo warehouseInfo = pWarehouseInfoMapper.selectById(advertisingPositionScopeRequestVO.getParty_id());
                    if(warehouseInfo == null){
                        return new Result<>(CodeEnum.FAIL_BUSINESS,"id为"+advertisingPositionScopeRequestVO.getParty_id()+"的仓库不存在");
                    }
                }
            }else{
                for (AdvertisingPositionScopeRequestVO advertisingPositionScopeRequestVO : advertisingPositionRequestVO.getParty_list() ) {
                    StoreInfo storeInfo = pStoreInfoMapper.selectById(advertisingPositionScopeRequestVO.getParty_id());
                    if(storeInfo == null){
                        return new Result<>(CodeEnum.FAIL_BUSINESS,"id为"+advertisingPositionScopeRequestVO.getParty_id()+"的门店不存在");
                    }
                }
            }
        }
        //修改广告位基本信息
        AdvertisingPosition advertisingPosition = advertisingPositionMapper.selectById(advertisingPositionRequestVO.getAdvertising_position_id());
        advertisingPosition.setAdvertising_name(advertisingPositionRequestVO.getAdvertising_name());
        advertisingPosition.setStart_date(DateUtil.parse(advertisingPositionRequestVO.getStart_date(),"yyyy-MM-dd HH:mm:ss"));
        advertisingPosition.setEnd_date(DateUtil.parse(advertisingPositionRequestVO.getEnd_date(),"yyyy-MM-dd HH:mm:ss"));
        advertisingPosition.setAdvertising_image(advertisingPositionRequestVO.getAdvertising_image());
        advertisingPosition.setAdvertising_scope(advertisingPositionRequestVO.getAdvertising_scope());
        advertisingPosition.setAdvertising_type(advertisingPositionRequestVO.getAdvertising_type());
        advertisingPositionMapper.updateById(advertisingPosition);
        //修改广告位跳转信息
        AdvertisingJumpConfig jumpConfig = new AdvertisingJumpConfig();
        jumpConfig.setJump_type(advertisingPositionRequestVO.getJump_type());
        jumpConfig.setJump_value(advertisingPositionRequestVO.getJump_value());
        EntityWrapper jumpConfigWrapper = new EntityWrapper();
        jumpConfigWrapper.eq("advertising_id",advertisingPosition.getId());
        advertisingJumpConfigMapper.update(jumpConfig,jumpConfigWrapper);
        if(CollectionUtil.isNotEmpty(advertisingPositionRequestVO.getParty_list())){
            //修改广告位活动范围
            EntityWrapper scopeWrapper = new EntityWrapper();
            scopeWrapper.eq("advertising_id",advertisingPosition.getId());
            advertisingScopeItemMapper.delete(scopeWrapper);
            //广告位范围
            for(AdvertisingPositionScopeRequestVO vo : advertisingPositionRequestVO.getParty_list()){
                AdvertisingScopeItem scopeItem = new AdvertisingScopeItem();
                scopeItem.setAdvertising_id(advertisingPosition.getId());
                scopeItem.setParty_id(vo.getParty_id());
                if(StrUtil.isNotEmpty(vo.getStart_date())){
                    scopeItem.setStart_date(DateUtil.parse(vo.getStart_date(),"yyyy-MM-dd HH:mm:ss"));
                }
                if(StrUtil.isNotEmpty(vo.getEnd_date())){
                    scopeItem.setEnd_date(DateUtil.parse(vo.getEnd_date(),"yyyy-MM-dd HH:mm:ss"));
                }
                if(StrUtil.isNotEmpty(vo.getTop_flag())){
                    scopeItem.setTop_flag(Integer.parseInt(vo.getTop_flag()));
                }else{
                    scopeItem.setTop_flag(0);//默认不置顶
                }
                advertisingScopeItemMapper.insert(scopeItem);
            }
        }
        return new Result<>(CodeEnum.SUCCESS);
    }

    /**
     * @author 汪豪
     * @methodName: enableAdvertisingPosition
     * @methodDesc: 启用一个广告位
     * @description:
     * @param: [advertisingPositionIdRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-27 23:00
     **/
    @Transactional
    public Result enableAdvertisingPosition(AdvertisingPositionIdRequestVO advertisingPositionIdRequestVO) {
        if(advertisingPositionIdRequestVO.getAdvertising_position_id() == null || advertisingPositionIdRequestVO.getAdvertising_position_id() <= 0L){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"广告位id不能为空");
        }
        AdvertisingPosition advertisingPosition = new AdvertisingPosition();
        advertisingPosition.setId(advertisingPositionIdRequestVO.getAdvertising_position_id());
        advertisingPosition.setAdvertising_status(1);//广告位状态:1-启用,0-禁用
        advertisingPositionMapper.updateById(advertisingPosition);
        return new Result<>(CodeEnum.SUCCESS);
    }

    /**
     * @author 汪豪
     * @methodName: disableAdvertisingPosition
     * @methodDesc: 停用一个广告位
     * @description:
     * @param: [advertisingPositionIdRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-27 23:00
     **/
    @Transactional
    public Result disableAdvertisingPosition(AdvertisingPositionIdRequestVO advertisingPositionIdRequestVO) {
        if(advertisingPositionIdRequestVO.getAdvertising_position_id() == null || advertisingPositionIdRequestVO.getAdvertising_position_id() <= 0L){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"广告位id不能为空");
        }
        AdvertisingPosition advertisingPosition = new AdvertisingPosition();
        advertisingPosition.setId(advertisingPositionIdRequestVO.getAdvertising_position_id());
        advertisingPosition.setAdvertising_status(0);//广告位状态:1-启用,0-禁用
        advertisingPositionMapper.updateById(advertisingPosition);
        return new Result<>(CodeEnum.SUCCESS);
    }

    /**
     * @author 汪豪
     * @methodName: deleteOnePartyFromAdvertisingPositionScope
     * @methodDesc: 从广告位的应用范围中删除一个门店或仓库
     * @description:
     * @param: [advertisingPositionScopeIdRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-27 23:05
     **/
    @Transactional
    public Result deleteOnePartyFromAdvertisingPositionScope(AdvertisingPositionScopeIdRequestVO advertisingPositionScopeIdRequestVO) {
        if(advertisingPositionScopeIdRequestVO.getAdvertising_position_scope_id() == null || advertisingPositionScopeIdRequestVO.getAdvertising_position_scope_id() <= 0L){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"广告位范围id不能为空");
        }
        advertisingScopeItemMapper.deleteById(advertisingPositionScopeIdRequestVO.getAdvertising_position_scope_id());
        return new Result<>(CodeEnum.SUCCESS);
    }

    /**
     * @author 汪豪
     * @methodName: topOneAdvertisingPositionScopeItem
     * @methodDesc: 置顶一个广告位的应用范围条目
     * @description: 就是将某个门店或仓库的这个广告位置顶
     * @param: [advertisingPositionScopeIdRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-28 9:15
     **/
    @Transactional
    public Result topOneAdvertisingPositionScopeItem(AdvertisingPositionScopeIdRequestVO advertisingPositionScopeIdRequestVO) {
        if(advertisingPositionScopeIdRequestVO.getAdvertising_position_scope_id() == null || advertisingPositionScopeIdRequestVO.getAdvertising_position_scope_id() <= 0L){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"广告位范围id不能为空");
        }
        AdvertisingScopeItem scopeItem = new AdvertisingScopeItem();
        scopeItem.setId(advertisingPositionScopeIdRequestVO.getAdvertising_position_scope_id());
        scopeItem.setTop_flag(1);
        advertisingScopeItemMapper.updateById(scopeItem);
        return new Result<>(CodeEnum.SUCCESS);
    }

    @Transactional
    public Result topManyAdvertisingPositionScopeItem(AdvertisingPositionScopeIdListRequestVO advertisingPositionScopeIdListRequestVO) {
        if(CollectionUtil.isEmpty(advertisingPositionScopeIdListRequestVO.getAdvertising_scope_id_list())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"广告位范围id集合不能为空");
        }
        for (Long id : advertisingPositionScopeIdListRequestVO.getAdvertising_scope_id_list()) {
            AdvertisingScopeItem scopeItem = new AdvertisingScopeItem();
            scopeItem.setId(id);
            scopeItem.setTop_flag(1);
            advertisingScopeItemMapper.updateById(scopeItem);
        }
        return new Result<>(CodeEnum.SUCCESS);
    }

    /**
     * @author 汪豪
     * @methodName: untopOneAdvertisingPositionScopeItem
     * @methodDesc: 取消置顶一个广告位的应用范围条目
     * @description: 就是将某个门店或仓库的这个广告位取消置顶
     * @param: [advertisingPositionScopeIdRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-28 9:16
     **/
    @Transactional
    public Result untopOneAdvertisingPositionScopeItem(AdvertisingPositionScopeIdRequestVO advertisingPositionScopeIdRequestVO) {
        if(advertisingPositionScopeIdRequestVO.getAdvertising_position_scope_id() == null || advertisingPositionScopeIdRequestVO.getAdvertising_position_scope_id() <= 0L){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"广告位范围id不能为空");
        }
        AdvertisingScopeItem scopeItem = new AdvertisingScopeItem();
        scopeItem.setId(advertisingPositionScopeIdRequestVO.getAdvertising_position_scope_id());
        scopeItem.setTop_flag(0);
        advertisingScopeItemMapper.updateById(scopeItem);
        return new Result<>(CodeEnum.SUCCESS);
    }

    /**
     * @author 汪豪
     * @methodName: findAdvertisingPosition
     * @methodDesc: APP端加载广告位
     * @description:
     * @param: [requestVO]
     * @return com.hryj.common.Result<java.util.List<com.hryj.entity.vo.promotion.advertisingposition.response.AppAdvertisingPositionResponseVO>>
     * @create 2018-06-29 11:13
     **/
    public Result<PageResponseVO<AppAdvertisingPositionResponseVO>> findAdvertisingPosition(RequestVO requestVO) {
        UserLoginVO userLoginVO = LoginCache.getUserLoginVO(requestVO.getLogin_token());
        if(userLoginVO == null){
            return new Result<>(CodeEnum.FAIL_BUSINESS, "获取用户登陆信息失败, token=" + requestVO.getLogin_token());
        }
        log.info("APP端加载广告位-登录token=====" + requestVO.getLogin_token());
        log.info("APP端加载广告位-当前登录用户信息:userLoginVO=====" + JSON.toJSONString(userLoginVO));
        //获取覆盖用户的门店仓库 （从缓存获取）
        List<UserPartyVO> storeList = userLoginVO.getStoreList();
        log.info("APP端加载广告位-用户周围门店(缓存):storeList=====" + JSON.toJSONString(storeList));
        UserPartyVO wh = userLoginVO.getWarehouse();
        log.info("APP端加载广告位-用户周围仓库(缓存):whList=====" + JSON.toJSONString(wh));

        boolean need_to_check = true;
        //如果从缓存获取不到，调拉口获取
        if(CollectionUtil.isEmpty(storeList) && wh == null){
            UserServiceRangeVO userServiceRangeVO;
            try {
                userServiceRangeVO = ServiceInvoker.getUserServiceRange(deptFeignClient, userLoginVO.getPoi_id(), userLoginVO.getCity_code());
            } catch (ServerException e) {
                log.error("APP端查询推荐位商品-调用接口获取覆盖用户的当事组织异常", e);
                return new Result<>(CodeEnum.FAIL_SERVER, "获取覆盖用户的门店和仓库失败");
            }
            //从缓存 和 接口都获取不到，直接返回
            if (userServiceRangeVO == null || (UtilValidate.isEmpty(userServiceRangeVO.getStoreList()) && userServiceRangeVO.getWarehouse() == null)) {
                log.info("没有覆盖到当前用户的门店和仓库");
                return new Result<>(CodeEnum.FAIL_BUSINESS, "没有覆盖到当前用户的门店和仓库");
            }
            need_to_check = false;
            storeList = userServiceRangeVO.getStoreList();
            wh = userServiceRangeVO.getWarehouse();
            log.info("APP端加载广告位-用户周围门店(接口):storeList=====" + JSON.toJSONString(storeList));
            log.info("APP端加载广告位-用户周围仓库(接口):whList=====" + JSON.toJSONString(wh));
        }

        List<AppAdvertisingPositionResponseVO> appResponseVO = new ArrayList<>();
        String currTime = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        if(CollectionUtil.isNotEmpty(storeList)){
            //根据用户位置到门店的距离从近到远排序
            storeList.sort(Comparator.comparing(UserPartyVO::getDistance));
            log.info("APP端加载广告位-根据用户位置到门店的距离从近到远排序:storeList=====" + JSON.toJSONString(storeList));
            for (UserPartyVO userPartyVO : storeList) {
                if(need_to_check){
                    //判断 是否需要对门店和仓库的状态进行验证 （调用 接口返回的不需要验证）
                    List<Long> store_id_list = new ArrayList<>();
                    store_id_list.add(userPartyVO.getParty_id());
                    ListResponseVO<DeptGroupResponseVO> partyBaseInfo = ServiceInvoker.getPartyBaseInfo(deptFeignClient, store_id_list);
                    DeptGroupResponseVO deptGroupResponseVO = partyBaseInfo.getRecords().get(0);
                    if(deptGroupResponseVO != null && deptGroupResponseVO.getDept_status() != null && deptGroupResponseVO.getDept_status() == 1){
                        List<AppAdvertisingPositionResponseVO> storeResponseVO =  advertisingPositionMapper.getAdvertisingPositionJumpDataForApp(deptGroupResponseVO.getDept_id(),currTime);
                        if(CollectionUtil.isNotEmpty(storeResponseVO)){
                            appResponseVO.addAll(storeResponseVO);
                        }
                    }
                }else{
                    List<AppAdvertisingPositionResponseVO> storeResponseVO =  advertisingPositionMapper.getAdvertisingPositionJumpDataForApp(userPartyVO.getParty_id(),currTime);
                    appResponseVO.addAll(storeResponseVO);
                    if(CollectionUtil.isNotEmpty(storeResponseVO)){
                        appResponseVO.addAll(storeResponseVO);
                    }
                }

            }
        }

        if(need_to_check){
            if(wh != null){
                List<Long> wh_id_list = new ArrayList<>();
                wh_id_list.add(wh.getParty_id());
                ListResponseVO<DeptGroupResponseVO> partyBaseInfo = ServiceInvoker.getPartyBaseInfo(deptFeignClient, wh_id_list);
                DeptGroupResponseVO deptGroupResponseVO = partyBaseInfo.getRecords().get(0);
                if(deptGroupResponseVO != null && deptGroupResponseVO.getDept_status() != null && deptGroupResponseVO.getDept_status() == 1){
                    List<AppAdvertisingPositionResponseVO> warehouseResponseVO =  advertisingPositionMapper.getAdvertisingPositionJumpDataForApp(deptGroupResponseVO.getDept_id(),currTime);
                    if(CollectionUtil.isNotEmpty(warehouseResponseVO)){
                        appResponseVO.addAll(warehouseResponseVO);
                    }
                }
            }
        }else{
            if(wh != null){
                List<AppAdvertisingPositionResponseVO> warehouseResponseVO = advertisingPositionMapper.getAdvertisingPositionJumpDataForApp(wh.getParty_id(), currTime);
                if(CollectionUtil.isNotEmpty(warehouseResponseVO)){
                    appResponseVO.addAll(warehouseResponseVO);
                }
            }
        }
        //ArrayList<AppAdvertisingPositionResponseVO> records = null;
        ArrayList<Long> advertising_id_list = new ArrayList<>();
        for (int i = 0; i < appResponseVO.size(); i++) {
            if(advertising_id_list.contains(appResponseVO.get(i).getAdvertising_id())){
                appResponseVO.remove(i);
                i--;
            }else{
                advertising_id_list.add(appResponseVO.get(i).getAdvertising_id());
            }
        }
        /*if(CollectionUtil.isNotEmpty(appResponseVO)){
            records = appResponseVO.stream().collect(
                    Collectors.collectingAndThen(
                            Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(AppAdvertisingPositionResponseVO::getAdvertising_id))), ArrayList::new)
            );
        }*/
        if(CollectionUtil.isNotEmpty(appResponseVO)){
            if(appResponseVO.size() > 5){
                int size = appResponseVO.size() - 1;
                for (int i = size; i >= 5; i--){
                    appResponseVO.remove(i);
                }
            }
            for (AppAdvertisingPositionResponseVO vo : appResponseVO) {
                if("03".equals(vo.getJump_type())){
                    ActivityInfo activityInfo = activityMapper.selectById(Long.parseLong(vo.getJump_value()));
                    vo.setJump_value(activityInfo.getTemplete_data()+"?activity_id="+vo.getJump_value()+"&login_token="+requestVO.getLogin_token());
                }
            }
        }
        PageResponseVO pageResponseVO = new PageResponseVO();
        pageResponseVO.setRecords(appResponseVO);
        log.info("APP端加载广告位-result=====" + JSON.toJSONString(appResponseVO));
        return new Result<>(CodeEnum.SUCCESS,pageResponseVO);
    }

    /**
     * @author 汪豪
     * @methodName: getAdvertisingPosition
     * @methodDesc:  根据广告ID返回广告详细信息
     * @description: include_party是否返回参与门店或仓库，true返回，false不返回， include_jump_config是否返回跳转配置信息：true返回，false不返回
     * @param: [advertisingPositionDetailRequestVO]
     * @return Result<AdvertisingPositionDataResponseVO>
     * @create 2018-07-05 21:23
     **/
    public Result<AdvertisingPositionDataResponseVO> getAdvertisingPosition(AdvertisingPositionDetailRequestVO advertisingPositionDetailRequestVO) {
        log.info("根据广告ID返回广告详细信息:advertisingPositionDetailRequestVO====="+JSON.toJSONString(advertisingPositionDetailRequestVO));
        if(advertisingPositionDetailRequestVO.getAdvertising_id() == null || advertisingPositionDetailRequestVO.getAdvertising_id() <= 0L){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"广告位id不能为空");
        }
        //广告位基本信息
        AdvertisingPosition advertisingPosition = advertisingPositionMapper.selectById(advertisingPositionDetailRequestVO.getAdvertising_id());
        if(advertisingPosition == null){
            return new Result<>(CodeEnum.FAIL_BUSINESS,"id为"+advertisingPositionDetailRequestVO.getAdvertising_id()+"的广告位不存在");
        }
        AdvertisingPositionDataResponseVO responseVO = new AdvertisingPositionDataResponseVO();
        responseVO.setAdvertising_id(advertisingPosition.getId());
        responseVO.setAdvertising_name(advertisingPosition.getAdvertising_name());
        responseVO.setAdvertising_scope(advertisingPosition.getAdvertising_scope());
        responseVO.setStart_date(DateUtil.format(advertisingPosition.getStart_date(),"yyyy-MM-dd HH:mm:ss"));
        responseVO.setEnd_date(DateUtil.format(advertisingPosition.getEnd_date(),"yyyy-MM-dd HH:mm:ss"));
        responseVO.setAdvertising_image(advertisingPosition.getAdvertising_image());
        //广告位跳转信息
        if(advertisingPositionDetailRequestVO.getInclude_jump_config()){
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.eq("advertising_id",advertisingPosition.getId());
            List<AdvertisingJumpConfig> jumpConfigs = advertisingJumpConfigMapper.selectList(wrapper);
            if(CollectionUtil.isNotEmpty(jumpConfigs)){
                AdvertisingJumpConfig jumpConfig = jumpConfigs.get(0);
                responseVO.setJump_type(jumpConfig.getJump_type());
                responseVO.setJump_value(jumpConfig.getJump_value());
            }
        }
        //广告位参与门店或仓库
        AdvertisingPositionJoinPartyDataResponseVO joinPartyDataResponseVO = new AdvertisingPositionJoinPartyDataResponseVO();
        if(advertisingPositionDetailRequestVO.getInclude_party()){
            Page page = new Page(joinPartyDataResponseVO.getPage_num(),joinPartyDataResponseVO.getPage_size());
            List<JoinPartyAdvertisingItemResponseVO> party_list = null;
            if("02".equals(advertisingPosition.getAdvertising_scope())){
                party_list = advertisingPositionMapper.getPartyAdvertisingStoreByAdvertisingId(advertisingPosition.getId(),page);
            }else{
                party_list = advertisingPositionMapper.getPartyAdvertisingWarehouseByAdvertisingId(advertisingPosition.getId(),page);
            }
            joinPartyDataResponseVO.setTotal_page(page.getPages());
            joinPartyDataResponseVO.setTotal_count(page.getTotal());
            joinPartyDataResponseVO.setParty_list(party_list);
        }
        responseVO.setJoin_party_data(joinPartyDataResponseVO);
        log.info("根据广告ID返回广告详细信息:result====="+JSON.toJSONString(responseVO));
        return new Result<>(CodeEnum.SUCCESS,responseVO);
    }

    /**
     * @author 汪豪
     * @methodName: searchAdvertisingJoinPartyData
     * @methodDesc: 查询广告位参与门店或仓库信息
     * @description:
     * @param: [searchAdvertisingPartyDataRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.promotion.advertisingposition.response.AdvertisingPositionJoinPartyDataResponseVO>
     * @create 2018-07-17 10:11
     **/
    public Result<AdvertisingPositionJoinPartyDataResponseVO> searchAdvertisingJoinPartyData(SearchAdvertisingPartyDataRequestVO searchAdvertisingPartyDataRequestVO) {
        log.info("查询广告位参与门店或仓库信息:searchAdvertisingPartyDataRequestVO====="+JSON.toJSONString(searchAdvertisingPartyDataRequestVO));
        if(searchAdvertisingPartyDataRequestVO.getAdvertising_id() == null || searchAdvertisingPartyDataRequestVO.getAdvertising_id() <= 0L){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"广告位id不能为空");
        }
        AdvertisingPosition advertisingPosition = advertisingPositionMapper.selectById(searchAdvertisingPartyDataRequestVO.getAdvertising_id());
        Page page = new Page(searchAdvertisingPartyDataRequestVO.getPage_num(),searchAdvertisingPartyDataRequestVO.getPage_size());
        List<JoinPartyAdvertisingItemResponseVO> party_list;
        if("02".equals(advertisingPosition.getAdvertising_scope())){
            party_list = advertisingPositionMapper.getPartyAdvertisingStoreByAdvertisingId(searchAdvertisingPartyDataRequestVO.getAdvertising_id(), page);
        }else{
            party_list = advertisingPositionMapper.getPartyAdvertisingWarehouseByAdvertisingId(searchAdvertisingPartyDataRequestVO.getAdvertising_id(), page);
        }
        AdvertisingPositionJoinPartyDataResponseVO responseVO = new AdvertisingPositionJoinPartyDataResponseVO();
        responseVO.setParty_list(party_list);
        responseVO.setTotal_page(page.getPages());
        responseVO.setTotal_count(page.getTotal());
        log.info("查询广告位参与门店或仓库信息:result====="+JSON.toJSONString(responseVO));
        return new Result<>(CodeEnum.SUCCESS,responseVO);
    }

}
