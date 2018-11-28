package com.hryj.service.app.v1_0;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.hryj.cache.LoginCache;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.product.PartyProduct;
import com.hryj.entity.bo.promotion.ActivityScopeItem;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.product.response.app.AppProdListItemResponseVO;
import com.hryj.entity.vo.promotion.activity.request.*;
import com.hryj.entity.vo.promotion.activity.response.*;
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


import java.math.BigDecimal;
import java.util.*;

/**
 * @author 汪豪
 * @className: AppActivityService
 * @description:
 * @create 2018/8/15 0015 10:57
 **/
@Slf4j
@Service("v1.0-AppActivityService")
public class AppActivityService {

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ActivityScopeItemMapper activityScopeItemMapper;

    @Autowired
    private PartyProductOtherMapper partyProductMapper;

    @Autowired
    private DeptFeignClient deptFeignClient;


    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.promotion.activity.response.AppPromotionActivityResponseVO>
     * @author 汪豪
     * @methodName: findPromotionActivity
     * @methodDesc: APP端加载促销活动
     * @description: 不分页
     * @param: [requestVO]
     * @create 2018-06-28 19:44
     **/
    public Result<PageResponseVO<AppPromotionActivityResponseVO>> findPromotionActivity(RequestVO requestVO) {
        log.info("APP端加载促销活动-v1_0");
        UserLoginVO userLoginVO = LoginCache.getUserLoginVO(requestVO.getLogin_token());
        if (userLoginVO == null) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "获取用户登陆信息失败, token=" + requestVO.getLogin_token());
        }
        log.info("APP端加载促销活动-用户登录token：" + requestVO.getLogin_token());
        log.info("APP端加载活动的详细信息-当前登录用户信息：" + JSON.toJSONString(userLoginVO));
        //获取覆盖用户的门店仓库 （从缓存获取）
        List<UserPartyVO> storeList = userLoginVO.getStoreList();
        log.info("APP端加载促销活动-用户周围门店（缓存）：" + JSON.toJSONString(storeList));
        UserPartyVO wh = userLoginVO.getWarehouse();
        log.info("APP端加载促销活动-用户周围仓库（缓存）：" + JSON.toJSONString(wh));
        boolean need_to_check = true;
        //如果从缓存获取不到，调拉口获取
        if (CollectionUtil.isEmpty(storeList) && wh == null) {
            UserServiceRangeVO userServiceRangeVO;
            try {
                userServiceRangeVO = ServiceInvoker.getUserServiceRange(deptFeignClient, userLoginVO.getPoi_id(), userLoginVO.getCity_code());
            } catch (ServerException e) {
                log.error("APP端查询推荐位商品-调用接口获取覆盖用户的当事组织异常", e);
                return new Result<>(CodeEnum.FAIL_SERVER, "获取覆盖用户的门店和仓库失败");
            }
            //从缓存 和 接口都获取不到，直接返回
            if (userServiceRangeVO == null || (UtilValidate.isEmpty(userServiceRangeVO.getStoreList()) && userServiceRangeVO.getWarehouse() == null)) {
                return new Result<>(CodeEnum.FAIL_BUSINESS, "没有覆盖到当前用户的门店和仓库");
            }
            need_to_check = false;
            storeList = userServiceRangeVO.getStoreList();
            wh = userServiceRangeVO.getWarehouse();
            log.info("APP端加载促销活动-用户周围门店（接口）：" + JSON.toJSONString(storeList));
            log.info("APP端加载促销活动-用户周围仓库（接口）：" + JSON.toJSONString(wh));
        }

        String currTime = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        List<AppPromotionActivityResponseVO> appResponseVO = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(storeList)) {
            storeList.sort(Comparator.comparing(UserPartyVO::getDistance));
            log.info("APP端加载促销活动-用户位置到门店的距离从近到远排序后：" + JSON.toJSONString(storeList));
            for (UserPartyVO userPartyVO : storeList) {
                //判断 是否需要对门店的状态进行验证 （调用 接口返回的不需要验证）
                if (need_to_check) {
                    List<Long> store_id_list = new ArrayList<>();
                    store_id_list.add(userPartyVO.getParty_id());
                    ListResponseVO<DeptGroupResponseVO> partyBaseInfo = ServiceInvoker.getPartyBaseInfo(deptFeignClient, store_id_list);
                    DeptGroupResponseVO deptGroupResponseVO = partyBaseInfo.getRecords().get(0);
                    if (deptGroupResponseVO.getDept_status() != null && deptGroupResponseVO.getDept_status() == 1) {
                        List<AppPromotionActivityResponseVO> storeResponseVO = activityMapper.getActivityListToApp(userPartyVO.getParty_id(), currTime);
                        if (CollectionUtil.isNotEmpty(storeResponseVO)) {
                            appResponseVO.addAll(storeResponseVO);
                        }
                    }
                } else {
                    List<AppPromotionActivityResponseVO> storeResponseVO = activityMapper.getActivityListToApp(userPartyVO.getParty_id(), currTime);
                    if (CollectionUtil.isNotEmpty(storeResponseVO)) {
                        appResponseVO.addAll(storeResponseVO);
                    }
                }
            }
        }

        //判断 是否需要仓库的状态进行验证 （调用 接口返回的不需要验证）
        if (wh != null) {
            if (need_to_check) {
                List<Long> wh_id_list = new ArrayList<>();
                wh_id_list.add(wh.getParty_id());
                ListResponseVO<DeptGroupResponseVO> partyBaseInfo = ServiceInvoker.getPartyBaseInfo(deptFeignClient, wh_id_list);
                DeptGroupResponseVO deptGroupResponseVO = partyBaseInfo.getRecords().get(0);
                if (deptGroupResponseVO.getDept_status() != null && deptGroupResponseVO.getDept_status() == 1) {
                    List<AppPromotionActivityResponseVO> whResponseVO = activityMapper.getActivityListToApp(wh.getParty_id(), currTime);
                    if (CollectionUtil.isNotEmpty(whResponseVO)) {
                        appResponseVO.addAll(whResponseVO);
                    }
                }
            } else {
                List<AppPromotionActivityResponseVO> whResponseVO = activityMapper.getActivityListToApp(wh.getParty_id(), currTime);
                if (CollectionUtil.isNotEmpty(whResponseVO)) {
                    appResponseVO.addAll(whResponseVO);
                }
            }
        }

        /*ArrayList<Long> activity_id_list = new ArrayList<>();
        for (int i = 0; i < appResponseVO.size(); i++) {
            if(activity_id_list.contains(appResponseVO.get(i).getActivity_id())){
                appResponseVO.remove(i);
                i--;
            }else{
                activity_id_list.add(appResponseVO.get(i).getActivity_id());
            }
        }*/
        /*List<AppPromotionActivityResponseVO> records = null;
        if(CollectionUtil.isNotEmpty(appResponseVO)){
            records = appResponseVO.stream().collect(
                    Collectors.collectingAndThen(
                            Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(AppPromotionActivityResponseVO::getActivity_id))), ArrayList::new));
        }*/
        PageResponseVO pageResponseVO = new PageResponseVO();
        pageResponseVO.setRecords(appResponseVO);
        log.info("APP端加载活动信息:result=====：" + JSON.toJSONString(appResponseVO));
        return new Result<>(CodeEnum.SUCCESS, pageResponseVO);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.promotion.activity.response.AppPromotionActivityDataResponseVO>
     * @author 汪豪
     * @methodName: getPromotionActivityData
     * @methodDesc: APP端加载活动的详细信息
     * @description:
     * @param: [onlyActivityIdRequestVO]
     * @create 2018-06-29 11:52
     **/
    public Result<AppPromotionActivityDataResponseVO> getPromotionActivityData(OnlyActivityIdRequestVO onlyActivityIdRequestVO) {
        log.info("APP端加载活动的详细信息-v1_0");
        if (onlyActivityIdRequestVO.getActivity_id() == null) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "活动id不能为空");
        }
        UserLoginVO userLoginVO = LoginCache.getUserLoginVO(onlyActivityIdRequestVO.getLogin_token());
        if (userLoginVO == null) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "获取用户登陆信息失败, token=" + onlyActivityIdRequestVO.getLogin_token());
        }
        log.info("APP端加载活动的详细信息-当前登录用户信息：" + JSON.toJSONString(userLoginVO));
        //获取覆盖用户的门店仓库 （从缓存获取）
        List<UserPartyVO> storeList = userLoginVO.getStoreList();
        log.info("APP端加载活动的详细信息-用户周围门店（缓存）：" + JSON.toJSONString(storeList));
        UserPartyVO wh = userLoginVO.getWarehouse();
        log.info("APP端加载活动的详细信息-用户周围仓库（缓存）：" + JSON.toJSONString(wh));
        boolean need_to_check = true;
        //如果从缓存获取不到，调拉口获取
        if (CollectionUtil.isEmpty(storeList) && wh == null) {
            UserServiceRangeVO userServiceRangeVO;
            try {
                userServiceRangeVO = ServiceInvoker.getUserServiceRange(deptFeignClient, userLoginVO.getPoi_id(), userLoginVO.getCity_code());
            } catch (ServerException e) {
                log.error("APP端查询推荐位商品-调用接口获取覆盖用户的当事组织异常", e);
                return new Result<>(CodeEnum.FAIL_SERVER, "获取覆盖用户的门店和仓库失败");
            }
            //从缓存 和 接口都获取不到，直接返回
            if (userServiceRangeVO == null || (UtilValidate.isEmpty(userServiceRangeVO.getStoreList()) && userServiceRangeVO.getWarehouse() == null)) {
                return new Result<>(CodeEnum.FAIL_BUSINESS, "没有覆盖到当前用户的门店和仓库");
            }
            need_to_check = false;
            storeList = userServiceRangeVO.getStoreList();
            wh = userServiceRangeVO.getWarehouse();
            log.info("APP端加载活动的详细信息-用户周围门店（接口）：" + JSON.toJSONString(storeList));
            log.info("APP端加载活动的详细信息-用户周围仓库（接口）：" + JSON.toJSONString(wh));
        }
        AppPromotionActivityDataResponseVO promotionActivityData = activityMapper.getActivityDataByActivityIdToApp(onlyActivityIdRequestVO.getActivity_id());
        if (promotionActivityData == null) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "id为" + onlyActivityIdRequestVO.getActivity_id() + "的活动不存在");
        }
        Long startDate = DateUtil.parse(promotionActivityData.getStart_date()).getTime();
        Long endDate = DateUtil.parse(promotionActivityData.getEnd_date()).getTime();
        Long currDate = new Date().getTime();
        if ("0".equals(promotionActivityData.getActivity_status()) || currDate < startDate || currDate > endDate) {
            promotionActivityData.setActivity_status("0");
            log.info("APP端加载活动的详细信息：result=====" + JSON.toJSONString(promotionActivityData));
            return new Result<>(CodeEnum.SUCCESS, promotionActivityData);
        }
        if ("01".equals(promotionActivityData.getActivity_style())) {
            List<AppProdListItemResponseVO> productDataList = activityMapper.getActivityProductDataListToApp(onlyActivityIdRequestVO.getActivity_id());
            if ("02".equals(promotionActivityData.getActivity_scope())) {
                //活动范围为门店
                //判断 是否需要对门店和仓库的状态进行验证 （调用 接口返回的不需要验证）
                if (CollectionUtil.isNotEmpty(storeList)) {
                    //根据用户位置到门店的距离从近到远排序
                    storeList.sort(Comparator.comparing(UserPartyVO::getDistance));
                    log.info("APP端加载活动的详细信息-用户位置到门店的距离从近到远排序后：" + JSON.toJSONString(storeList));
                    if (need_to_check) { //从缓存中取得门店和仓库信息需要验证
                        for (UserPartyVO userPartyVO : storeList) {
                            ActivityScopeItem partyItem = new ActivityScopeItem();
                            partyItem.setActivity_id(onlyActivityIdRequestVO.getActivity_id());
                            partyItem.setParty_id(userPartyVO.getParty_id());
                            ActivityScopeItem scopeItem = activityScopeItemMapper.selectOne(partyItem);
                            if (scopeItem != null) { //验证门店参与本次活动
                                List<Long> store_id_list = new ArrayList<>();
                                store_id_list.add(userPartyVO.getParty_id());
                                ListResponseVO<DeptGroupResponseVO> partyBaseInfo = ServiceInvoker.getPartyBaseInfo(deptFeignClient, store_id_list);
                                DeptGroupResponseVO deptGroupResponseVO = partyBaseInfo.getRecords().get(0);
                                if (deptGroupResponseVO.getDept_status() != null && deptGroupResponseVO.getDept_status() == 1) {
                                    for (AppProdListItemResponseVO vo : productDataList) {
                                        if (vo.getParty_id() == null) {
                                            PartyProduct partyProduct = new PartyProduct();
                                            partyProduct.setParty_id(userPartyVO.getParty_id());
                                            partyProduct.setProduct_id(vo.getProduct_id());
                                            PartyProduct pProduct = partyProductMapper.selectOne(partyProduct);
                                            if (pProduct != null && pProduct.getUp_down_status() == 1 && pProduct.getInventory_quantity() != null && new BigDecimal(pProduct.getInventory_quantity()).compareTo(BigDecimal.ZERO) == 1) {
                                                vo.setParty_id(pProduct.getParty_id());
                                                vo.setSale_price(pProduct.getSale_price().toString());
                                                vo.setInventory_quantity(pProduct.getInventory_quantity().toString());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        for (UserPartyVO userPartyVO : storeList) {
                            ActivityScopeItem partyItem = new ActivityScopeItem();
                            partyItem.setActivity_id(onlyActivityIdRequestVO.getActivity_id());
                            partyItem.setParty_id(userPartyVO.getParty_id());
                            ActivityScopeItem scopeItem = activityScopeItemMapper.selectOne(partyItem);
                            if (scopeItem != null) {
                                for (AppProdListItemResponseVO vo : productDataList) {
                                    if (vo.getParty_id() == null) {
                                        PartyProduct partyProduct = new PartyProduct();
                                        partyProduct.setParty_id(userPartyVO.getParty_id());
                                        partyProduct.setProduct_id(vo.getProduct_id());
                                        PartyProduct pProduct = partyProductMapper.selectOne(partyProduct);
                                        if (pProduct != null && pProduct.getUp_down_status() == 1 && pProduct.getInventory_quantity() != null && new BigDecimal(pProduct.getInventory_quantity()).compareTo(BigDecimal.ZERO) == 1) {
                                            vo.setParty_id(pProduct.getParty_id());
                                            vo.setSale_price(pProduct.getSale_price().toString());
                                            vo.setInventory_quantity(pProduct.getInventory_quantity().toString());
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    return new Result<>(CodeEnum.FAIL_BUSINESS, "没有覆盖到当前用户的门店");
                }
            } else {
                if (wh != null) {
                    //活动范围为仓库
                    for (AppProdListItemResponseVO vo : productDataList) {
                        PartyProduct whProduct = new PartyProduct();
                        whProduct.setParty_id(wh.getParty_id());
                        whProduct.setProduct_id(vo.getProduct_id());
                        PartyProduct wh_prod_item = partyProductMapper.selectOne(whProduct);
                        if (wh_prod_item != null && wh_prod_item.getUp_down_status() == 1 && wh_prod_item.getInventory_quantity() != null && new BigDecimal(wh_prod_item.getInventory_quantity()).compareTo(BigDecimal.ZERO) == 1) {
                            vo.setParty_id(wh_prod_item.getParty_id());
                            vo.setSale_price(wh_prod_item.getSale_price().toString());
                            vo.setInventory_quantity(wh_prod_item.getInventory_quantity().toString());
                        }
                    }
                }
            }

            for (int i = 0; i < productDataList.size(); i++) {
                if (productDataList.get(i).getParty_id() == null || productDataList.get(i).getInventory_quantity() == null || new BigDecimal(productDataList.get(i).getInventory_quantity()).compareTo(BigDecimal.ZERO) == 0 || new BigDecimal(productDataList.get(i).getInventory_quantity()).compareTo(BigDecimal.ZERO) == -1) {
                    productDataList.remove(i);
                    i--;
                }
            }
            promotionActivityData.setProduct_list(productDataList);

        }
        log.info("APP端加载活动的详细信息：result=====" + JSON.toJSONString(promotionActivityData));
        return new Result<>(CodeEnum.SUCCESS, promotionActivityData);
    }
}
