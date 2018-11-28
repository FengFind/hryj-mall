package com.hryj.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.cache.CodeCache;
import com.hryj.cache.LoginCache;
import com.hryj.cacheutil.ProductBrandCacheHandler;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.constant.CommonConstantPool;
import com.hryj.entity.bo.product.Brand;
import com.hryj.entity.bo.product.PartyProduct;
import com.hryj.entity.bo.product.ProductInfo;
import com.hryj.entity.bo.promotion.ActivityInfo;
import com.hryj.entity.bo.promotion.ActivityProductItem;
import com.hryj.entity.bo.promotion.ActivityScopeItem;
import com.hryj.entity.bo.staff.store.StoreInfo;
import com.hryj.entity.bo.staff.warehouse.WarehouseInfo;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.product.common.response.ProductBrand;
import com.hryj.entity.vo.product.response.app.AppProdListItemResponseVO;
import com.hryj.entity.vo.promotion.activity.request.*;
import com.hryj.entity.vo.promotion.activity.response.*;
import com.hryj.entity.vo.staff.dept.response.DeptGroupResponseVO;
import com.hryj.entity.vo.staff.team.StaffStoreWhVO;
import com.hryj.entity.vo.staff.user.StaffAdminLoginVO;
import com.hryj.entity.vo.user.UserLoginVO;
import com.hryj.entity.vo.user.UserPartyVO;
import com.hryj.entity.vo.user.UserServiceRangeVO;
import com.hryj.exception.ServerException;
import com.hryj.feign.DeptFeignClient;
import com.hryj.mapper.*;
import com.hryj.service.util.ConstantsConfig;
import com.hryj.service.util.ServiceInvoker;
import com.hryj.threadpool.ThreadPoolUtil;
import com.hryj.utils.UtilValidate;
import com.hryj.worktask.ActivityDisableTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author 汪豪
 * @className: ActivityService
 * @description: 活动相关Service
 * @create 2018/7/4 0004 21:53
 **/
@Slf4j
@Service
public class ActivityService extends ServiceImpl<ActivityMapper,ActivityInfo> {

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ActivityScopeItemMapper activityScopeItemMapper;
    
    @Autowired
    private ActivityProductItemMapper activityProductItemMapper;

    @Autowired
    private PartyProductOtherMapper partyProductMapper;
    
    @Autowired
    private ProductInfoMapper productInfoMapper;

    @Autowired
    private PStoreInfoMapper pStoreInfoMapper;

    @Autowired
    private PWarehouseInfoMapper pWarehouseInfoMapper;

    @Autowired
    private DeptFeignClient deptFeignClient;

    @Autowired
    private ProductBrandMapper brandMapper;

    /**
     * @author 汪豪
     * @methodName: searchPromotionActivityPage
     * @methodDesc: 分页查询促销活动
     * @description:
     * @param: [searchPromotionActivityRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.promotion.activity.response.SearchPromotionActivityItemResponseVO>>
     * @create 2018-06-30 9:20
     **/
    public Result<PageResponseVO<SearchPromotionActivityItemResponseVO>> searchPromotionActivityPage(SearchPromotionActivityRequestVO searchPromotionActivityRequestVO) {
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

        Date currDate = new Date();
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.lt("end_date",currDate);
        wrapper.eq("activity_status",1);
        wrapper.eq("audit_status",1);
        List<ActivityInfo> activityInfoList = activityMapper.selectList(wrapper);
        if(CollectionUtil.isNotEmpty(activityInfoList)){
            for (ActivityInfo activityInfo : activityInfoList) {
                ActivityInfo updateActivity = new ActivityInfo();
                updateActivity.setId(activityInfo.getId());
                updateActivity.setActivity_status(0);
                activityMapper.updateById(updateActivity);
            }
        }
        Map<String,Object> param = new HashMap<>();
        param.put("activity_name",searchPromotionActivityRequestVO.getActivity_name());
        param.put("activity_type",searchPromotionActivityRequestVO.getActivity_type());
        param.put("audit_status",searchPromotionActivityRequestVO.getAudit_status());
        param.put("activity_status",searchPromotionActivityRequestVO.getActivity_status());
        param.put("start_date_begin",searchPromotionActivityRequestVO.getStart_date_begin());
        param.put("start_date_end",searchPromotionActivityRequestVO.getStart_date_end());
        param.put("end_date_begin",searchPromotionActivityRequestVO.getEnd_date_begin());
        param.put("end_date_end",searchPromotionActivityRequestVO.getEnd_date_end());
        param.put("operator_name",searchPromotionActivityRequestVO.getOperator_name());
        param.put("create_time_begin",searchPromotionActivityRequestVO.getCreate_time_begin());
        param.put("create_time_end",searchPromotionActivityRequestVO.getCreate_time_end());
        param.put("audit_time_begin",searchPromotionActivityRequestVO.getAudit_time_begin());
        param.put("audit_time_end",searchPromotionActivityRequestVO.getAudit_time_end());
        param.put("party_id_list",searchPromotionActivityRequestVO.getParty_id_list());
        param.put("s_w_id_list",s_w_id_list);
        if(CollectionUtil.isNotEmpty(searchPromotionActivityRequestVO.getParty_id_list())){
            param.put("party_id_list_size",searchPromotionActivityRequestVO.getParty_id_list().size());
        }
        log.info("分页查询促销活动:searchPromotionActivityRequestVO====="+JSON.toJSONString(searchPromotionActivityRequestVO));
        Page page = new Page(searchPromotionActivityRequestVO.getPage_num(),searchPromotionActivityRequestVO.getPage_size());
        List<SearchPromotionActivityItemResponseVO> records = activityMapper.searchPromotionActivityPage(param,page);
        PageResponseVO pageResponseVO = new PageResponseVO();
        pageResponseVO.setRecords(records);
        pageResponseVO.setTotal_page(page.getPages());
        pageResponseVO.setTotal_count(page.getTotal());
        log.info("分页查询促销活动:result====="+JSON.toJSONString(pageResponseVO));
        return new Result<>(CodeEnum.SUCCESS,pageResponseVO);
    }

    /**
     * @author 汪豪
     * @methodName: getPromotionActivity
     * @methodDesc: 根据活动ID返回活动详细信息
     * @description: include_party是否返回参与门店或仓库，true返回，false不返回， include_party是否返回参与产品信息，true返回，false不返回,include_audit_record是否返回审核记录，true返回，false不返回
     * @param: [promotionActivityDetailRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.promotion.activity.response.PromotionActivityDataResponseVO>
     * @create 2018-07-06 10:22
     **/
    public Result<PromotionActivityDataResponseVO> getPromotionActivity(PromotionActivityDetailRequestVO promotionActivityDetailRequestVO) {
        log.info("根据活动ID返回活动详细信息:promotionActivityDetailRequestVO====="+JSON.toJSONString(promotionActivityDetailRequestVO));
        if(promotionActivityDetailRequestVO.getActivity_id() == null || promotionActivityDetailRequestVO.getActivity_id() <= 0L){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"促销活动id为空");
        }
        PromotionActivityDataResponseVO responseVO = activityMapper.getPromotionActivityById(promotionActivityDetailRequestVO.getActivity_id());
        if("01".equals(responseVO.getActivity_style())){
            responseVO.setTemplete_data("");
        }
        if(responseVO == null){
            return new Result<>(CodeEnum.FAIL_BUSINESS,"ID为"+promotionActivityDetailRequestVO.getActivity_id()+"的促销活动不存在");
        }
        if(promotionActivityDetailRequestVO.getInclude_party()){
            //返回参与门店或仓库
            PromotionActivityJoinPartyDataResponseVO promotionActivityJoinPartyDataResponseVO = new PromotionActivityJoinPartyDataResponseVO();
            Page page = new Page(promotionActivityJoinPartyDataResponseVO.getPage_num(),promotionActivityJoinPartyDataResponseVO.getPage_size());
            List<JoinPartyItemResponseVO> partyItemResponseVOS = null;
            if("02".equals(responseVO.getActivity_scope())){
                partyItemResponseVOS = activityMapper.getPartyStoreDataByActivityId(promotionActivityDetailRequestVO.getActivity_id(),page);
            }else{
                partyItemResponseVOS = activityMapper.getPartyWarehouseDataByActivityId(promotionActivityDetailRequestVO.getActivity_id(),page);
            }
            promotionActivityJoinPartyDataResponseVO.setTotal_page((int)page.getPages());
            promotionActivityJoinPartyDataResponseVO.setTotal_count((int)page.getTotal());
            promotionActivityJoinPartyDataResponseVO.setParty_list(partyItemResponseVOS);
            responseVO.setJoin_party_data(promotionActivityJoinPartyDataResponseVO);
        }

        if(promotionActivityDetailRequestVO.getInclude_product()){
            //返回参与产品信息
            PromotionActivityJoinProductDataResponseVO productDataResponseVO = new PromotionActivityJoinProductDataResponseVO();
            List<JoinProductItemResponseVO> partyProductDataResponseVOs = activityMapper.getPartyProductDataByActivityId(promotionActivityDetailRequestVO.getActivity_id());
            for (JoinProductItemResponseVO partyProductDataResponseVO : partyProductDataResponseVOs) {
                Brand brand = brandMapper.selectById(partyProductDataResponseVO.getBrand_id());
                if(brand != null){
                    ProductBrand productBrand = new ProductBrand();
                    productBrand.setId(brand.getId());
                    productBrand.setBrand_name(brand.getBrand_name());
                    productBrand.setLogo_image(brand.getLogo_image());
                    partyProductDataResponseVO.setBrand(productBrand);
                }
            }
            productDataResponseVO.setProduct_list(partyProductDataResponseVOs);
            responseVO.setJoin_product_data(productDataResponseVO);
        }

        if(promotionActivityDetailRequestVO.getInclude_audit_record()){
            //返回审核记录
            List<PromotionActivityAuditRecordResponseVO> activityAuditRecord = activityMapper.getActivityAuditRecordByActivityId(promotionActivityDetailRequestVO.getActivity_id());
            responseVO.setAudit_record_list(activityAuditRecord);
        }
        log.info("根据活动ID返回活动详细信息:result====="+JSON.toJSONString(responseVO));
        return new Result<>(CodeEnum.SUCCESS,responseVO);
    }

    /**
     * @author 汪豪
     * @methodName: searchPromotionActivityJoinPartyData
     * @methodDesc: 查询活动参与门店或仓库信息
     * @description: 参与门店可能比较多，是以分页查询方式处理
     * @param: [searchActivityJoinPartyRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.promotion.activity.response.PromotionActivityJoinPartyDataResponseVO>>
     * @create 2018-06-30 9:21
     **/
    public Result<PageResponseVO<PromotionActivityJoinPartyDataResponseVO>> searchPromotionActivityJoinPartyData(SearchActivityJoinPartyRequestVO searchActivityJoinPartyRequestVO) {
        log.info("查询活动参与门店或仓库信息:searchActivityJoinPartyRequestVO====="+JSON.toJSONString(searchActivityJoinPartyRequestVO));
        if(searchActivityJoinPartyRequestVO.getActivity_id() == null  || searchActivityJoinPartyRequestVO.getActivity_id() <= 0L){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"促销活动id不能为空");
        }
        ActivityInfo activityInfo = activityMapper.selectById(searchActivityJoinPartyRequestVO.getActivity_id());
        if(activityInfo == null){
            return new Result<>(CodeEnum.FAIL_BUSINESS,"ID为"+searchActivityJoinPartyRequestVO.getActivity_id()+"的促销活动不存在");
        }
        Page page = new Page(searchActivityJoinPartyRequestVO.getPage_num(),searchActivityJoinPartyRequestVO.getPage_size());
        List<JoinPartyItemResponseVO> partyDataResponseVO = null;
        if("02".equals(activityInfo.getActivity_scope())){
            //查询的为门店
            partyDataResponseVO = activityMapper.getPartyStoreDataByPage(searchActivityJoinPartyRequestVO, page);
        }else{
            partyDataResponseVO = activityMapper.getPartyWarehouseDataByPage(searchActivityJoinPartyRequestVO, page);
        }
        PromotionActivityJoinPartyDataResponseVO promotionActivityJoinPartyDataResponseVO = new PromotionActivityJoinPartyDataResponseVO();
        promotionActivityJoinPartyDataResponseVO.setParty_list(partyDataResponseVO);
        List<PromotionActivityJoinPartyDataResponseVO> records = new ArrayList();
        records.add(promotionActivityJoinPartyDataResponseVO);
        PageResponseVO pageResponseVO = new PageResponseVO();
        pageResponseVO.setRecords(records);
        pageResponseVO.setTotal_count(page.getTotal());
        pageResponseVO.setTotal_page(page.getPages());
        log.info("查询活动参与门店或仓库信息:result====="+JSON.toJSONString(pageResponseVO));
        return new Result<>(CodeEnum.SUCCESS,pageResponseVO);
    }

    /**
     * @author 汪豪
     * @methodName: searchPromotionActivityJoinProductData
     * @methodDesc: 查询活动参与产品信息
     * @description: 参与产品可能比较多，是以分页查询方式处理
     * @param: [searchActivityJoinProductRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.promotion.activity.response.JoinProductItemResponseVO>>
     * @create 2018-06-30 9:22
     **/
    public Result<PageResponseVO<JoinProductItemResponseVO>> searchPromotionActivityJoinProductData(SearchActivityJoinProductRequestVO searchActivityJoinProductRequestVO) {
        log.info("查询活动参与产品信息:searchActivityJoinProductRequestVO====="+JSON.toJSONString(searchActivityJoinProductRequestVO));
        if(searchActivityJoinProductRequestVO.getActivity_id() == null || searchActivityJoinProductRequestVO.getActivity_id() <= 0L){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"促销活动id不能为空");
        }

        Page page = new Page(searchActivityJoinProductRequestVO.getPage_num(),searchActivityJoinProductRequestVO.getPage_size());
        List<JoinProductItemResponseVO> records = activityMapper.getPromotionActivityJoinProductData(searchActivityJoinProductRequestVO,page);
        for (JoinProductItemResponseVO partyProductDataResponseVO : records) {
            Brand brand = brandMapper.selectById(partyProductDataResponseVO.getBrand_id());
            if(brand != null){
                ProductBrand productBrand = new ProductBrand();
                productBrand.setId(brand.getId());
                productBrand.setBrand_name(brand.getBrand_name());
                productBrand.setLogo_image(brand.getLogo_image());
                partyProductDataResponseVO.setBrand(productBrand);
            }
        }
        PageResponseVO pageResponseVO = new PageResponseVO();
        pageResponseVO.setTotal_page(page.getPages());
        pageResponseVO.setTotal_count(page.getTotal());
        pageResponseVO.setRecords(records);
        log.info("查询活动参与产品信息:result====="+JSON.toJSONString(pageResponseVO));
        return new Result<>(CodeEnum.SUCCESS,pageResponseVO);
    }

    /**
     * @author 汪豪
     * @methodName: findActivityAuditRecord
     * @methodDesc: 查询活动的审核记录信息
     * @description:
     * @param: [onlyActivityIdRequestVo]
     * @return com.hryj.common.Result<com.hryj.entity.vo.promotion.activity.response.PromotionActivityAuditRecordResponseVO>
     * @create 2018-06-28 17:08
     **/
    public Result<ListResponseVO<PromotionActivityAuditRecordResponseVO>> findPromotionActivityAuditRecord(OnlyActivityIdRequestVO onlyActivityIdRequestVo) {
        log.info("查询活动的审核记录信息:onlyActivityIdRequestVo====="+JSON.toJSONString(onlyActivityIdRequestVo));
        if(onlyActivityIdRequestVo.getActivity_id() == null || onlyActivityIdRequestVo.getActivity_id() <= 0L){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"促销活动id不能为空");
        }
        ActivityInfo activityInfo = activityMapper.selectById(onlyActivityIdRequestVo.getActivity_id());
        if(activityInfo == null){
            return new Result<>(CodeEnum.FAIL_BUSINESS,"ID为"+onlyActivityIdRequestVo.getActivity_id()+"的促销活动不存在");
        }
        if(activityInfo.getAudit_status() == 0){
            return new Result<>(CodeEnum.FAIL_BUSINESS,"该促销活动还未审核");
        }
        List<PromotionActivityAuditRecordResponseVO> records = activityMapper.getActivityAuditRecordByActivityId(onlyActivityIdRequestVo.getActivity_id());
        log.info("查询活动的审核记录信息:result====="+JSON.toJSONString(records));
        return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>(records));
    }

    /**
     * @author 汪豪
     * @methodName: saveCreateActivity
     * @methodDesc: 保存创建一个新的促销活动
     * @description:
     * @param: [promotionActitivyRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-28 11:36
     **/
    @Transactional
    public Result saveCreatePromotionActivity(PromotionActivityRequestVO promotionActitivyRequestVO) {
        log.info("保存创建一个新的促销活动:promotionActitivyRequestVO====="+JSON.toJSONString(promotionActitivyRequestVO));
        StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(promotionActitivyRequestVO.getLogin_token());
        if(staffAdminLoginVO == null){
            return new Result<>(CodeEnum.FAIL_BUSINESS, "获取用户登陆信息失败, token=" + promotionActitivyRequestVO.getLogin_token());
        }
        log.info("保存创建一个新的促销活动:操作人====="+JSON.toJSONString(staffAdminLoginVO));
        String storeIdList = staffAdminLoginVO.getStoreIdList();
        log.info("保存创建一个新的促销活动:操作人storeIdList====="+JSON.toJSONString(storeIdList));
        String whIdList = staffAdminLoginVO.getWhIdList();
        log.info("保存创建一个新的促销活动:操作人whIdList====="+JSON.toJSONString(whIdList));
        if(StrUtil.isEmpty(promotionActitivyRequestVO.getActivity_name())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动名称不能为空");
        }
        if(StrUtil.isEmpty(promotionActitivyRequestVO.getStart_date())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动开始时间不能为空");
        }
        if(StrUtil.isEmpty(promotionActitivyRequestVO.getEnd_date())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动结束时间不能为空");
        }
        if(StrUtil.isEmpty(promotionActitivyRequestVO.getActivity_image())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动banner图URL不能为空");
        }
        if("02".equals(promotionActitivyRequestVO.getActivity_style())){
            if(StrUtil.isEmpty(promotionActitivyRequestVO.getTemplete_data())){
                return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动模板内容不能为空");
            }
        }
        if(StrUtil.isEmpty(promotionActitivyRequestVO.getActivity_detail())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动详情不能为空");
        }
        if(StrUtil.isEmpty(promotionActitivyRequestVO.getActivity_scope())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动范围不能为空");
        }
        if(!promotionActitivyRequestVO.getAll_party()){
            if(CollectionUtil.isEmpty(promotionActitivyRequestVO.getParty_id_list())){
                return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动范围的门店或仓库ID集合不能为空");
            }else{
                if("01".equals(promotionActitivyRequestVO.getActivity_scope())){
                    for (Long party_id : promotionActitivyRequestVO.getParty_id_list()) {
                        WarehouseInfo warehouseInfo = pWarehouseInfoMapper.selectById(party_id);
                        if(warehouseInfo == null){
                            return new Result<>(CodeEnum.FAIL_BUSINESS,"id为"+party_id+"的仓库不存在");
                        }
                    }
                }else{
                    for (Long party_id : promotionActitivyRequestVO.getParty_id_list()) {
                        StoreInfo storeInfo = pStoreInfoMapper.selectById(party_id);
                        if(storeInfo == null){
                            return new Result<>(CodeEnum.FAIL_BUSINESS,"id为"+party_id+"的门店不存在");
                        }
                    }
                }
            }
        }else{
            if("02".equals(promotionActitivyRequestVO.getActivity_scope())){
                if(StrUtil.isEmpty(storeIdList)){
                    return new Result<>(CodeEnum.FAIL_BUSINESS,"该员工权限下没有门店");
                }
            }
            if("01".equals(promotionActitivyRequestVO.getActivity_scope())){
                if(StrUtil.isEmpty(whIdList)){
                    return new Result<>(CodeEnum.FAIL_BUSINESS,"该员工权限下没有仓库");
                }
            }
        }
        List<PromotionActivityProductItemRequestVO> product_list = promotionActitivyRequestVO.getProduct_list();
        if("01".equals(promotionActitivyRequestVO.getActivity_style())){
            if(CollectionUtil.isEmpty(product_list)){
                return new Result<>(CodeEnum.FAIL_PARAMCHECK,"参加活动的商品ID和活动价格集合不能为空");
            }
            for (PromotionActivityProductItemRequestVO promotionActivityProductItemRequestVO : product_list) {
                if(promotionActivityProductItemRequestVO.getProduct_id() == null){
                    return new Result<>(CodeEnum.FAIL_PARAMCHECK,"参加活动的商品ID不能为空");
                }
                if(StrUtil.isEmpty(promotionActivityProductItemRequestVO.getActivity_price())){
                    return new Result<>(CodeEnum.FAIL_PARAMCHECK,"参加活动的商品活动价格不能为空");
                }
                ProductInfo productInfo = productInfoMapper.selectById(promotionActivityProductItemRequestVO.getProduct_id());
                if(NumberUtil.isLess(new BigDecimal(promotionActivityProductItemRequestVO.getActivity_price()),productInfo.getCost_price())){
                    return new Result<>(CodeEnum.FAIL_BUSINESS,"活动价不能小于成本价");
                }
            }
        }
        //新增促销活动信息
        ActivityInfo activityInfo = new ActivityInfo();
        activityInfo.setActivity_name(promotionActitivyRequestVO.getActivity_name());
        activityInfo.setStart_date(DateUtil.parse(promotionActitivyRequestVO.getStart_date(),"yyyy-MM-dd HH:mm:ss"));
        activityInfo.setEnd_date(DateUtil.parse(promotionActitivyRequestVO.getEnd_date(),"yyyy-MM-dd HH:mm:ss"));
        activityInfo.setActivity_image(promotionActitivyRequestVO.getActivity_image());
        if(StrUtil.isEmpty(promotionActitivyRequestVO.getActivity_style())){
            activityInfo.setActivity_style("01");
        }else{
            activityInfo.setActivity_style(promotionActitivyRequestVO.getActivity_style());
        }
        if(StrUtil.isEmpty(promotionActitivyRequestVO.getActivity_style()) || "01".equals(promotionActitivyRequestVO.getActivity_style())){
            activityInfo.setTemplete_data(CodeCache.getValueByKey("ActivityPagePrefixUrl", "S01"));
            //activityInfo.setTemplete_data(ConstantsConfig.JUMP_ACTIVITY_DETAIL_URL);
        }else{
            activityInfo.setTemplete_data(promotionActitivyRequestVO.getTemplete_data());
        }
        activityInfo.setActivity_detail(promotionActitivyRequestVO.getActivity_detail());
        activityInfo.setActivity_scope(promotionActitivyRequestVO.getActivity_scope());
        if(StrUtil.isEmpty(promotionActitivyRequestVO.getActivity_type())){
            activityInfo.setActivity_type("01");
        }else{
            activityInfo.setActivity_type(promotionActitivyRequestVO.getActivity_type());
        }
        if("01".equals(promotionActitivyRequestVO.getActivity_type())){
            activityInfo.setActivity_mark_image(ConstantsConfig.MARK_IMAGE_HOT_URL);
        }
        activityInfo.setActivity_status(1); //活动状态:1-正常,0-关闭
        activityInfo.setAudit_status(0);    //活动审核状态:0-待审核,1-通过,2-不通过
        if(promotionActitivyRequestVO.getAll_party()){
            activityInfo.setAll_party(1);
        }else {
            activityInfo.setAll_party(0);
        }
        activityInfo.setOperator_id(staffAdminLoginVO.getStaff_id());
        activityInfo.setOperator_name(staffAdminLoginVO.getStaff_name());
        activityMapper.insert(activityInfo);
        //新增活动参与门店或仓库对应关系
        List<Long> partyIds = promotionActitivyRequestVO.getParty_id_list();
        if(!promotionActitivyRequestVO.getAll_party()){
            for (Long partyId : partyIds) {
                ActivityScopeItem activityScopeItem = new ActivityScopeItem();
                activityScopeItem.setActivity_id(activityInfo.getId());
                activityScopeItem.setParty_id(partyId);
                activityScopeItem.setStart_date(activityInfo.getStart_date());
                activityScopeItem.setEnd_date(activityInfo.getEnd_date());
                activityScopeItemMapper.insert(activityScopeItem);
            }
        }else{
            if("02".equals(promotionActitivyRequestVO.getActivity_scope())){
                String[] ids = storeIdList.split(",");
                for(String id : ids){
                    ActivityScopeItem activityScopeItem = new ActivityScopeItem();
                    activityScopeItem.setActivity_id(activityInfo.getId());
                    activityScopeItem.setParty_id(Long.parseLong(id));
                    activityScopeItem.setStart_date(activityInfo.getStart_date());
                    activityScopeItem.setEnd_date(activityInfo.getEnd_date());
                    activityScopeItemMapper.insert(activityScopeItem);
                }
            }else{
                String[] ids = whIdList.split(",");
                for(String id : ids){
                    ActivityScopeItem activityScopeItem = new ActivityScopeItem();
                    activityScopeItem.setActivity_id(activityInfo.getId());
                    activityScopeItem.setParty_id(Long.parseLong(id));
                    activityScopeItem.setStart_date(activityInfo.getStart_date());
                    activityScopeItem.setEnd_date(activityInfo.getEnd_date());
                    activityScopeItemMapper.insert(activityScopeItem);
                }
            }
        }
        //新增活动参与商品对应关系
        if("01".equals(promotionActitivyRequestVO.getActivity_style())){
            Long sort_num = 0L;
            for (int i = 0; i < product_list.size(); i++){
                PromotionActivityProductItemRequestVO activityProductItemRequestVO = product_list.get(i);
                ActivityProductItem activityProductItem = new ActivityProductItem();
                activityProductItem.setActivity_id(activityInfo.getId());
                activityProductItem.setProduct_id(activityProductItemRequestVO.getProduct_id());
                BigDecimal bd_activity_price = new BigDecimal(activityProductItemRequestVO.getActivity_price());
                activityProductItem.setActivity_price(bd_activity_price);
                sort_num = sort_num + 1;
                activityProductItem.setSort_num(sort_num);
                activityProductItemMapper.insert(activityProductItem);

            }
        }
        return new Result<>(CodeEnum.SUCCESS);
    }

    /**
     * @author 汪豪
     * @methodName: updateActivity
     * @methodDesc: 保存修改一个促销活动
     * @description: 活动审核通过后不能再进行修改，活动基本信息采用更新方式，参与门店或仓库，参与商品以先删除后新增的方式处理
     * @param: [promotionActitivyRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-28 11:37
     **/
    @Transactional
    public Result updatePromotionActivity(PromotionActivityRequestVO promotionActitivyRequestVO) {
        log.info("修改一个促销活动:promotionActitivyRequestVO====="+JSON.toJSONString(promotionActitivyRequestVO));
        StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(promotionActitivyRequestVO.getLogin_token());
        if(staffAdminLoginVO == null){
            return new Result<>(CodeEnum.FAIL_BUSINESS, "获取用户登陆信息失败, token=" + promotionActitivyRequestVO.getLogin_token());
        }
        String storeIdList = staffAdminLoginVO.getStoreIdList();
        String whIdList = staffAdminLoginVO.getWhIdList();
        log.info("保存创建一个新的促销活动:操作人====="+JSON.toJSONString(staffAdminLoginVO));
        log.info("保存创建一个新的促销活动:操作人storeIdList====="+JSON.toJSONString(storeIdList));
        log.info("保存创建一个新的促销活动:操作人whIdList====="+JSON.toJSONString(whIdList));
        if (promotionActitivyRequestVO.getActivity_id() == null || promotionActitivyRequestVO.getActivity_id() <= 0L){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动id不能为空");
        }
        ActivityInfo activityInfo = activityMapper.selectById(promotionActitivyRequestVO.getActivity_id());
        if(activityInfo.getAudit_status() == 1){
            return new Result<>(CodeEnum.SUCCESS,"该活动已审核通过，不可修改");
        }
        if(StrUtil.isEmpty(promotionActitivyRequestVO.getActivity_name())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动名称不能为空");
        }
        if(StrUtil.isEmpty(promotionActitivyRequestVO.getStart_date())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动开始时间不能为空");
        }
        if(StrUtil.isEmpty(promotionActitivyRequestVO.getEnd_date())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动结束时间不能为空");
        }
        if(StrUtil.isEmpty(promotionActitivyRequestVO.getActivity_image())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动banner图URL不能为空");
        }
        if("02".equals(promotionActitivyRequestVO.getActivity_style())){
            if(StrUtil.isEmpty(promotionActitivyRequestVO.getTemplete_data())){
                return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动模板内容不能为空");
            }
        }
        if(StrUtil.isEmpty(promotionActitivyRequestVO.getActivity_detail())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动详情不能为空");
        }
        if(StrUtil.isEmpty(promotionActitivyRequestVO.getActivity_scope())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动范围不能为空");
        }

        if(!promotionActitivyRequestVO.getAll_party()){
            if(CollectionUtil.isEmpty(promotionActitivyRequestVO.getParty_id_list())){
                return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动范围的门店或仓库ID集合不能为空");
            }else{
                if("01".equals(promotionActitivyRequestVO.getActivity_scope())){
                    for (Long party_id : promotionActitivyRequestVO.getParty_id_list()) {
                        WarehouseInfo warehouseInfo = pWarehouseInfoMapper.selectById(party_id);
                        if(warehouseInfo == null){
                            return new Result<>(CodeEnum.FAIL_BUSINESS,"id为"+party_id+"的仓库不存在");
                        }
                    }
                }else{
                    for (Long party_id : promotionActitivyRequestVO.getParty_id_list()) {
                        StoreInfo storeInfo = pStoreInfoMapper.selectById(party_id);
                        if(storeInfo == null){
                            return new Result<>(CodeEnum.FAIL_BUSINESS,"id为"+party_id+"的门店不存在");
                        }
                    }
                }
            }
        }else{
            if("02".equals(promotionActitivyRequestVO.getActivity_scope())){
                if(StrUtil.isEmpty(storeIdList)){
                    return new Result<>(CodeEnum.FAIL_BUSINESS,"该员工权限下没有门店");
                }
            }
            if("01".equals(promotionActitivyRequestVO.getActivity_scope())){
                if(StrUtil.isEmpty(whIdList)){
                    return new Result<>(CodeEnum.FAIL_BUSINESS,"该员工权限下没有仓库");
                }
            }

        }
        List<PromotionActivityProductItemRequestVO> product_list = promotionActitivyRequestVO.getProduct_list();
        if("01".equals(promotionActitivyRequestVO.getActivity_style())){
            if(CollectionUtil.isEmpty(product_list)){
                return new Result<>(CodeEnum.FAIL_PARAMCHECK,"参加活动的商品ID和活动价格集合不能为空");
            }
            for (PromotionActivityProductItemRequestVO promotionActivityProductItemRequestVO : product_list) {
                if(promotionActivityProductItemRequestVO.getProduct_id() == null){
                    return new Result<>(CodeEnum.FAIL_PARAMCHECK,"参加活动的商品ID不能为空");
                }
                if(StrUtil.isEmpty(promotionActivityProductItemRequestVO.getActivity_price())){
                    return new Result<>(CodeEnum.FAIL_PARAMCHECK,"参加活动的商品活动价格不能为空");
                }
                ProductInfo productInfo = productInfoMapper.selectById(promotionActivityProductItemRequestVO.getProduct_id());
                if(NumberUtil.isLess(new BigDecimal(promotionActivityProductItemRequestVO.getActivity_price()),productInfo.getCost_price())){
                    return new Result<>(CodeEnum.FAIL_BUSINESS,"活动价不能小于成本价");
                }
            }
        }
        //活动基本信息
        activityInfo.setActivity_name(promotionActitivyRequestVO.getActivity_name());
        activityInfo.setStart_date(DateUtil.parse(promotionActitivyRequestVO.getStart_date(),"yyyy-MM-dd HH:mm:ss"));
        activityInfo.setEnd_date(DateUtil.parse(promotionActitivyRequestVO.getEnd_date(),"yyyy-MM-dd HH:mm:ss"));
        activityInfo.setActivity_image(promotionActitivyRequestVO.getActivity_image());
        activityInfo.setActivity_style(promotionActitivyRequestVO.getActivity_style());
        activityInfo.setAudit_status(0);
        if("01".equals(promotionActitivyRequestVO.getActivity_style())){
            activityInfo.setTemplete_data(CodeCache.getValueByKey("ActivityPagePrefixUrl", "S01"));
            //activityInfo.setTemplete_data(ConstantsConfig.JUMP_ACTIVITY_DETAIL_URL);
        }else{
            activityInfo.setTemplete_data(promotionActitivyRequestVO.getTemplete_data());
        }
        activityInfo.setActivity_detail(promotionActitivyRequestVO.getActivity_detail());
        activityInfo.setActivity_scope(promotionActitivyRequestVO.getActivity_scope());
        activityInfo.setActivity_type(promotionActitivyRequestVO.getActivity_type());
        if("01".equals(promotionActitivyRequestVO.getActivity_type())){
            activityInfo.setActivity_mark_image(ConstantsConfig.MARK_IMAGE_HOT_URL);
        }
        if(promotionActitivyRequestVO.getAll_party()){
            activityInfo.setAll_party(1);
        }else {
            activityInfo.setAll_party(0);
        }
        activityMapper.updateById(activityInfo);
        //参与活动的门店或仓库
        EntityWrapper activityScopeWrapper = new EntityWrapper();
        activityScopeWrapper.eq("activity_id",activityInfo.getId());
        activityScopeItemMapper.delete(activityScopeWrapper);
        List<Long> partyIds = promotionActitivyRequestVO.getParty_id_list();
        if(!promotionActitivyRequestVO.getAll_party()){
            for (Long partyId : partyIds) {
                ActivityScopeItem activityScopeItem = new ActivityScopeItem();
                activityScopeItem.setActivity_id(activityInfo.getId());
                activityScopeItem.setParty_id(partyId);
                activityScopeItem.setStart_date(activityInfo.getStart_date());
                activityScopeItem.setEnd_date(activityInfo.getEnd_date());
                activityScopeItemMapper.insert(activityScopeItem);
            }
        }else{
            if("02".equals(promotionActitivyRequestVO.getActivity_scope())){
                String[] storeIds = storeIdList.split(",");
                for(String id : storeIds){
                    ActivityScopeItem activityScopeItem = new ActivityScopeItem();
                    activityScopeItem.setActivity_id(activityInfo.getId());
                    activityScopeItem.setParty_id(Long.parseLong(id));
                    activityScopeItem.setStart_date(activityInfo.getStart_date());
                    activityScopeItem.setEnd_date(activityInfo.getEnd_date());
                    activityScopeItemMapper.insert(activityScopeItem);
                }
            }else{
                String[] whIds = whIdList.split(",");
                for(String id : whIds){
                    ActivityScopeItem activityScopeItem = new ActivityScopeItem();
                    activityScopeItem.setActivity_id(activityInfo.getId());
                    activityScopeItem.setParty_id(Long.parseLong(id));
                    activityScopeItem.setStart_date(activityInfo.getStart_date());
                    activityScopeItem.setEnd_date(activityInfo.getEnd_date());
                    activityScopeItemMapper.insert(activityScopeItem);
                }
            }
        }

        //参与活动的商品
        //List<PromotionActivityProductItemRequestVO> product_list = promotionActitivyRequestVO.getProduct_list();
        if("01".equals(promotionActitivyRequestVO.getActivity_style()) && CollectionUtil.isNotEmpty(product_list)){
            EntityWrapper activityProductWrapper = new EntityWrapper();
            activityProductWrapper.eq("activity_id",activityInfo.getId());
            activityProductItemMapper.delete(activityProductWrapper);
            Long sort_num = 0L;
            for (int i = 0; i < product_list.size(); i++){
                PromotionActivityProductItemRequestVO activityProductItemRequestVO = product_list.get(i);
                ActivityProductItem activityProductItem = new ActivityProductItem();
                activityProductItem.setActivity_id(activityInfo.getId());
                activityProductItem.setProduct_id(activityProductItemRequestVO.getProduct_id());
                BigDecimal bd_activity_price = new BigDecimal(activityProductItemRequestVO.getActivity_price());
                activityProductItem.setActivity_price(bd_activity_price);
                sort_num = sort_num + 1;
                activityProductItem.setSort_num(sort_num);
                activityProductItemMapper.insert(activityProductItem);

            }
        }else if ("02".equals(promotionActitivyRequestVO.getActivity_style())){
            EntityWrapper activityProductWrapper = new EntityWrapper();
            activityProductWrapper.eq("activity_id",activityInfo.getId());
            activityProductItemMapper.delete(activityProductWrapper);
        }
        return new Result<>(CodeEnum.SUCCESS);
    }

    /**
     * @author 汪豪
     * @methodName: updateActivityBase
     * @methodDesc: 修改活动的基本信息
     * @description: 活动审核通过后不能再进行修改
     * @param: [promotionActivityBaseRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-28 11:44
     **/
    @Transactional
    public Result updatePromotionActivityBase(PromotionActivityBaseRequestVO promotionActivityBaseRequestVO) {
        log.info("修改活动的基本信息:promotionActivityBaseRequestVO====="+JSON.toJSONString(promotionActivityBaseRequestVO));
        if (promotionActivityBaseRequestVO.getActivity_id() == null || promotionActivityBaseRequestVO.getActivity_id() <= 0L){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动id不能为空");
        }
        ActivityInfo activityInfo = activityMapper.selectById(promotionActivityBaseRequestVO.getActivity_id());
        if(activityInfo.getAudit_status() == 1){
            return new Result<>(CodeEnum.SUCCESS,"该活动已审核通过，不可修改");
        }
        if(StrUtil.isEmpty(promotionActivityBaseRequestVO.getActivity_name())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动名称不能为空");
        }
        if(StrUtil.isEmpty(promotionActivityBaseRequestVO.getStart_date())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动开始时间不能为空");
        }
        if(StrUtil.isEmpty(promotionActivityBaseRequestVO.getEnd_date())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动结束时间不能为空");
        }
        if(StrUtil.isEmpty(promotionActivityBaseRequestVO.getActivity_image())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动banner图URL不能为空");
        }
        if("01".equals(promotionActivityBaseRequestVO.getActivity_style())){
            if(StrUtil.isEmpty(promotionActivityBaseRequestVO.getTemplete_data())){
                return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动模板内容不能为空");
            }
        }
        if(StrUtil.isEmpty(promotionActivityBaseRequestVO.getActivity_detail())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动详情不能为空");
        }
        if(StrUtil.isEmpty(promotionActivityBaseRequestVO.getActivity_style())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动样式不能为空");
        }
        activityInfo.setActivity_name(promotionActivityBaseRequestVO.getActivity_name());
        if(StrUtil.isNotEmpty(promotionActivityBaseRequestVO.getStart_date())){
            activityInfo.setStart_date(DateUtil.parse(promotionActivityBaseRequestVO.getStart_date(),"yyyy-MM-dd HH:mm:ss"));
        }
        if(StrUtil.isNotEmpty(promotionActivityBaseRequestVO.getEnd_date())) {
            activityInfo.setEnd_date(DateUtil.parse(promotionActivityBaseRequestVO.getEnd_date(), "yyyy-MM-dd HH:mm:ss"));
        }
        activityInfo.setActivity_image(promotionActivityBaseRequestVO.getActivity_image());
        activityInfo.setActivity_style(promotionActivityBaseRequestVO.getActivity_style());
        if("01".equals(promotionActivityBaseRequestVO.getActivity_style())){
            activityInfo.setTemplete_data(CodeCache.getValueByKey("ActivityPagePrefixUrl", "S01"));
            //activityInfo.setTemplete_data(ConstantsConfig.JUMP_ACTIVITY_DETAIL_URL);
        }else{
            activityInfo.setTemplete_data(promotionActivityBaseRequestVO.getTemplete_data());
        }
        activityInfo.setActivity_detail(promotionActivityBaseRequestVO.getActivity_detail());
        activityMapper.updateById(activityInfo);
        return new Result<>(CodeEnum.SUCCESS);
    }

    /**
     * @author 汪豪
     * @methodName: deleteOnePartyFormScope
     * @methodDesc: 删除一个指定的活动应用范围（门店或仓库）
     * @description: 活动审核通过后不能删除
     * @param: [activityScopeItemIdRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-28 11:47
     **/
    @Transactional
    public Result deleteOnePartyFormScope(ActivityScopeItemIdRequestVO activityScopeItemIdRequestVO) {
        log.info("删除一个指定的活动应用范围（门店或仓库）:activityScopeItemIdRequestVO====="+JSON.toJSONString(activityScopeItemIdRequestVO));
        if(activityScopeItemIdRequestVO.getActivity_scope_item_id() == null || activityScopeItemIdRequestVO.getActivity_scope_item_id() <= 0L){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动范围id不能为空");
        }
        ActivityScopeItem activityScopeItem = activityScopeItemMapper.selectById(activityScopeItemIdRequestVO.getActivity_scope_item_id());
        ActivityInfo activityInfo = activityMapper.selectById(activityScopeItem.getActivity_id());
        if(activityInfo.getAudit_status() == 1){
            return new Result<>(CodeEnum.SUCCESS,"该活动已审核通过，不可修改");
        }
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("id",activityScopeItemIdRequestVO.getActivity_scope_item_id());
        activityScopeItemMapper.delete(wrapper);
        return new Result<>(CodeEnum.SUCCESS);
    }

    /**
     * @author 汪豪
     * @methodName: deleteOneProductFormScope
     * @methodDesc: 删除一个指定的活动参与商品
     * @description: 活动审核通过后不能删除
     * @param: [activityProductItemIdRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-28 11:52
     **/
    @Transactional
    public Result deleteOneProductFormScope(PromotionActivityAppendProdRequestVO promotionActivityAppendProdRequestVO) {
        log.info("删除一个指定的活动参与商品:promotionActivityAppendProdRequestVO====="+JSON.toJSONString(promotionActivityAppendProdRequestVO));
        if(promotionActivityAppendProdRequestVO.getActivity_id() == null || promotionActivityAppendProdRequestVO.getActivity_id() <= 0L){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动id不能为空");
        }
        if(CollectionUtil.isEmpty(promotionActivityAppendProdRequestVO.getProduct_list())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"商品集合不能为空");
        }
        ActivityInfo activityInfo = activityMapper.selectById(promotionActivityAppendProdRequestVO.getActivity_id());
        if(activityInfo.getAudit_status() == 1){
            return new Result<>(CodeEnum.FAIL_BUSINESS,"该活动已审核通过，不可修改");
        }
        for(PromotionActivityProductItemRequestVO promotionActivityProductItemRequestVO : promotionActivityAppendProdRequestVO.getProduct_list() ){
            if(promotionActivityProductItemRequestVO.getProduct_id() != null){
                EntityWrapper wrapper = new EntityWrapper();
                wrapper.eq("activity_id", promotionActivityAppendProdRequestVO.getActivity_id());
                wrapper.eq("product_id",promotionActivityProductItemRequestVO.getProduct_id());
                //wrapper.eq("activity_price",promotionActivityProductItemRequestVO.getActivity_price());
                activityProductItemMapper.delete(wrapper);
            }
        }
        /*EntityWrapper selectWrapper = new EntityWrapper();
        selectWrapper.eq("activity_id", promotionActivityAppendProdRequestVO.getActivity_id());
        selectWrapper.orderBy("sort_num",true);
        List<ActivityProductItem> list = activityProductItemMapper.selectList(selectWrapper);
        if(CollectionUtil.isNotEmpty(list)){
            for (int i = 0; i < list.size(); i++) {
                ActivityProductItem item = list.get(i);
                item.setSort_num(i+1L);
                activityProductItemMapper.updateById(item);
            }
        }*/
        return new Result<>(CodeEnum.SUCCESS);
    }

    /**
     * @author 汪豪
     * @methodName: appendManyProductToActivity
     * @methodDesc: 向活动追加商品
     * @description: 活动审核通过后不能追加商品
     * @param: [promotionActivityAppendProdRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-28 11:56
     **/
    @Transactional
    public Result appendManyProductToActivity(PromotionActivityAppendProdRequestVO promotionActivityAppendProdRequestVO) {
        log.info("向活动追加商品:promotionActivityAppendProdRequestVO====="+JSON.toJSONString(promotionActivityAppendProdRequestVO));
        if(promotionActivityAppendProdRequestVO.getActivity_id() == null || promotionActivityAppendProdRequestVO.getActivity_id() <= 0L){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动id不能为空");
        }
        ActivityInfo activityInfo = activityMapper.selectById(promotionActivityAppendProdRequestVO.getActivity_id());
        if(activityInfo.getAudit_status() == 1){
            return new Result<>(CodeEnum.SUCCESS,"该活动已审核通过，不可修改");
        }
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("activity_id",promotionActivityAppendProdRequestVO.getActivity_id());
        List<ActivityProductItem> productItems = activityProductItemMapper.selectList(wrapper);
        //现参与活动的商品id
        List<Long> productIdList = new ArrayList<>();
        //新追加的活动商品id
        List<Long> newPartyIds = new ArrayList<>();
        for (ActivityProductItem productItem : productItems) {
            productIdList.add(productItem.getProduct_id());
        }
        List<PromotionActivityProductItemRequestVO> product_list = promotionActivityAppendProdRequestVO.getProduct_list();
        for (PromotionActivityProductItemRequestVO promotionActivityProductItemRequestVO : product_list) {
            //验证商品状态
            ProductInfo productInfo = productInfoMapper.selectById(promotionActivityProductItemRequestVO.getProduct_id());
            if(productInfo != null && productInfo.getUp_down_status() != 0 && productInfo.getForbid_sale_flag() != 1){
                newPartyIds.add(promotionActivityProductItemRequestVO.getProduct_id());
            }
        }
        //去掉已在活动中的商品id
        newPartyIds.removeAll(productIdList);
        for(PromotionActivityProductItemRequestVO promotionActivityProductItemRequestVO :  product_list){
            if(newPartyIds.contains(promotionActivityProductItemRequestVO.getProduct_id())){
                ActivityProductItem activityProductItem = new ActivityProductItem();
                activityProductItem.setActivity_id(promotionActivityAppendProdRequestVO.getActivity_id());
                activityProductItem.setProduct_id(promotionActivityProductItemRequestVO.getProduct_id());
                BigDecimal bd = new BigDecimal(promotionActivityProductItemRequestVO.getActivity_price());
                activityProductItem.setActivity_price(bd);
                Long sort_num = activityProductItemMapper.getSortNumMaxByActivityId(promotionActivityAppendProdRequestVO.getActivity_id());
                activityProductItem.setSort_num(sort_num+1);
                activityProductItemMapper.insert(activityProductItem);
            }
        }
        return new Result<>(CodeEnum.SUCCESS);
    }

    /**
     * @author 汪豪
     * @methodName: upMoveOneJoinProduct
     * @methodDesc: 上移一个产品在活动中的排序位置
     * @description:
     * @param: [activityProductItemIdRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-07-05 21:40
     **/
    @Transactional
    public Result upMoveOneJoinProduct(ActivityProductItemIdRequestVO activityProductItemIdRequestVO) {
        log.info("上移一个产品在活动中的排序位置:activityProductItemIdRequestVO====="+JSON.toJSONString(activityProductItemIdRequestVO));
        if(activityProductItemIdRequestVO.getActivity_product_item_id() == null || activityProductItemIdRequestVO.getActivity_product_item_id() <= 0L){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动商品id不能为空");
        }
        ActivityProductItem item = activityProductItemMapper.selectById(activityProductItemIdRequestVO.getActivity_product_item_id());
        Long sortNumMax = activityProductItemMapper.getSortNumMaxByActivityId(item.getActivity_id());
        if(item.getSort_num() < sortNumMax){
            Long item_sort_num = item.getSort_num();

            ActivityProductItem item1 = activityProductItemMapper.getActivityProductItemBySortNumAndActivityId(item.getActivity_id(), item_sort_num + 1);
            item1.setSort_num(item_sort_num);
            activityProductItemMapper.updateById(item1);

            item.setSort_num(item_sort_num + 1);
            activityProductItemMapper.updateById(item);
        }
        return new Result<>(CodeEnum.SUCCESS);
    }

    /**
     * @author 汪豪
     * @methodName: downMoveJoinProduct
     * @methodDesc: 下移一个产品在活动中的排序位置
     * @description:
     * @param: [activityProductItemIdRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-07-05 21:40
     **/
    @Transactional
    public Result downMoveJoinProduct(ActivityProductItemIdRequestVO activityProductItemIdRequestVO) {
        log.info("下移一个产品在活动中的排序位置:activityProductItemIdRequestVO====="+JSON.toJSONString(activityProductItemIdRequestVO));
        if(activityProductItemIdRequestVO.getActivity_product_item_id() == null || activityProductItemIdRequestVO.getActivity_product_item_id() <= 0L){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动商品id不能为空");
        }
        ActivityProductItem item = activityProductItemMapper.selectById(activityProductItemIdRequestVO.getActivity_product_item_id());
        if(item.getSort_num() > 1){
            Long item_sort_num = item.getSort_num();
            ActivityProductItem item1 = activityProductItemMapper.getActivityProductItemBySortNumAndActivityId(item.getActivity_id(), item_sort_num - 1);
            item1.setSort_num(item_sort_num);
            activityProductItemMapper.updateById(item1);

            item.setSort_num(item_sort_num - 1);
            activityProductItemMapper.updateById(item);

        }
        return new Result<>(CodeEnum.SUCCESS);
    }

    /**
     * @author 汪豪
     * @methodName: findPromotionActivity
     * @methodDesc: APP端加载促销活动
     * @description: 不分页
     * @param: [requestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.promotion.activity.response.AppPromotionActivityResponseVO>
     * @create 2018-06-28 19:44
     **/
    public Result<PageResponseVO<AppPromotionActivityResponseVO>> findPromotionActivity(RequestVO requestVO) {
        UserLoginVO userLoginVO = LoginCache.getUserLoginVO(requestVO.getLogin_token());
        if(userLoginVO == null){
            return new Result<>(CodeEnum.FAIL_BUSINESS, "获取用户登陆信息失败, token=" + requestVO.getLogin_token());
        }
        log.info("APP端加载促销活动-用户登录token："+requestVO.getLogin_token());
        log.info("APP端加载活动的详细信息-当前登录用户信息："+JSON.toJSONString(userLoginVO));
        //获取覆盖用户的门店仓库 （从缓存获取）
        List<UserPartyVO> storeList = userLoginVO.getStoreList();
        log.info("APP端加载促销活动-用户周围门店（缓存）："+JSON.toJSONString(storeList));
        UserPartyVO wh = userLoginVO.getWarehouse();
        log.info("APP端加载促销活动-用户周围仓库（缓存）："+JSON.toJSONString(wh));
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
                return new Result<>(CodeEnum.FAIL_BUSINESS, "没有覆盖到当前用户的门店和仓库");
            }
            need_to_check = false;
            storeList = userServiceRangeVO.getStoreList();
            wh = userServiceRangeVO.getWarehouse();
            log.info("APP端加载促销活动-用户周围门店（接口）："+JSON.toJSONString(storeList));
            log.info("APP端加载促销活动-用户周围仓库（接口）："+JSON.toJSONString(wh));
        }

        String currTime = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        List<AppPromotionActivityResponseVO> appResponseVO = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(storeList)){
            storeList.sort(Comparator.comparing(UserPartyVO::getDistance));
            log.info("APP端加载促销活动-用户位置到门店的距离从近到远排序后："+JSON.toJSONString(storeList));
            for (UserPartyVO userPartyVO : storeList) {
                //判断 是否需要对门店的状态进行验证 （调用 接口返回的不需要验证）
                if(need_to_check){
                    List<Long> store_id_list = new ArrayList<>();
                    store_id_list.add(userPartyVO.getParty_id());
                    ListResponseVO<DeptGroupResponseVO> partyBaseInfo = ServiceInvoker.getPartyBaseInfo(deptFeignClient, store_id_list);
                    DeptGroupResponseVO deptGroupResponseVO = partyBaseInfo.getRecords().get(0);
                    if(deptGroupResponseVO.getDept_status() != null && deptGroupResponseVO.getDept_status() == 1){
                        List<AppPromotionActivityResponseVO> storeResponseVO = activityMapper.getActivityListToApp(userPartyVO.getParty_id(),currTime);
                        if(CollectionUtil.isNotEmpty(storeResponseVO)){
                            appResponseVO.addAll(storeResponseVO);
                        }
                    }
                }else{
                    List<AppPromotionActivityResponseVO> storeResponseVO = activityMapper.getActivityListToApp(userPartyVO.getParty_id(),currTime);
                    if(CollectionUtil.isNotEmpty(storeResponseVO)){
                        appResponseVO.addAll(storeResponseVO);
                    }
                }
            }
        }

        //判断 是否需要仓库的状态进行验证 （调用 接口返回的不需要验证）
        if(wh != null){
            if(need_to_check){
                List<Long> wh_id_list = new ArrayList<>();
                wh_id_list.add(wh.getParty_id());
                ListResponseVO<DeptGroupResponseVO> partyBaseInfo = ServiceInvoker.getPartyBaseInfo(deptFeignClient, wh_id_list);
                DeptGroupResponseVO deptGroupResponseVO = partyBaseInfo.getRecords().get(0);
                if(deptGroupResponseVO.getDept_status() != null && deptGroupResponseVO.getDept_status() == 1){
                    List<AppPromotionActivityResponseVO> whResponseVO = activityMapper.getActivityListToApp(wh.getParty_id(),currTime);
                    if(CollectionUtil.isNotEmpty(whResponseVO)){
                        appResponseVO.addAll(whResponseVO);
                    }
                }
            }else{
                List<AppPromotionActivityResponseVO> whResponseVO = activityMapper.getActivityListToApp(wh.getParty_id(),currTime);
                if(CollectionUtil.isNotEmpty(whResponseVO)){
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
        log.info("APP端加载活动信息:result=====："+JSON.toJSONString(appResponseVO));
        return new Result<>(CodeEnum.SUCCESS,pageResponseVO);
    }

    /**
     * @author 汪豪
     * @methodName: getPromotionActivityData
     * @methodDesc: APP端加载活动的详细信息
     * @description:
     * @param: [onlyActivityIdRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.promotion.activity.response.AppPromotionActivityDataResponseVO>
     * @create 2018-06-29 11:52
     **/
    public Result<AppPromotionActivityDataResponseVO> getPromotionActivityData(OnlyActivityIdRequestVO onlyActivityIdRequestVO) {
        if(onlyActivityIdRequestVO.getActivity_id() == null){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动id不能为空");
        }
        UserLoginVO userLoginVO = LoginCache.getUserLoginVO(onlyActivityIdRequestVO.getLogin_token());
        if(userLoginVO == null){
            return new Result<>(CodeEnum.FAIL_BUSINESS, "获取用户登陆信息失败, token=" + onlyActivityIdRequestVO.getLogin_token());
        }
        log.info("APP端加载活动的详细信息-当前登录用户信息："+JSON.toJSONString(userLoginVO));
        //获取覆盖用户的门店仓库 （从缓存获取）
        List<UserPartyVO> storeList = userLoginVO.getStoreList();
        log.info("APP端加载活动的详细信息-用户周围门店（缓存）："+JSON.toJSONString(storeList));
        UserPartyVO wh = userLoginVO.getWarehouse();
        log.info("APP端加载活动的详细信息-用户周围仓库（缓存）："+JSON.toJSONString(wh));
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
                return new Result<>(CodeEnum.FAIL_BUSINESS, "没有覆盖到当前用户的门店和仓库");
            }
            need_to_check = false;
            storeList = userServiceRangeVO.getStoreList();
            wh = userServiceRangeVO.getWarehouse();
            log.info("APP端加载活动的详细信息-用户周围门店（接口）："+JSON.toJSONString(storeList));
            log.info("APP端加载活动的详细信息-用户周围仓库（接口）："+JSON.toJSONString(wh));
        }
        AppPromotionActivityDataResponseVO promotionActivityData = activityMapper.getActivityDataByActivityIdToApp(onlyActivityIdRequestVO.getActivity_id());
        if(promotionActivityData == null){
            return new Result<>(CodeEnum.FAIL_BUSINESS,"id为"+onlyActivityIdRequestVO.getActivity_id()+"的活动不存在");
        }
        Long startDate = DateUtil.parse(promotionActivityData.getStart_date()).getTime();
        Long endDate = DateUtil.parse(promotionActivityData.getEnd_date()).getTime();
        Long currDate = new Date().getTime();
        if("0".equals(promotionActivityData.getActivity_status()) || currDate < startDate || currDate > endDate){
            promotionActivityData.setActivity_status("0");
            log.info("APP端加载活动的详细信息：result=====" + JSON.toJSONString(promotionActivityData));
            return new Result<>(CodeEnum.SUCCESS,promotionActivityData);
        }
        if("01".equals(promotionActivityData.getActivity_style())) {
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
        return new Result<>(CodeEnum.SUCCESS,promotionActivityData);
    }

    public Result<ListResponseVO<ActivityInProgressProductItemResponseVO>> activityJoinProductDetail(List<ActivityIdProductIdRequestVO> activityIdProductIdRequestVO) {
        if(CollectionUtil.isEmpty(activityIdProductIdRequestVO)){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"集合不能为空");
        }
        ListResponseVO listResponseVO = new ListResponseVO();
        List<ActivityInProgressProductItemResponseVO> records = new ArrayList<>();
        for (ActivityIdProductIdRequestVO requestVO : activityIdProductIdRequestVO) {
            if(requestVO != null && requestVO.getActivity_id() != null && requestVO.getProduct_id() != null){
                ActivityInfo activityInfo = activityMapper.selectById(requestVO.getActivity_id());
                if(activityInfo != null){
                    Date currDate = new Date();
                    if(activityInfo.getAudit_status() == 1 && activityInfo.getActivity_status() == 1 && currDate.compareTo(activityInfo.getStart_date()) != -1 && currDate.compareTo(activityInfo.getEnd_date()) != 1){
                        ActivityInProgressProductItemResponseVO responseVO = activityMapper.getPartyProductDataByCondition(requestVO);
                        if(responseVO != null){
                            responseVO.setActivity_status(1);
                            responseVO.setActivity_type_name(ActivityInfo.getActivityTypeName(responseVO.getActivity_type()));
                            records.add(responseVO);
                        }else{
                            //该商品不在活动中
                            responseVO = new ActivityInProgressProductItemResponseVO();
                            responseVO.setActivity_status(0);
                            records.add(responseVO);
                        }
                    }else{
                        //活动未进行或已结束 已停用
                        ActivityInProgressProductItemResponseVO responseVO = new ActivityInProgressProductItemResponseVO();
                        responseVO.setActivity_status(0);
                        records.add(responseVO);
                    }
                }else{
                    //活动不存在
                    ActivityInProgressProductItemResponseVO responseVO = new ActivityInProgressProductItemResponseVO();
                    responseVO.setActivity_status(0);
                    records.add(responseVO);
                }
            }
        }
        listResponseVO.setRecords(records);
        return new Result<>(CodeEnum.SUCCESS,listResponseVO);
    }

    @Transactional
    public Result disablePomotionActivity(OnlyActivityIdRequestVO onlyActivityIdRequestVO) {
        if(onlyActivityIdRequestVO.getActivity_id() == null || onlyActivityIdRequestVO.getActivity_id() <= 0L){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动id不能为空");
        }
        ActivityInfo activityInfo = activityMapper.selectById(onlyActivityIdRequestVO.getActivity_id());
        if(activityInfo == null){
            return new Result<>(CodeEnum.FAIL_BUSINESS,"ID为"+onlyActivityIdRequestVO.getActivity_id()+"的促销活动不存在");
        }
        //活动状态 1-正常 0-停用
        activityInfo.setActivity_status(CommonConstantPool.NUMBER_ZERO);
        activityMapper.updateById(activityInfo);

        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("activity_id",onlyActivityIdRequestVO.getActivity_id());
        List<ActivityScopeItem> scopeItems = activityScopeItemMapper.selectList(wrapper);

        //JOIN TASK WGY 活动停用启动任务影响商品搜索缓存
        if (UtilValidate.isNotEmpty(scopeItems)) {
            List<Long> party_id_list = new ArrayList<>(scopeItems.size());
            for (ActivityScopeItem scopeItem : scopeItems) {
                party_id_list.add(scopeItem.getParty_id());
            }
            ThreadPoolUtil.submitTask(new ActivityDisableTask(party_id_list, activityInfo.getStart_date(), activityInfo.getEnd_date()));
        }

        return new Result<>(CodeEnum.SUCCESS);
    }

    @Transactional
    public Result enablePomotionActivity(OnlyActivityIdRequestVO onlyActivityIdRequestVO) {
        if(onlyActivityIdRequestVO.getActivity_id() == null || onlyActivityIdRequestVO.getActivity_id() <= 0L){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动id不能为空");
        }
        ActivityInfo activityInfo = activityMapper.selectById(onlyActivityIdRequestVO.getActivity_id());
        if(activityInfo == null){
            return new Result<>(CodeEnum.FAIL_BUSINESS,"ID为"+onlyActivityIdRequestVO.getActivity_id()+"的促销活动不存在");
        }
        activityInfo.setActivity_status(1); //活动状态 1-正常 0-停用
        activityMapper.updateById(activityInfo);
        return new Result<>(CodeEnum.SUCCESS);
    }

    /**
     * @author 汪豪
     * @methodName: orderProductJoinActivityInfo
     * @methodDesc: 查询订单商品参与活动信息
     * @description:
     * @param: [partyProductActivityRequestVO]
     * @return com.hryj.common.Result<java.util.Map<java.lang.Long,java.util.List<com.hryj.entity.vo.promotion.activity.response.OrderActivityInfoVO>>>
     * @create 2018-08-16 10:24
     **/
    public Result<List<OrderActivityInfoResponseVO>> orderProductJoinActivityInfo(List<PartyProductActivityRequestVO> partyProductActivityRequestVO) {
        log.info("查询订单商品参与活动信息:partyProductActivityRequestVO======"+JSON.toJSONString(partyProductActivityRequestVO));
        for (PartyProductActivityRequestVO productActivityRequestVO : partyProductActivityRequestVO) {
            if (productActivityRequestVO.getParty_id() == null){
                new Result<>(CodeEnum.FAIL_PARAMCHECK,"门店或仓库id不能为空");
            }
            if (productActivityRequestVO.getProduct_id() == null){
                new Result<>(CodeEnum.FAIL_PARAMCHECK,"商品id不能为空");
            }
            if (productActivityRequestVO.getActivity_id() == null){
                new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动id不能为空");
            }
        }
        //查询结果集
        List<OrderActivityInfoResponseVO> orderActivityInfoResponseVOList = new ArrayList<>();
        //返回结果集
        //Map<Long,List<OrderActivityInfoResponseVO>> records = new HashMap<>();

        for (PartyProductActivityRequestVO productActivityRequestVO : partyProductActivityRequestVO) {
            OrderActivityInfoResponseVO orderActivityInfoResponseVO = activityMapper.getOrderProductJoinActivityInfo(productActivityRequestVO);
            if (orderActivityInfoResponseVO == null){
                return new Result<>(CodeEnum.FAIL_BUSINESS,new ArrayList<>());
            }
            orderActivityInfoResponseVOList.add(orderActivityInfoResponseVO);
        }
        //根据party_id分组封装返回结果
        /*for (OrderActivityInfoResponseVO orderActivityInfoResponseVO : orderActivityInfoResponseVOList) {
            boolean key_flag = records.containsKey(orderActivityInfoResponseVO.getParty_id());
            if(key_flag){
                records.get(orderActivityInfoResponseVO.getParty_id()).add(orderActivityInfoResponseVO);
            }else{
                List<OrderActivityInfoResponseVO> recordsItem = new ArrayList<>();
                recordsItem.add(orderActivityInfoResponseVO);
                records.put(orderActivityInfoResponseVO.getParty_id(),recordsItem);
            }
        }*/
        return new Result<>(CodeEnum.SUCCESS,orderActivityInfoResponseVOList);
    }

    /**
     * @author 汪豪
     * @methodName: getActivityInfoById
     * @methodDesc: 根据活动id查询活动信息
     * @description: 内部接口
     * @param: [partyProductActivityRequestVO]
     * @return com.hryj.common.Result<java.util.List<com.hryj.entity.vo.promotion.activity.response.OrderActivityInfoResponseVO>>
     * @create 2018-08-21 14:29
     **/
    public Result<List<OrderActivityInfoResponseVO>> getActivityInfoById(List<OnlyActivityIdRequestVO> onlyActivityIdRequestVOS) {
        log.info("查询订单商品参与活动信息:partyProductActivityRequestVO======"+JSON.toJSONString(onlyActivityIdRequestVOS));
        List<Long> activity_id_list = new ArrayList<>();
        for (OnlyActivityIdRequestVO vo : onlyActivityIdRequestVOS) {
            if (vo.getActivity_id() == null || vo.getActivity_id() == 0){
                new Result<>(CodeEnum.FAIL_PARAMCHECK,"活动id不能为空");
            }else{
                activity_id_list.add(vo.getActivity_id());
            }
        }
        List<OrderActivityInfoResponseVO> records = activityMapper.getActivityInfoById(activity_id_list);
        return new Result<>(CodeEnum.SUCCESS,records);
    }
}
