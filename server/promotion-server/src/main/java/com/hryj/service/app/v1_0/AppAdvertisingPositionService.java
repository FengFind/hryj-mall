package com.hryj.service.app.v1_0;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.hryj.cache.LoginCache;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.promotion.ActivityInfo;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.promotion.advertisingposition.response.*;
import com.hryj.entity.vo.staff.dept.response.DeptGroupResponseVO;
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

import java.util.*;

/**
 * @author 汪豪
 * @className: AdvertisingPositionService
 * @description: 广告相关Service
 * @create 2018/7/9 0009 19:07
 **/
@Slf4j
@Service("v1.0-AppAdvertisingPositionService")
public class AppAdvertisingPositionService {

    @Autowired
    private AdvertisingPositionMapper advertisingPositionMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private DeptFeignClient deptFeignClient;

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
        log.info("APP端加载广告位-v1_0");
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
}
