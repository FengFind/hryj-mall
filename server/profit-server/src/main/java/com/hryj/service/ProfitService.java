package com.hryj.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.plugins.Page;
import com.hryj.cache.LoginCache;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.profit.RegionBalanceSummary;
import com.hryj.entity.bo.profit.StoreBalanceSummary;
import com.hryj.entity.vo.PageRequestVO;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.profit.request.*;
import com.hryj.entity.vo.profit.response.*;
import com.hryj.entity.vo.staff.user.StaffAdminLoginVO;
import com.hryj.entity.vo.staff.user.StaffAppLoginVO;
import com.hryj.exception.GlobalException;
import com.hryj.feign.TaskFeignClient;
import com.hryj.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

/**
 * @author 李道云
 * @className: ProfitService
 * @description: 分润service
 * @create 2018/7/11 14:57
 **/
@Slf4j
@Service
public class ProfitService{

    @Autowired
    private RegionBalanceSummaryMapper regionBalanceSummaryMapper;

    @Autowired
    private RegionBalanceDetailMapper regionBalanceDetailMapper;

    @Autowired
    private StoreBalanceSummaryMapper storeBalanceSummaryMapper;

    @Autowired
    private StaffBalanceSummaryMapper staffBalanceSummaryMapper;

    @Autowired
    private DeptGrossProfitBalanceMapper deptGrossProfitBalanceMapper;

    @Autowired
    private TaskFeignClient taskFeignClient;

    /**
     * @author 李道云
     * @methodName: searchRegionProfitList
     * @methodDesc: 分页查询区域分润列表
     * @description:
     * @param: [regionProfitRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.profit.response.RegionBalanceVO>>
     * @create 2018-07-13 16:12
     **/
    public Result<PageResponseVO<RegionBalanceVO>> searchRegionProfitList(RegionProfitRequestVO regionProfitRequestVO){
        log.info("分页查询区域分润列表:regionProfitRequestVO===" + JSON.toJSONString(regionProfitRequestVO));
        StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(regionProfitRequestVO.getLogin_token());
        regionProfitRequestVO.setDept_path(staffAdminLoginVO.getDeptGroup().getDept_path());
        Page page = new Page(regionProfitRequestVO.getPage_num(),regionProfitRequestVO.getPage_size());
        List<RegionBalanceVO> records = regionBalanceSummaryMapper.searchRegionProfitList(regionProfitRequestVO,page);
        PageResponseVO pageResponseVO = new PageResponseVO();
        pageResponseVO.setRecords(records);
        pageResponseVO.setTotal_count(page.getTotal());
        pageResponseVO.setTotal_page(page.getPages());
        log.info("分页查询区域分润列表:pageResponseVO===" + JSON.toJSONString(pageResponseVO));
        return new Result<>(CodeEnum.SUCCESS, pageResponseVO);
    }
    /**
     * @author 李道云
     * @methodName: searchRegionProfitDetailList
     * @methodDesc: 分页查询区域分润明细列表
     * @description:
     * @param: [regionProfitDetailRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.profit.response.RegionBalanceDetailVO>>
     * @create 2018-07-13 17:12
     **/
    public Result<PageResponseVO<RegionBalanceDetailVO>> searchRegionProfitDetailList(RegionProfitDetailRequestVO regionProfitDetailRequestVO){
        log.info("分页查询区域分润明细列表:regionProfitDetailRequestVO===" + JSON.toJSONString(regionProfitDetailRequestVO));
        Page page = new Page(regionProfitDetailRequestVO.getPage_num(),regionProfitDetailRequestVO.getPage_size());
        List<RegionBalanceDetailVO> records = regionBalanceDetailMapper.searchRegionProfitDetailList(regionProfitDetailRequestVO,page);
        PageResponseVO pageResponseVO = new PageResponseVO();
        pageResponseVO.setRecords(records);
        pageResponseVO.setTotal_count(page.getTotal());
        pageResponseVO.setTotal_page(page.getPages());
        log.info("分页查询区域分润明细列表:pageResponseVO===" + JSON.toJSONString(pageResponseVO));
        return new Result<>(CodeEnum.SUCCESS, pageResponseVO);
    }

    /**
     * @author 李道云
     * @methodName: updateRegionNonFixedCost
     * @methodDesc: 更新区域公司非固定成本
     * @description:
     * @param: [nonFixedCostSetRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-07-13 17:46
     **/
    @Transactional
    public Result updateRegionNonFixedCost(NonFixedCostSetRequestVO nonFixedCostSetRequestVO) throws GlobalException {
        log.info("更新区域公司非固定成本:nonFixedCostSetRequestVO===" + JSON.toJSONString(nonFixedCostSetRequestVO));
        //1、请求参数校验
        Long summary_id = nonFixedCostSetRequestVO.getSummary_id();
        String non_fixed_cost = nonFixedCostSetRequestVO.getNon_fixed_cost();
        Integer setup_status = nonFixedCostSetRequestVO.getSetup_status();
        if(summary_id ==null){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"结算汇总id不能为空");
        }
        if(StrUtil.isEmpty(non_fixed_cost)){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"非固定成本不能为空");
        }
        if(setup_status ==null){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"设置状态不能为空");
        }
        //2、判断当前区域公司是否已经设置非固定成本
        RegionBalanceSummary regionBalanceSummary = regionBalanceSummaryMapper.selectById(summary_id);
        if(regionBalanceSummary.getSetup_status()){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"已设置，不能再设置");
        }
        regionBalanceSummary.setRegion_non_fixed_cost(NumberUtil.round(non_fixed_cost,2));
        StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(nonFixedCostSetRequestVO.getLogin_token());
        Long operator_id = staffAdminLoginVO.getStaff_id();
        regionBalanceSummary.setOperator_id(operator_id);//设置操作人id
        if(setup_status ==0){
            regionBalanceSummary.setSetup_status(false);
            regionBalanceSummaryMapper.updateById(regionBalanceSummary);
            return new Result<>(CodeEnum.SUCCESS);
        }else{
            //如果确定设置区域公司非固定成本,先判断区域公司下所有门店是否已设置完非固定成本
            if(!regionBalanceSummary.getStore_setup_status()){
                return new Result(CodeEnum.FAIL_BUSINESS,"门店的非固定成本没有设置完");
            }
            //判断上个月是否已结算完成
            RegionBalanceSummary wrapper = new RegionBalanceSummary();
            String balance_month = regionBalanceSummary.getBalance_month();
            Date lastMonth = DateUtil.parse(balance_month,"yyyy-MM");
            String last_month = DateUtil.format(DateUtil.offsetMonth(lastMonth,-1),"yyyy-MM");
            wrapper.setBalance_month(last_month);
            wrapper.setDept_id(regionBalanceSummary.getDept_id());
            RegionBalanceSummary regionSummary = regionBalanceSummaryMapper.selectOne(wrapper);
            if(regionSummary !=null && !regionSummary.getSetup_status()){
                return new Result(CodeEnum.FAIL_BUSINESS,"前一个月的未结算，设置失败");
            }
            regionBalanceSummary.setSetup_status(true);
            regionBalanceSummaryMapper.updateById(regionBalanceSummary);
        }
        //3、开始复杂的分润计算，耗时太长异步计算
        ThreadUtil.excAsync(() -> {
            taskFeignClient.calculateProfit(summary_id,operator_id);
        },false);
        return new Result<>(CodeEnum.SUCCESS,"后台正在进行复杂的分润计算，请稍等几分钟再查看分润结果");
    }

    /**
     * @author 李道云
     * @methodName: searchStoreProfitList
     * @methodDesc: 分页查询门店分润列表
     * @description:
     * @param: [storeProfitRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.profit.response.StoreBalanceVO>>
     * @create 2018-07-13 19:43
     **/
    public Result<PageResponseVO<StoreBalanceVO>> searchStoreProfitList(StoreProfitRequestVO storeProfitRequestVO) throws GlobalException {
        log.info("分页查询门店分润列表:storeProfitRequestVO===" + JSON.toJSONString(storeProfitRequestVO));
        StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(storeProfitRequestVO.getLogin_token());
        storeProfitRequestVO.setDept_path(staffAdminLoginVO.getDeptGroup().getDept_path());
        Page page = new Page(storeProfitRequestVO.getPage_num(),storeProfitRequestVO.getPage_size());
        List<StoreBalanceVO> records = storeBalanceSummaryMapper.searchStoreProfitList(storeProfitRequestVO,page);
        PageResponseVO pageResponseVO = new PageResponseVO();
        pageResponseVO.setRecords(records);
        pageResponseVO.setTotal_count(page.getTotal());
        pageResponseVO.setTotal_page(page.getPages());
        log.info("分页查询门店分润列表:pageResponseVO===" + JSON.toJSONString(pageResponseVO));
        return new Result<>(CodeEnum.SUCCESS, pageResponseVO);
    }

    /**
     * @author 李道云
     * @methodName: findStoreProfitDetailList
     * @methodDesc: 查询门店分润明细数据
     * @description:
     * @param: [storeProfitDetailRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.profit.response.StoreProfitDetailResponseVO>
     * @create 2018-07-13 19:44
     **/
    public Result<StoreProfitDetailResponseVO> findStoreProfitDetailList(StoreProfitDetailRequestVO storeProfitDetailRequestVO) throws GlobalException {
        log.info("查询门店分润明细数据:storeProfitDetailRequestVO===" + JSON.toJSONString(storeProfitDetailRequestVO));
        Long store_id = storeProfitDetailRequestVO.getStore_id();
        String query_month = storeProfitDetailRequestVO.getQuery_month();
        if(store_id ==null){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"门店id不能为空");
        }
        if(StrUtil.isEmpty(query_month)){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"查询月份不能为空");
        }
        StoreProfitDetailResponseVO storeProfitDetailResponseVO = new StoreProfitDetailResponseVO();
        //店员数据
        List<StaffBalanceSummaryVO> staffDataList = staffBalanceSummaryMapper.findStaffBalanceSummaryList(store_id, query_month);
        //成本数据
        List<StoreCostDetailVO> costDataList = staffBalanceSummaryMapper.findStoreCostDetailList(store_id, query_month);
        //配送数据
        List<StaffDistributionVO> distributionList = staffBalanceSummaryMapper.findStaffDistributionList(store_id,query_month);
        storeProfitDetailResponseVO.setStaffDataList(staffDataList);
        storeProfitDetailResponseVO.setCostDataList(costDataList);
        storeProfitDetailResponseVO.setDistributionList(distributionList);
        log.info("查询门店分润明细数据:storeProfitDetailResponseVO===" + JSON.toJSONString(storeProfitDetailResponseVO));
        return new Result<>(CodeEnum.SUCCESS, storeProfitDetailResponseVO);
    }

    /**
     * @author 李道云
     * @methodName: updateStoreNonFixedCost
     * @methodDesc: 更新门店非固定成本
     * @description:
     * @param: [nonFixedCostSetRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-07-13 20:28
     **/
    @Transactional
    public Result updateStoreNonFixedCost(NonFixedCostSetRequestVO nonFixedCostSetRequestVO) throws GlobalException {
        log.info("更新门店非固定成本:nonFixedCostSetRequestVO===" + JSON.toJSONString(nonFixedCostSetRequestVO));
        Long summary_id = nonFixedCostSetRequestVO.getSummary_id();
        String non_fixed_cost = nonFixedCostSetRequestVO.getNon_fixed_cost();
        Integer setup_status = nonFixedCostSetRequestVO.getSetup_status();
        if(summary_id ==null){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"结算汇总id不能为空");
        }
        if(StrUtil.isEmpty(non_fixed_cost)){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"非固定成本不能为空");
        }
        if(setup_status ==null){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"设置状态不能为空");
        }
        StoreBalanceSummary storeBalanceSummary = storeBalanceSummaryMapper.selectById(summary_id);
        if(storeBalanceSummary.getSetup_status()){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"已设置，不能再设置");
        }
        storeBalanceSummary.setStore_non_fixed_cost(NumberUtil.round(non_fixed_cost,2));
        StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(nonFixedCostSetRequestVO.getLogin_token());
        storeBalanceSummary.setOperator_id(staffAdminLoginVO.getStaff_id());//设置操作人id
        if(setup_status ==1){
            storeBalanceSummary.setSetup_status(true);
        }
        storeBalanceSummaryMapper.updateById(storeBalanceSummary);
        //判断是否已全部设置完门店的非固定成本，如果已设置完，需要更新区域公司结算汇总表的门店设置状态
        List<StoreBalanceSummary> storeList = storeBalanceSummaryMapper.findNoSetNonFixedCostStoreList(storeBalanceSummary.getBalance_month(),storeBalanceSummary.getRegion_id());
        if(CollectionUtil.isEmpty(storeList)){
            regionBalanceSummaryMapper.updateStoreSetupStatus(storeBalanceSummary.getBalance_month(),storeBalanceSummary.getRegion_id());
        }
        return new Result<>(CodeEnum.SUCCESS);
    }

    /**
     * @author 李道云
     * @methodName: searchStoreManagerProfitList
     * @methodDesc: 分页查询店长分润列表
     * @description:
     * @param: [storeManagerProfitRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.profit.response.StoreManagerProfitVO>>
     * @create 2018-07-13 20:28
     **/
    public Result<PageResponseVO<StoreManagerProfitVO>> searchStoreManagerProfitList(StoreManagerProfitRequestVO storeManagerProfitRequestVO) throws GlobalException{
        log.info("分页查询店长分润列表:storeManagerProfitRequestVO===" + JSON.toJSONString(storeManagerProfitRequestVO));
        StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(storeManagerProfitRequestVO.getLogin_token());
        storeManagerProfitRequestVO.setDept_path(staffAdminLoginVO.getDeptGroup().getDept_path());
        Page page = new Page(storeManagerProfitRequestVO.getPage_num(),storeManagerProfitRequestVO.getPage_size());
        List<StoreManagerProfitVO> records = staffBalanceSummaryMapper.searchStoreManagerProfitList(storeManagerProfitRequestVO,page);
        PageResponseVO pageResponseVO = new PageResponseVO();
        pageResponseVO.setRecords(records);
        pageResponseVO.setTotal_count(page.getTotal());
        pageResponseVO.setTotal_page(page.getPages());
        log.info("分页查询店长分润列表:pageResponseVO===" + JSON.toJSONString(pageResponseVO));
        return new Result<>(CodeEnum.SUCCESS, pageResponseVO);
    }

    /**
     * @author 李道云
     * @methodName: findProfitDataDetail
     * @methodDesc: 查询分润数据明细
     * @description:
     * @param: [requestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.profit.response.ProfitDataDetailVO>
     * @create 2018-07-14 9:26
     **/
    public Result<ProfitDataDetailVO> findProfitDataDetail(RequestVO requestVO){
        log.info("查询分润数据明细:requestVO===" + JSON.toJSONString(requestVO));
        String login_token = requestVO.getLogin_token();
        StaffAppLoginVO staffAppLoginVO = LoginCache.getStaffAppLoginVO(login_token);
        Long staff_id = staffAppLoginVO.getStaff_id();
        String query_month = DateUtil.format(new Date(),"yyyy-MM");
        ProfitDataDetailVO profitDataDetailVO = staffBalanceSummaryMapper.findProfitDataDetail(staff_id,query_month);
        if(profitDataDetailVO !=null){
            log.info("查询分润数据明细:profitDataDetailVO===" + JSON.toJSONString(profitDataDetailVO));
        }else{
            profitDataDetailVO = new ProfitDataDetailVO();
        }
        return new Result<>(CodeEnum.SUCCESS, profitDataDetailVO);
    }

    /**
     * @author 李道云
     * @methodName: searchProfitDataDetail
     * @methodDesc: 分页查询分润数据明细
     * @description:
     * @param: [pageRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.profit.response.ProfitDataDetailVO>>
     * @create 2018-07-14 9:47
     **/
    public Result<PageResponseVO<ProfitDataDetailVO>> searchProfitDataDetail(@RequestBody PageRequestVO pageRequestVO){
        log.info("分页查询分润数据明细:pageRequestVO===" + JSON.toJSONString(pageRequestVO));
        String login_token = pageRequestVO.getLogin_token();
        StaffAppLoginVO staffAppLoginVO = LoginCache.getStaffAppLoginVO(login_token);
        Long staff_id = staffAppLoginVO.getStaff_id();
        Page page = new Page(pageRequestVO.getPage_num(),pageRequestVO.getPage_size());
        List<ProfitDataDetailVO> records = staffBalanceSummaryMapper.searchProfitDataDetail(staff_id,page);
        PageResponseVO pageResponseVO = new PageResponseVO();
        pageResponseVO.setRecords(records);
        pageResponseVO.setTotal_count(page.getTotal());
        pageResponseVO.setTotal_page(page.getPages());
        log.info("分页查询分润数据明细:pageResponseVO===" + JSON.toJSONString(pageResponseVO));
        return new Result<>(CodeEnum.SUCCESS, pageResponseVO);
    }

    /**
     * @author 李道云
     * @methodName: searcheDeptGrossProfit
     * @methodDesc: 分页查询部门毛利分润
     * @description:
     * @param: [pageRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.profit.response.DeptGrossProfitResponseVO>>
     * @create 2018-08-16 11:21
     **/
    public Result<PageResponseVO<DeptGrossProfitResponseVO>> searcheDeptGrossProfit(PageRequestVO pageRequestVO){
        log.info("分页查询部门毛利分润:pageRequestVO===" + JSON.toJSONString(pageRequestVO));
        String login_token = pageRequestVO.getLogin_token();
        StaffAppLoginVO staffAppLoginVO = LoginCache.getStaffAppLoginVO(login_token);
        Long dept_id = staffAppLoginVO.getDeptGroup().getId();
        Page page = new Page(pageRequestVO.getPage_num(),pageRequestVO.getPage_size());
        List<DeptGrossProfitResponseVO> records = deptGrossProfitBalanceMapper.searcheDeptGrossProfit(dept_id,page);
        PageResponseVO pageResponseVO = new PageResponseVO();
        pageResponseVO.setRecords(records);
        pageResponseVO.setTotal_count(page.getTotal());
        pageResponseVO.setTotal_page(page.getPages());
        log.info("分页查询部门毛利分润:pageResponseVO===" + JSON.toJSONString(pageResponseVO));
        return new Result<>(CodeEnum.SUCCESS, pageResponseVO);
    }

    /**
     * @author 李道云
     * @methodName: updateReferralStatisData
     * @methodDesc: 更新推荐用户的统计数据
     * @description:
     * @param: [statis_date, staff_id, store_id]
     * @return com.hryj.common.Result
     * @create 2018-08-29 10:03
     **/
    public Result updateReferralStatisData(@RequestParam(name = "statis_date") String statis_date,
                                           @RequestParam(name = "staff_id") Long staff_id,
                                           @RequestParam(name = "store_id") Long store_id){
        log.info("更新推荐用户的统计数据,statis_date={},staff_id={},store_id={}",statis_date,staff_id,store_id);
        if(StrUtil.isEmpty(statis_date)){
           return new Result(CodeEnum.FAIL_PARAMCHECK,"统计日期不能为空");
        }
        if(staff_id ==null){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"员工id不能为空");
        }
        if(store_id ==null){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"门店id不能为空");
        }
        staffBalanceSummaryMapper.updateReferralStatisData(statis_date,staff_id);
        storeBalanceSummaryMapper.updateReferralStatisData(statis_date,store_id);
        return new Result(CodeEnum.SUCCESS);
    }
}
