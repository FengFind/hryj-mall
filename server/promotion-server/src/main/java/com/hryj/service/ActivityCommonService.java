package com.hryj.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.promotion.ActivityInfo;
import com.hryj.entity.bo.promotion.ActivityScopeItem;
import com.hryj.entity.vo.promotion.activity.request.common.CommonProdCheckRequestVO;
import com.hryj.entity.vo.promotion.activity.response.common.CommonProdCheckResponseVO;
import com.hryj.entity.vo.promotion.activity.response.common.ProdCheckItem;
import com.hryj.exception.ServerException;
import com.hryj.mapper.ActivityMapper;
import com.hryj.mapper.ActivityScopeItemMapper;
import com.hryj.utils.UtilMisc;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.*;

/**
 * @author 王光银
 * @className: ActivityCommonService
 * @description:
 * @create 2018/7/10 0010 19:03
 **/
@Slf4j
@Service
public class ActivityCommonService extends ServiceImpl<ActivityMapper, ActivityInfo> {

    @Autowired
    private ActivityScopeItemMapper activityScopeItemMapper;

    /**
     * @author 王光银
     * @methodName: checkProduct
     * @methodDesc: 从商品角度返回这个商品所有的有效的促销活动
     * @description:
     * @param: [commonProdCheckRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.promotion.activity.response.common.CommonProdCheckResponseVO>
     * @create 2018-07-10 19:05
     **/
    @PostMapping("/checkProduct")
    public Result<CommonProdCheckResponseVO> checkProduct(@RequestBody CommonProdCheckRequestVO checkRequestVO) throws ServerException {
        if (checkRequestVO == null || UtilValidate.isEmpty(checkRequestVO.getProduct_id_list())) {
            return new Result<>(CodeEnum.SUCCESS, new CommonProdCheckResponseVO());
        }

        //查询包含了这些商品的所有 有效的活动
        Date current = new Date();
        Map<String, Object> params_map = new HashMap<>(5);
        params_map.put("prod_id_list", checkRequestVO.getProduct_id_list());
        params_map.put("audit_status", ActivityInfo.AUDIT_STATUS_PASS);
        params_map.put("activity_status", ActivityInfo.ACTIVITY_STATUS_NORMAL);
        params_map.put("start_date", current);
        params_map.put("end_date", current);

        List<ProdCheckItem> list;
        try {
            list = super.baseMapper.selectCheckActivity(params_map);
        } catch (Exception e) {
            log.error("促销活动公共服务-加载包含指定商品的有效活动时异常", e);
            throw new ServerException("促销活动公共服务-加载包含指定商品的有效活动时异常", e);
        }
        if (UtilValidate.isEmpty(list)) {
            return new Result<>(CodeEnum.SUCCESS, new CommonProdCheckResponseVO());
        }

        //按商品对这些活动进行归属
        Map<Long, List<ProdCheckItem>> prod_check_map = new HashMap<>(list.size());
        for (ProdCheckItem checkItem : list) {
            if (prod_check_map.containsKey(checkItem.getProduct_id())) {
                prod_check_map.get(checkItem.getProduct_id()).add(checkItem);
            } else {
                prod_check_map.put(checkItem.getProduct_id(), UtilMisc.toList(checkItem));
            }
        }

        //加载活动的当事组织范围（门店或仓库）
        Set<Long> activity_id_set = new HashSet<>(list.size());
        for (ProdCheckItem item : list) {
            activity_id_set.add(item.getActivity_id());
        }
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.in("activity_id", activity_id_set);
        wrapper.setSqlSelect("activity_id", "party_id", "start_date", "end_date");
        List<ActivityScopeItem> scopeItemList = activityScopeItemMapper.selectList(wrapper);

        //如果当事组织单独设置了活动的起止时间则对活动再一次进行过虑，并且按活动对这些参与的当事组织进行归类
        Map<Long, Set<Long>> activity_party = new HashMap<>(scopeItemList.size());
        if (UtilValidate.isNotEmpty(scopeItemList)) {
            Iterator<ActivityScopeItem> it = scopeItemList.iterator();
            while (it.hasNext()) {
                ActivityScopeItem item = it.next();
                if (item.getStart_date() != null && current.before(item.getStart_date())) {
                    it.remove();
                }
                if (item.getEnd_date() != null && current.after(item.getEnd_date())) {
                    it.remove();
                }

                if (activity_party.containsKey(item.getActivity_id())) {
                    activity_party.get(item.getActivity_id()).add(item.getParty_id());
                } else {
                    activity_party.put(item.getActivity_id(), UtilMisc.toSet(item.getParty_id()));
                }
            }
        }

        //将活动范围的当事组织关联到返回的商品检查数据上
        Iterator<Long> it = prod_check_map.keySet().iterator();
        while (it.hasNext()) {
            Long product_id = it.next();
            List<ProdCheckItem> sub_list = prod_check_map.get(product_id);
            for (ProdCheckItem item : sub_list) {
                item.setScope_party(activity_party.get(item.getActivity_id()));
            }
        }
        CommonProdCheckResponseVO checkResponseVO = new CommonProdCheckResponseVO();
        checkResponseVO.setCheck_result(prod_check_map);
        return new Result<>(CodeEnum.SUCCESS, checkResponseVO);
    }
}
