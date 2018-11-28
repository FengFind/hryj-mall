package com.hryj.service.app;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.cache.LoginCache;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.product.PartyProduct;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.user.UserLoginVO;
import com.hryj.entity.vo.user.UserPartyVO;
import com.hryj.entity.vo.userparty.request.UserPartyRequestVO;
import com.hryj.entity.vo.userparty.response.UserPartyResponseItem;
import com.hryj.exception.BizException;
import com.hryj.exception.ServerException;
import com.hryj.feign.UserFeignClient;
import com.hryj.mapper.PartyProductMapper;
import com.hryj.service.PartyProductUtilService;
import com.hryj.service.util.UserCoveredByPartyUtil;
import com.hryj.utils.UtilMisc;
import com.hryj.utils.UtilValidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author 王光银
 * @className: UserPartyService
 * @description:
 * @create 2018/8/15 0015 16:53
 **/
@Service
public class UserPartyService extends ServiceImpl<PartyProductMapper, PartyProduct> {

    @Autowired
    private PartyProductUtilService partyProductUtilService;

    @Autowired
    private UserFeignClient userFeignClient;

    /**
     * @author 王光银
     * @methodName: getUserUniqueParty
     * @methodDesc: 返回用户关联的唯一门店ID
     * @description:
     * @param: [userPartyRequestVO]
     * @return com.hryj.common.Result<java.lang.Long>
     * @create 2018-08-16 15:29
     **/
    public Result<Long> getUserUniqueParty(UserPartyRequestVO userPartyRequestVO) throws BizException {
        Result<Long> result = UserCoveredByPartyUtil.getCoveredUserUniqueParty(userPartyRequestVO.getUser_id(), userPartyRequestVO.getLogin_token(), userFeignClient, partyProductUtilService);
        return result;
    }


    /**
     * @author 王光银
     * @methodName: findAroundPartyList
     * @methodDesc: 返回覆盖用户的门店和仓库
     * @description: 返回数据的排序规则： 默认门店 - 门店按照距离 - 仓库
     * @param: [userPartyRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.userparty.response.UserPartyResponseItem>>
     * @create 2018-08-15 16:55
     **/
    public Result<ListResponseVO<UserPartyResponseItem>> findAroundPartyList(UserPartyRequestVO userPartyRequestVO) throws ServerException {

        List<UserPartyResponseItem> store_list = null;
        UserPartyResponseItem warehouse = null;
        UserPartyResponseItem defaultParty = null;

        if (userPartyRequestVO.getUser_id() != null && userPartyRequestVO.getUser_id() > 0L) {
            //(invoke-api) 调用API获取覆盖用户的门店和仓库
            Result<UserCoveredByPartyUtil.UserCoveredPartyHandler> result = UserCoveredByPartyUtil.calculateAndGetUserCoveredParty(userPartyRequestVO.getUser_id(), userFeignClient);
            if (result.isFailed()) {
                return new Result<>(CodeEnum.FAIL_BUSINESS, result.getMsg());
            }
            if (result.getData() != null) {
                UserCoveredByPartyUtil.UserCoveredPartyHandler handler = result.getData();

                if (handler.default_party != null) {
                    defaultParty = fromUserPartyVo(handler.default_party);
                }

                if (UtilValidate.isNotEmpty(handler.store)) {
                    store_list = new ArrayList<>(handler.store.size());
                    for (UserPartyVO vo : handler.store) {
                        UserPartyResponseItem item = fromUserPartyVo(vo);

                        if (!item.equals(defaultParty)) {
                            store_list.add(item);
                        }
                    }
                }

                if (handler.warehouse != null ) {
                    UserPartyResponseItem item = fromUserPartyVo(handler.warehouse);
                    if (!item.equals(defaultParty)) {
                        warehouse = item;
                    }
                }
            }
        }

        //用户端的请求根据用户登陆 token获取数据
        UserLoginVO userLogin = LoginCache.getUserLoginVO(userPartyRequestVO.getLogin_token());
        if (userLogin != null) {

            if (userLogin.getDefaultParty() != null) {
                defaultParty = fromUserPartyVo(userLogin.getDefaultParty());
            }

            if (UtilValidate.isNotEmpty(userLogin.getStoreList())) {
                if (UtilValidate.isEmpty(store_list)) {
                    store_list = new ArrayList<>(userLogin.getStoreList().size());
                }

                for (UserPartyVO vo : userLogin.getStoreList()) {
                    UserPartyResponseItem item = fromUserPartyVo(vo);
                    if (!item.equals(defaultParty)) {
                        store_list.add(item);
                    }
                }
            }

            if (userLogin.getWarehouse() != null) {
                UserPartyResponseItem item = fromUserPartyVo(userLogin.getWarehouse());
                if (!item.equals(defaultParty)) {
                    warehouse = item;
                }
            }
        }

        if (UtilValidate.isNotEmpty(store_list)) {
            store_list = UtilMisc.toList(UtilMisc.toSet(store_list));
        }

        List<UserPartyResponseItem> return_party_list;
        if (UtilValidate.isNotEmpty(store_list)) {
            store_list.sort((UserPartyResponseItem item1, UserPartyResponseItem item2) -> (item1.getDistance().compareTo(item2.getDistance())));
            return_party_list = store_list;
        } else {
            return_party_list = new ArrayList<>(2);
        }

        if (defaultParty != null) {
            return_party_list.add(0, defaultParty);
        }

        if (warehouse != null) {
            return_party_list.add(warehouse);
        }

        /**
         * TODO WGY 验证这些门店的状态, 过滤已关闭的门店 - 暂时先不做验证，提高响应效率
         */

        /**
         * 获取这些门店或仓库的在售商品数量， 过滤掉没有在售商品的门店
         */
        Set<Long> party_id_set = new HashSet<>(return_party_list.size());
        for (UserPartyResponseItem item : return_party_list) {
            party_id_set.add(item.getParty_id());
        }
        Map<Long, Integer> party_prod_statistics_map = partyProductUtilService.statisticsPartyProductNum(party_id_set);
        Iterator<UserPartyResponseItem> it = return_party_list.iterator();
        while (it.hasNext()) {
            UserPartyResponseItem item = it.next();
            boolean gt_zero = party_prod_statistics_map.get(item.getParty_id()) > 0;
            if (!gt_zero) {
                it.remove();
            }
        }

        return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>(return_party_list));
    }

    private UserPartyResponseItem fromUserPartyVo(UserPartyVO vo) {
        UserPartyResponseItem item = new UserPartyResponseItem();
        item.setParty_id(vo.getParty_id());
        item.setParty_name(vo.getParty_name());
        item.setParty_address(vo.getParty_address());
        item.setDistance(vo.getDistance() == null ? 0 : vo.getDistance().intValue());
        item.setDept_type(vo.getDept_type());
        return item;
    }
}
