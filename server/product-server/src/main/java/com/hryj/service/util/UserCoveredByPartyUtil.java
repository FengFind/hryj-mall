package com.hryj.service.util;

import com.hryj.cache.LoginCache;
import com.hryj.common.BizCodeEnum;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.common.SysCodeEnmu;
import com.hryj.entity.vo.staff.user.StaffAdminLoginVO;
import com.hryj.entity.vo.user.UserLoginVO;
import com.hryj.entity.vo.user.UserPartyVO;
import com.hryj.entity.vo.user.UserServiceRangeVO;
import com.hryj.exception.ServerException;
import com.hryj.feign.PartyFeignClient;
import com.hryj.feign.UserFeignClient;
import com.hryj.service.PartyProductUtilService;
import com.hryj.service.worktask.SetUserDefaultPartyTask;
import com.hryj.threadpool.ThreadPoolUtil;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author 王光银
 * @className: UserCoveredByPartyUtil
 * @description:
 * @create 2018/7/12 0012 14:55
 **/
@Slf4j
public class UserCoveredByPartyUtil {

    /**
     * 获取运营端管理用户可见的门店或仓库ID集合
     * @param client
     * @param token
     * @param force_refresh
     * @param party_type
     * @return
     */
    public static Result<List<Long>> calculateAndGetAdminVisibleParty(PartyFeignClient client, String token, boolean force_refresh, String party_type) {
        if (UtilValidate.isEmpty(token)) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "用户登陆token是空值");
        }
        StaffAdminLoginVO adminLogin = LoginCache.getStaffAdminLoginVO(token);
        if (adminLogin == null) {
            return new Result(CodeEnum.FAIL_BUSINESS, "获取用户信息失败,token=" + token);
        }
        if (force_refresh) {
            //(invoke-api)调用接口获取当前操作用户能够看到的门店与仓库ID
            try {
                IdsHandler idsHandler = APIInvoker.getStaffUserCoveredParty(adminLogin.getStaff_id(), client);
                if (UtilValidate.isEmpty(idsHandler.id_set_one) && UtilValidate.isEmpty(idsHandler.id_set_two)) {
                    return new Result<>(CodeEnum.SUCCESS);
                }
                List<Long> party_id_list = new ArrayList<>((UtilValidate.isEmpty(idsHandler.id_set_one) ? 0 : idsHandler.id_set_one.size())
                        + (UtilValidate.isEmpty(idsHandler.id_set_two) ? 0 : idsHandler.id_set_two.size()));


                if (UtilValidate.isEmpty(party_type)) {
                    //返回所有
                    if (UtilValidate.isNotEmpty(idsHandler.id_set_one)) {
                        party_id_list.addAll(idsHandler.id_set_one);
                    }
                    if (UtilValidate.isNotEmpty(idsHandler.id_set_two)) {
                        party_id_list.addAll(idsHandler.id_set_two);
                    }
                } else if (SysCodeEnmu.DEPTETYPE_01.getCodeValue().equals(party_type)) {
                    //返回门店
                    if (UtilValidate.isNotEmpty(idsHandler.id_set_one)) {
                        party_id_list.addAll(idsHandler.id_set_one);
                    }
                } else if (SysCodeEnmu.DEPTETYPE_01.getCodeValue().equals(party_type)) {
                    //返回仓库
                    if (UtilValidate.isNotEmpty(idsHandler.id_set_two)) {
                        party_id_list.addAll(idsHandler.id_set_two);
                    }
                }
                return new Result(CodeEnum.SUCCESS, party_id_list);
            } catch (Exception e) {
                log.error("com.hryj.service.util.PartyUtil: 调用接口获取组织员工能够看到的门店与仓库异常", e);
                return new Result<>(CodeEnum.FAIL_SERVER, "获取组织员工能够看到的门店与仓库异常");
            }
        }
        String split_char = ",";
        List<Long> party_id_list = new ArrayList<>(20);
        if (UtilValidate.isEmpty(party_type) || SysCodeEnmu.DEPTETYPE_01.getCodeValue().equals(party_type)) {
            if (UtilValidate.isNotEmpty(adminLogin.getStoreIdList())) {
                for (String id : adminLogin.getStoreIdList().split(split_char)) {
                    try {
                        party_id_list.add(Long.valueOf(id));
                    } catch (NumberFormatException e) {
                        log.error("获取当前用户能够看到的门店 - 门店ID数据格式错误:" + id, e);
                    }
                }
            }
        }

        if (UtilValidate.isEmpty(party_type) || SysCodeEnmu.DEPTETYPE_02.getCodeValue().equals(party_type)) {
            if (UtilValidate.isNotEmpty(adminLogin.getWhIdList())) {
                for (String id : adminLogin.getWhIdList().split(split_char)) {
                    try {
                        party_id_list.add(Long.valueOf(id));
                    } catch (NumberFormatException e) {
                        log.error("获取当前用户能够看到的仓库 - 仓库ID数据格式错误:" + id, e);
                    }
                }
            }
        }
        return new Result<>(CodeEnum.SUCCESS, party_id_list);
    }

    /**
     * 获取APP端用户可见的仓库和门店（根据用户登陆token获取，适用于APP用户端）
     * @param token
     * @param client
     * @param force_refresh
     * @return
     */
    public static Result<UserCoveredPartyHandler> calculateAndGetUserCoveredParty(String token, PartyFeignClient client, boolean force_refresh) {
        if (UtilValidate.isEmpty(token)) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "用户登陆token是空值");
        }
        UserLoginVO userLogin = LoginCache.getUserLoginVO(token);
        if (userLogin == null) {
            return new Result(CodeEnum.FAIL_BUSINESS, "获取用户信息失败,token=" + token);
        }
        UserCoveredPartyHandler coveredPartyHandler = new UserCoveredPartyHandler();
        //强制刷新时直接调用接口获取当前用户的覆盖门店与仓库
        if (force_refresh) {
            try {
                UserServiceRangeVO userServiceRangeVO = APIInvoker.getUserServiceRange(client, userLogin.getPoi_id(), userLogin.getCity_code());
                if (userServiceRangeVO != null && UtilValidate.isNotEmpty(userServiceRangeVO.getStoreList())) {
                    coveredPartyHandler.store = userServiceRangeVO.getStoreList();
                }
                if (userServiceRangeVO != null && userServiceRangeVO.getWarehouse() != null) {
                    coveredPartyHandler.warehouse = userServiceRangeVO.getWarehouse();
                }
                if (userServiceRangeVO != null && userServiceRangeVO.getDefaultParty() != null) {
                    coveredPartyHandler.default_party = userServiceRangeVO.getDefaultParty();
                }
                return new Result<>(CodeEnum.SUCCESS, coveredPartyHandler);
            } catch (ServerException e) {
                log.error("com.hryj.service.util.UserCoveredByPartyUtil ：计算获取用户的覆盖门店和仓库失败", e);
                coveredPartyHandler.warehouse = userLogin.getWarehouse();
                coveredPartyHandler.store = userLogin.getStoreList();
                return new Result<>(CodeEnum.SUCCESS, coveredPartyHandler);
            }
        }
        if (UtilValidate.isEmpty(userLogin.getStoreList()) && userLogin.getWarehouse() == null) {
            return calculateAndGetUserCoveredParty(token, client, true);
        }
        coveredPartyHandler.warehouse = userLogin.getWarehouse();
        coveredPartyHandler.store = userLogin.getStoreList();
        return new Result<>(CodeEnum.SUCCESS, coveredPartyHandler);
    }

    /**
     * 获取APP端用户可见的仓库和门店（根据用户ID获取，适用于APP门店端）
     * @param user_id
     * @param client
     * @return
     */
    public static Result<UserCoveredPartyHandler> calculateAndGetUserCoveredParty(Long user_id, UserFeignClient client) {
        if (user_id == null || user_id <= 0L) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "用户ID是空值");
        }
        UserCoveredPartyHandler coveredPartyHandler = new UserCoveredPartyHandler();
        try {
            UserServiceRangeVO userServiceRangeVO = APIInvoker.getUserServiceRange(client, user_id);
            if (userServiceRangeVO != null && UtilValidate.isNotEmpty(userServiceRangeVO.getStoreList())) {
                coveredPartyHandler.store = userServiceRangeVO.getStoreList();
            }
            if (userServiceRangeVO != null && userServiceRangeVO.getWarehouse() != null) {
                coveredPartyHandler.warehouse = userServiceRangeVO.getWarehouse();
            }
            if (userServiceRangeVO != null && userServiceRangeVO.getDefaultParty() != null) {
                coveredPartyHandler.default_party = userServiceRangeVO.getDefaultParty();
            }
            return new Result<>(CodeEnum.SUCCESS, coveredPartyHandler);
        } catch (ServerException e) {
            log.error("com.hryj.service.util.UserCoveredByPartyUtil ：计算获取用户的覆盖门店和仓库失败", e);
            return new Result<>(CodeEnum.FAIL_SERVER, "计算获取用户的覆盖门店和仓库失败:" + (UtilValidate.isEmpty(e.getMessage()) ? "" : e.getMessage()));
        }
    }

    /**
     * 获取APP端用户可见的仓库和门店的ID集合（根据用户登陆token获取，适用于APP用户端）
     * @param token
     * @param partyFeignClient
     * @return
     */
    public static Result<Set<Long>> getUserCoveredParty(String token, PartyFeignClient partyFeignClient) {
        UserLoginVO user_login = LoginCache.getUserLoginVO(token);
        if (user_login == null) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "获取用户登陆信息失败, token=" + token);
        }
        UserServiceRangeVO serviceRangeVO;
        try {
            serviceRangeVO = APIInvoker.getUserServiceRange(partyFeignClient, user_login.getPoi_id(), user_login.getCity_code());
            if (serviceRangeVO == null) {
                Result result = new Result(CodeEnum.FAIL_BUSINESS, "没有覆盖当前用户的门店和仓库");
                result.setBiz_code(BizCodeEnum.NO_COVERED_PARTY.getCode());
                return result;
            }
            if (serviceRangeVO.getWarehouse() == null && UtilValidate.isEmpty(serviceRangeVO.getStoreList())) {
                Result result = new Result(CodeEnum.FAIL_BUSINESS, "没有覆盖当前用户的门店和仓库");
                result.setBiz_code(BizCodeEnum.NO_COVERED_PARTY.getCode());
                return result;
            }
        } catch (ServerException e) {
            return new Result<>(CodeEnum.FAIL_SERVER, e.getMessage());
        }

        Set<Long> party_id_set = new HashSet<>((serviceRangeVO.getWarehouse() == null ? 0 : 1) + (serviceRangeVO.getStoreList() == null ? 0 : serviceRangeVO.getStoreList().size()));
        if (serviceRangeVO.getWarehouse() != null) {
            party_id_set.add(serviceRangeVO.getWarehouse().getParty_id());
        }
        if (UtilValidate.isNotEmpty(serviceRangeVO.getStoreList())) {
            for (UserPartyVO userPartyVO : serviceRangeVO.getStoreList()) {
                party_id_set.add(userPartyVO.getParty_id());
            }
        }
        return new Result<>(CodeEnum.SUCCESS, party_id_set);
    }

    public static class UserCoveredPartyHandler {

        public List<UserPartyVO> store;

        public UserPartyVO warehouse;

        public UserPartyVO default_party;

        public boolean hasDefault() {
            return this.default_party != null;
        }

        public boolean hasParty() {
            return this.warehouse != null || UtilValidate.isNotEmpty(this.store) || this.default_party != null;
        }

        public boolean hasStore() {
            return UtilValidate.isNotEmpty(store);
        }

        public boolean hasWarehouse() {
            return this.warehouse != null;
        }
    }

    /**
     * @author 王光银
     * @methodName: getCoveredUserUniqueParty
     * @methodDesc: 返回一个覆盖用户的唯一的门店或仓库
     * @description:
     * @param: [token, user_id]
     * @return java.lang.Long
     * @create 2018-08-15 15:37
     **/
    public static Result<Long> getCoveredUserUniqueParty(Long user_id, String token, UserFeignClient userFeignClient, PartyProductUtilService partyProductUtilService) {
        boolean user_id_null = user_id == null || user_id <= 0L;
        boolean token_null = UtilValidate.isEmpty(token);
        if (token_null && user_id_null) {
            log.error("com.hryj.service.util.UserCoveredByPartyUtil.getCoveredUserUniqueParty: token和用户ID都是空值");
            return new Result<>(CodeEnum.FAIL_BUSINESS, "用户ID与用户登陆token同时为空,无法获取用户的关联门店");
        }

        UserCoveredPartyHandler curr_user_party_handler = null;

        // user_id 有值说明请求来自于APP门店端
        if (user_id != null && user_id > 0L) {
            //(invoke-api) 调用接口获取覆盖用户的门店和仓库
            Result<UserCoveredByPartyUtil.UserCoveredPartyHandler> result = calculateAndGetUserCoveredParty(user_id, userFeignClient);
            if (result.isFailed()) {
                return new Result(CodeEnum.FAIL_BUSINESS, result.getMsg());
            }
            curr_user_party_handler = result.getData();
        }

        // token 有值说明请求来自于APP用户端
        if (UtilValidate.isNotEmpty(token)) {
            UserLoginVO userLogin = LoginCache.getUserLoginVO(token);
            if (userLogin == null) {
                log.error("com.hryj.service.util.UserCoveredByPartyUtil.getCoveredUserUniqueParty: 根据token获取登陆用户数据失败, token = " + token);
            } else {
                curr_user_party_handler = new UserCoveredPartyHandler();
                curr_user_party_handler.default_party = userLogin.getDefaultParty();
                curr_user_party_handler.store = userLogin.getStoreList();
                curr_user_party_handler.warehouse = userLogin.getWarehouse();
            }
        }

        /**
         * 以下是处理用户的默认门店策略
         */
        if (curr_user_party_handler == null) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "您周围还没有咱们的小店");
        }
        if (curr_user_party_handler.default_party == null
                && UtilValidate.isEmpty(curr_user_party_handler.store)
                && curr_user_party_handler.warehouse == null) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "您周围还没有咱们的小店");
        }

        Set<Long> party_id_set = new HashSet<>();
        if (curr_user_party_handler.default_party != null) {
            party_id_set.add(curr_user_party_handler.default_party.getParty_id());
        }
        if (UtilValidate.isNotEmpty(curr_user_party_handler.store)) {
            for (UserPartyVO vo : curr_user_party_handler.store) {
                party_id_set.add(vo.getParty_id());
            }
        }
        if (curr_user_party_handler.warehouse != null) {
            party_id_set.add(curr_user_party_handler.warehouse.getParty_id());
        }

        Map<Long, Integer> statisticsMap = partyProductUtilService.statisticsPartyProductNum(party_id_set);

        if (curr_user_party_handler.default_party != null
                && curr_user_party_handler.default_party.getParty_id() != null
                && statisticsMap.containsKey(curr_user_party_handler.default_party.getParty_id())
                && statisticsMap.get(curr_user_party_handler.default_party.getParty_id()) > 0) {
            /**
             * 如果用户设置了默认门店并且门店下有上架商品，直接返回使用，此时不考虑该门店的状态（是否关闭）
             */
            return new Result<>(CodeEnum.SUCCESS, curr_user_party_handler.default_party.getParty_id());
        }

        //没有默认门店的情况下根据先门店、后仓库，门店按距离排序的原则计算出一个默认门店并且设置
        UserPartyVO nearestParty = getNearestParty(curr_user_party_handler.store, curr_user_party_handler.warehouse, statisticsMap);
        if (nearestParty != null) {
            /**
             * 添加一个任务设置用户的默认门店
             */
            if (UtilValidate.isNotEmpty(token)) {
                ThreadPoolUtil.submitTask(new SetUserDefaultPartyTask(token, nearestParty, userFeignClient));
            }
            return new Result<>(CodeEnum.SUCCESS, nearestParty.getParty_id());
        }
        return new Result<>(CodeEnum.FAIL_BUSINESS, "您周围还没有咱们的小店");
    }

    private static UserPartyVO getNearestParty(List<UserPartyVO> store_list, UserPartyVO warehouse, Map<Long, Integer> statisticsMap) {
        if (UtilValidate.isNotEmpty(store_list)) {
            UserPartyVO nearestParty = null;
            for (UserPartyVO vo : store_list) {
                if (vo.getDistance() == null) {
                    vo.setDistance(BigDecimal.ZERO);
                }

                if (UtilValidate.isEmpty(statisticsMap) || !statisticsMap.containsKey(vo.getParty_id()) || statisticsMap.get(vo.getParty_id()) < 1) {
                    continue;
                }

                if (nearestParty == null) {
                    nearestParty = vo;
                    continue;
                }
                if (nearestParty.getDistance().compareTo(vo.getDistance()) > 0) {
                    nearestParty = vo;
                }
            }
            if (nearestParty != null) {
                return nearestParty;
            }
        }

        if (warehouse != null && UtilValidate.isNotEmpty(statisticsMap) && statisticsMap.containsKey(warehouse.getParty_id()) && statisticsMap.get(warehouse.getParty_id()) > 0) {
            return warehouse;
        }

        return null;
    }

}
