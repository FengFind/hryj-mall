package com.hryj.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.cache.LoginCache;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.constant.CommonConstantPool;
import com.hryj.entity.bo.promotion.ActivityAuditRecord;
import com.hryj.entity.bo.promotion.ActivityInfo;
import com.hryj.entity.bo.promotion.ActivityScopeItem;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.promotion.activity.request.SearchPromotionActivityRequestVO;
import com.hryj.entity.vo.promotion.activity.request.SubmitActivityRequestVO;
import com.hryj.entity.vo.promotion.activity.response.PromotionActivityAuditRecordResponseVO;
import com.hryj.entity.vo.staff.team.StaffStoreWhVO;
import com.hryj.entity.vo.staff.user.StaffAdminLoginVO;
import com.hryj.exception.BizException;
import com.hryj.exception.ServerException;
import com.hryj.feign.DeptFeignClient;
import com.hryj.mapper.ActivityAuditMapper;
import com.hryj.mapper.ActivityMapper;
import com.hryj.mapper.ActivityScopeItemMapper;
import com.hryj.service.util.ServiceInvoker;
import com.hryj.threadpool.ThreadPoolUtil;
import com.hryj.utils.UtilValidate;
import com.hryj.worktask.ActivityAuditPassTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author 汪豪
 * @className: ActivityAuditService
 * @description: 活动审核相关Service
 * @create 2018/7/4 0004 20:28
 **/
@Slf4j
@Service
public class ActivityAuditService extends ServiceImpl<ActivityAuditMapper,ActivityAuditRecord> {
    
    @Autowired
    private ActivityAuditMapper activityAuditMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ActivityScopeItemMapper activityScopeItemMapper;

    @Autowired
    private DeptFeignClient deptFeignClient;
    /**
     * @author 汪豪
     * @methodName: searchPromotionActivityAuditRecord
     * @methodDesc: 分页查询促销活动的所有审核记录
     * @description:
     * @param: [searchPromotionActivityRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.promotion.activity.response.PromotionActivityAuditRecordResponseVO>>
     * @create 2018-07-06 9:27
     **/
    public Result<PageResponseVO<PromotionActivityAuditRecordResponseVO>> searchPromotionActivityAuditRecord(SearchPromotionActivityRequestVO searchPromotionActivityRequestVO) {
        StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(searchPromotionActivityRequestVO.getLogin_token());
        if(staffAdminLoginVO == null){
            return new Result<>(CodeEnum.FAIL_BUSINESS,"获取用户登陆信息失败, token=" + searchPromotionActivityRequestVO.getLogin_token());
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
        searchPromotionActivityRequestVO.setS_w_id_list(s_w_id_list);
        log.info("分页查询促销活动的所有审核记录:searchPromotionActivityRequestVO====="+JSON.toJSONString(searchPromotionActivityRequestVO));
        Page page = new Page(searchPromotionActivityRequestVO.getPage_num(),searchPromotionActivityRequestVO.getPage_size());
        List<PromotionActivityAuditRecordResponseVO> records = activityAuditMapper.searchActivityAuditRecordList(searchPromotionActivityRequestVO, page);
        PageResponseVO pageResponseVO = new PageResponseVO();
        pageResponseVO.setRecords(records);
        pageResponseVO.setTotal_count(page.getTotal());
        pageResponseVO.setTotal_page(page.getPages());
        log.info("分页查询促销活动的所有审核记录:result====="+JSON.toJSONString(pageResponseVO));
        return new Result<>(CodeEnum.SUCCESS, pageResponseVO);
    }

    /**
     * @author 汪豪
     * @methodName: submitActivityHandleResult
     * @methodDesc: 提交促销活动审核处理结果
     * @description:
     * @param: [submitActivityRequestVo]
     * @return com.hryj.common.Result
     * @create 2018-07-06 9:27
     **/
    @Transactional
    public Result submitActivityHandleResult(SubmitActivityRequestVO submitActivityRequestVo) throws BizException {
        log.info("提交促销活动审核处理结果:submitActivityRequestVo====="+JSON.toJSONString(submitActivityRequestVo));
        if(submitActivityRequestVo.getActivity_id() == null || submitActivityRequestVo.getActivity_id() <= 0L){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动id不能为空");
        }
        if(StrUtil.isEmpty(submitActivityRequestVo.getHandle_result())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"处理结果不能为空");
        }
        StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(submitActivityRequestVo.getLogin_token());
        if(staffAdminLoginVO == null){
            return new Result(CodeEnum.FAIL_BUSINESS,"获取用户登陆信息失败,token="+submitActivityRequestVo.getLogin_token());
        }

        /**
         * 修改了一下，后面的任务需要活动的开始日期和结束日期，活动对象从数据库查
         */
        ActivityInfo activityInfo = activityMapper.selectById(submitActivityRequestVo.getActivity_id());
        activityInfo.setAudit_status(Integer.parseInt(submitActivityRequestVo.getHandle_result()));
        activityMapper.updateById(activityInfo);

        activityInfo = activityMapper.selectById(submitActivityRequestVo.getActivity_id());
        ActivityAuditRecord record = new ActivityAuditRecord();
        record.setActivity_id(submitActivityRequestVo.getActivity_id());
        record.setSubmit_staff_id(activityInfo.getOperator_id());
        record.setSubmit_staff_name(activityInfo.getOperator_name());
        record.setSubmit_time(activityInfo.getCreate_time());
        record.setHandle_staff_id(staffAdminLoginVO.getStaff_id());
        record.setHandle_staff_name(staffAdminLoginVO.getStaff_name());
        record.setHandle_result(Integer.parseInt(submitActivityRequestVo.getHandle_result()));
        record.setHandle_time(new Date());
        record.setAudit_remark(submitActivityRequestVo.getHandle_remark());
        activityAuditMapper.insert(record);

        if(submitActivityRequestVo.getHandle_result().equals(CommonConstantPool.STR_ONE)){
            //JOIN TASK WGY  活动审核通过后启动任务影响商品搜索缓存
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.eq("activity_id",activityInfo.getId());
            List<ActivityScopeItem> scopeItems = activityScopeItemMapper.selectList(wrapper);
            if (UtilValidate.isNotEmpty(scopeItems)) {
                List<Long> party_id_list = new ArrayList<>(scopeItems.size());
                for (ActivityScopeItem scopeItem : scopeItems) {
                    party_id_list.add(scopeItem.getParty_id());
                }
                ThreadPoolUtil.submitTask(new ActivityAuditPassTask(party_id_list, activityInfo.getStart_date(), activityInfo.getEnd_date()));
            }
        }
        return new Result<>(CodeEnum.SUCCESS);
    }
}
