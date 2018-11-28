package com.hryj.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.IdWorker;
import com.hryj.cache.CodeCache;
import com.hryj.cache.LoginCache;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.common.SysCodeEnmu;
import com.hryj.constant.StaffConstants;
import com.hryj.entity.bo.staff.dept.DeptCost;
import com.hryj.entity.bo.staff.dept.DeptGroup;
import com.hryj.entity.bo.staff.dept.DeptShareConfig;
import com.hryj.entity.bo.staff.store.StoreDistributionArea;
import com.hryj.entity.bo.staff.store.StoreInfo;
import com.hryj.entity.bo.staff.user.StaffDeptRelation;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.staff.dept.request.DeptCostRequestVO;
import com.hryj.entity.vo.staff.dept.request.DeptShareConfigReqestVO;
import com.hryj.entity.vo.staff.dept.response.DeptShareListResponseVO;
import com.hryj.entity.vo.staff.store.request.*;
import com.hryj.entity.vo.staff.store.response.StoreInfoResponseVO;
import com.hryj.entity.vo.staff.store.response.StoreListResponseVO;
import com.hryj.entity.vo.staff.user.StaffAdminLoginVO;
import com.hryj.entity.vo.staff.user.response.StaffDeptRelationResponseVO;
import com.hryj.exception.GlobalException;
import com.hryj.feign.UserFeignClient;
import com.hryj.mapper.DeptGroupMapper;
import com.hryj.mapper.StoreInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author 代廷波
 * @className: StoreService
 * @description:
 * @create 2018/6/27 0027-19:31
 **/
@Slf4j
@Service
public class StoreService extends ServiceImpl<StoreInfoMapper, StoreInfo> {

    @Autowired
    private StoreInfoMapper storeInfoMapper;

    @Autowired
    private DeptGroupMapper deptGroupMapper;

    @Autowired
    private StaffDeptRelationService staffDeptRelationService;

    @Autowired
    private StoreDistributionAreaService storeDistributionAreaService;

    @Autowired
    private DeptShareConfigService deptShareConfigService;

    @Autowired
    private DeptCostService deptCostService;

    @Autowired
    private DeptService deptService;

    @Autowired
    private StaffDeptChangeRecordService staffDeptChangeRecordService;

    @Autowired
    private StaffLoginTokenService staffLoginTokenService;
    @Autowired
    private UserFeignClient userFeignClient;
    /**
     * @return com.hryj.common.Result
     * @author 代廷波
     * @description: 保存门店
     * @param: vo
     * @create 2018/07/21 20:41
     **/
    @Transactional
    public Result saveStore(StoreInfoRequestVO vo) {

        StaffAdminLoginVO loginVO = LoginCache.getStaffAdminLoginVO(vo.getLogin_token());
        if (null == loginVO) {
            return new Result(CodeEnum.FAIL_TOKEN_INVALID);
        }
        Long operator_id = loginVO.getStaff_id();
        log.info("保存门店：operator_id={},saveStore======{}", operator_id, JSON.toJSONString(vo));

        // 0.验证 ---分润列表是否为空
        if (CollectionUtil.isEmpty(vo.getDeptShareConfigVOList())) {

            return new Result(CodeEnum.FAIL_PARAMCHECK, "分润列表为空");
        }

        //验证---验证分润节点长度是否正确
        // 计算从当前分润节点 所有父节点长度=等于当前点节点等级
        int upDeptGroupSize = deptService.getDeptIdByleve(vo.getDept_id());
        if (vo.getDeptShareConfigVOList().size() != upDeptGroupSize) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "分润列表数据不全");
        }

        //分润校验
        String share = judgeStoreShareConfig(vo.getDeptShareConfigVOList());
        if (StrUtil.isNotEmpty(share)) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, share);
        }

        //验证---门店名称
        if (deptService.validatoDeptName(vo.getStore_name(), null)) {

            return new Result(CodeEnum.FAIL_PARAMCHECK, "门店名称重复");
        }
        //验证---基本信息
        String msg = validatorStore(vo);
        if (StrUtil.isNotEmpty(msg)) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, msg);
        }
        //验证---门店编号
        if (validatorStoreSum(vo.getStore_num(), null)) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "门店编号重复");
        }
        vo.setProvince_name(getCityName(vo.getProvince_code()));//所在省
        vo.setCity_name(getCityName(vo.getCity_code()));//所在市
        vo.setArea_name(getCityName(vo.getArea_code()));//所在区

        //1.保存组织
        Long store_id = saveDeptGroup(operator_id, vo);

        //2.保存门店信息
        saveStoreDet(store_id, operator_id, vo);

        //3.保存门店送货区块
        if (CollectionUtil.isNotEmpty(vo.getStoreDistributionAreaVOList())) {
            String addr = vo.getProvince_name() + vo.getCity_name() + vo.getArea_name();
            saveStoreDistributionArea(store_id, operator_id, vo.getStoreDistributionAreaVOList(), addr);
        }

        //4.保存门店员工
        saveStoreStaff(store_id, operator_id, vo.getStoreStaffRelationVOList(),vo.getService_rule());
        //5.保存分润
        saveStoreShareConfig(store_id, operator_id, vo.getDeptShareConfigVOList());
        //6 保存门店成本

        if (CollectionUtil.isNotEmpty(vo.getDeptCostVOList())) {
            saveStoreCost(store_id, operator_id, vo.getDeptCostVOList());
        }

        return new Result();
    }

    private String validatorStore(StoreInfoRequestVO vo) {
        String msg = null;
        /*if (StrUtil.isEmpty(vo.getStore_name())) {
            return "门店名为空";
        }*/
        if (StrUtil.isEmpty(vo.getProvince_code())) {
            return "省为空";
        }

       /* if (StrUtil.isEmpty(vo.getProvince_name())) {
            return "所在省名称为空";
        }*/

        if (StrUtil.isEmpty(vo.getCity_code())) {
            return "市为空";
        }

       /* if (StrUtil.isEmpty(vo.getCity_name())) {
            return "所在市名称为空";
        }*/

        if (StrUtil.isEmpty(vo.getArea_code())) {
            return "区为空";
        }

       /* if (StrUtil.isEmpty(vo.getArea_name())){
            return "所在区为空";
        }*/

       /* if (StrUtil.isEmpty(vo.getStreet_code())){
            return "所在街道代码";
        }

        if (StrUtil.isEmpty(vo.getStreet_name())){
            return "所在街道为空";
        }
*/
        if (StrUtil.isEmpty(vo.getDetail_address())) {
            return "详细地址为空";
        }

        if (StrUtil.isEmpty(vo.getLocations())) {
            return "门店坐标为空";
        }

        if (StrUtil.isEmpty(vo.getTelephone())) {
            return "联系电话为空";
        }
        /*if (StrUtil.isEmpty(vo.getContact_name())) {
            return "联系人为空";
        }*/
        if (StrUtil.isEmpty(vo.getBusiness_time_start())) {
            return "营业时间起为空";
        } else {
            Boolean b = ReUtil.isMatch(StaffConstants.TIME_REGEX, vo.getBusiness_time_start().toString());
            if (!b) {
                return "营业时间起格式不对";
            }
        }
        if (StrUtil.isEmpty(vo.getBusiness_time_end())) {
            return "营业时间止为空";
        } else {
            Boolean b = ReUtil.isMatch(StaffConstants.TIME_REGEX, vo.getBusiness_time_end().toString());
            if (!b) {
                return "营业时间止格式不对";
            }
        }
        if (StrUtil.isEmpty(vo.getStore_num())) {
            return "门店编号为空";
        }
        if (StrUtil.isEmpty(vo.getService_rule())) {
            return "服务提成规则为空";
        }
        if (StrUtil.isEmpty(vo.getShare_cost_flag().toString())) {
            return "是否分摊成本为空";
        }
        //成本
        if (CollectionUtil.isNotEmpty(vo.getDeptCostVOList())) {
            Boolean b;
            for (DeptCostRequestVO cost : vo.getDeptCostVOList()) {
                if (null == cost.getCost_amt()) {
                    msg = "成本没有值";
                    break;
                } else {

                    //验证两位有数字最大 99.99 最小0.00
                    b = ReUtil.isMatch(StaffConstants.NUMBER_REGEX, cost.getCost_amt().toString());
                    if (!b) {
                        msg = "成本数字无效";
                        break;
                    }
                }
            }
        }
        //判断店长 必须有且只能有一个店长       storeStaffRelationVOList
        if (CollectionUtil.isNotEmpty(vo.getStoreStaffRelationVOList())) {
            int job = 0;
            Boolean b = true;
            DeptGroup dept = null;
            BigDecimal service_ratio = new BigDecimal("0");//服务提成比例
            BigDecimal share_cost_ratio = new BigDecimal("0");//服务提成比例
            for (StoreStaffRelationRequestVO staff : vo.getStoreStaffRelationVOList()) {
                if (SysCodeEnmu.STAFFJOB_01.getCodeValue().equals(staff.getStaff_job())) {
                    ++job;
                }

                //普通员工
                if (SysCodeEnmu.SAFFTYPE_01.getCodeValue().equals(staff.getStaff_type())) {
                    if (null != staff.getSalary_amt()) {
                        b = ReUtil.isMatch(StaffConstants.NUMBER_REGEX, staff.getSalary_amt().toString());
                        if (!b) {
                            msg = "工资金额数字无效";
                            break;
                        }
                    } else {
                        msg = "工资金额为空";
                        break;
                    }
                    if (null != staff.getHelp_order_ratio()) {
                        b = ReUtil.isMatch(StaffConstants.NUMBER_REGEX, staff.getHelp_order_ratio().toString());
                        if (!b) {
                            msg = "代下单提成比例为数字无效";
                            break;
                        } else {
                            //大于时返回1,  等于时返回0, 小于时返回 -1
                            int i = staff.getHelp_order_ratio().compareTo(StaffConstants.DEFAULT_MAX_SHARE_RATIO);
                            if (i == 1) {
                                msg = "代下单提成比例不能大于100%";
                            }

                        }
                    } else {
                        if (!(SysCodeEnmu.STAFFJOB_01.getCodeValue().equals(staff.getStaff_job()))) {
                            msg = "代下单提成比例为数字为空";
                            break;
                        }
                    }

                    if (null != staff.getDistribution_amt()) {
                        b = ReUtil.isMatch(StaffConstants.NUMBER_REGEX, staff.getDistribution_amt().toString());
                        if (!b) {
                            msg = "配送提成数字无效";
                            break;
                        }
                    } else {
                        msg = "配送提成数字为空";
                        break;
                    }
                    if (null != staff.getService_ratio()) {
                        b = ReUtil.isMatch(StaffConstants.NUMBER_REGEX, staff.getService_ratio().toString());
                        if (!b) {
                            msg = "服务提成比例为数字无效";
                            break;
                        } else {
                            //大于时返回1,  等于时返回0, 小于时返回 -1
                            int i = staff.getService_ratio().compareTo(StaffConstants.DEFAULT_MAX_SHARE_RATIO);
                            if (i == 1) {
                                msg = "服务提成比例不能大于100%";
                            }
                        }
                        service_ratio=service_ratio.add(staff.getService_ratio());
                    }
                    if (null != staff.getShare_cost_ratio()) {
                        b = ReUtil.isMatch(StaffConstants.NUMBER_REGEX, staff.getShare_cost_ratio().toString());
                        if (!b) {
                            msg = "成本分摊比例为数字无效";
                            break;
                        } else {
                            //大于时返回1,  等于时返回0, 小于时返回 -1
                            int i = staff.getShare_cost_ratio().compareTo(StaffConstants.DEFAULT_MAX_SHARE_RATIO);
                            if (i == 1) {
                                msg = "成本分摊比例不能大于100%";
                            }

                        }
                        share_cost_ratio=share_cost_ratio.add(staff.getShare_cost_ratio());
                    }


                }
                if (null != staff.getStore_id()) {
                    if (staff.getStore_id().longValue() != vo.getStore_id().longValue()) {
                        dept = deptGroupMapper.selectById(staff.getStore_id());
                        if (null == dept) {
                            msg = "没有找到转移的门店";
                            break;
                        }
                        if (dept.getDept_status().equals(0)) {//部门状态:1-正常,0-关闭
                            msg = "转移到的门店以关闭";
                            break;
                        }
                        if (!dept.getDept_type().equals(SysCodeEnmu.DEPTETYPE_01.getCodeValue())) {
                            msg = "门店员工只能转移到门店";
                            break;
                        }
                    }
                }

            }
            if (job != 1) {
                return "一个门店只能存在一个店长";
            }
            if (null != msg) {
                return msg;
            }
            //大于时返回1,  等于时返回0, 小于时返回 -1
            if (SysCodeEnmu.SERVICE_RULE_01.getCodeValue().equals(vo.getService_rule()) &&
                    0!= service_ratio.compareTo(StaffConstants.DEFAULT_MAX_SHARE_RATIO)) {
                return "服务提成比例必需为100%";
            }
            if (0 != share_cost_ratio.compareTo(StaffConstants.DEFAULT_MAX_SHARE_RATIO)) {
                return "成本分摊比例必需为100%";
            }
        } else {
            return "门店必须有一位店长";
        }
        return null;
    }

    /**
     * @return boolean
     * @author 代廷波
     * @description: 验证门店编号
     * @param: store_num 门店编号
     * @param: store_id 门店id
     * @create 2018/08/06 16:53
     **/
    public boolean validatorStoreSum(String store_num, Long store_id) {
        EntityWrapper<StoreInfo> wrapper = new EntityWrapper<>();
        wrapper.eq("store_num", store_num);
        StoreInfo storeInfo = super.selectOne(wrapper);
        if (storeInfo != null && !storeInfo.getId().equals(store_id)) {
            return true;
        }
        return false;
    }

    /**
     * @return share_key分润比例是否正确, NUMBER_REGEX_key数字是否有效
     * @author 代廷波
     * @description: 验证分润比例 数字是否有效
     * @param: list
     * @create 2018/07/16 14:48
     **/
    public String judgeStoreShareConfig(List<DeptShareConfigReqestVO> list) {
        BigDecimal share_conut = new BigDecimal("0");
        String msg = null;
        Boolean b = false;
        for (DeptShareConfigReqestVO config : list) {
            //验证两位有数字最大 99.99 最小0.00
            if (null == config.getStaff_id() || config.getStaff_id().equals(0)) {//判断员工
                msg = "分润员工id为空";
                break;
            }
            if (null == config.getShare_ratio()) {//判断比例
                msg = "分润比例为空";
                break;
            } else {
                b = ReUtil.isMatch(StaffConstants.NUMBER_REGEX_TWO_DECIMAL, config.getShare_ratio().toString());
                if (!b) {
                    msg = "分润比例数字设置错误";
                    break;
                }
            }
            if (null == config.getDept_id() || config.getDept_id().equals(0)) {//判断部门id
                msg = "分润部门id为空";
                break;
            }

            share_conut = share_conut.add(config.getShare_ratio());
        }

        //大于时返回1,  等于时返回0, 小于时返回 -1
        int i = share_conut.compareTo(StaffConstants.DEFAULT_MAX_SHARE_RATIO);
        if (i != -1) {
            msg = "分润比例不能大于100%";
        }

        return msg;
    }

    /**
     * @return java.lang.Long
     * @author 代廷波
     * @description: 保存组织(新增或者修改)
     * @param: operator_id 操作人
     * @param: vo
     * @param: new_type操作类型  新增:new_type 修改:update_type
     * @create 2018/07/04 21:51
     **/
    public Long saveDeptGroup(Long operator_id, StoreInfoRequestVO vo) {

        //1.创建组织
        DeptGroup dept = new DeptGroup();
        dept.setDept_name(vo.getStore_name());//部门上级id
        dept.setEnd_flag(1);//门店设置为未节点店
        dept.setOperator_id(operator_id);//操作人
        dept.setDept_pid(vo.getDept_id());
        dept.setCompany_flag(0);//是否为区域公司:1-是,0-否
        dept.setDept_type(SysCodeEnmu.DEPTETYPE_01.getCodeValue());//部门类型:01-门店,02-仓库,03-普通部门
        dept.setDept_status(1);//门店状态:1-正常,0-关闭
        dept.setId(IdWorker.getId());
        deptGroupMapper.insert(dept);


        //2.获取部门上级等级和路径
        DeptGroup dept_parent = deptGroupMapper.selectById(vo.getDept_id());
        //3.设置当前组织的等级和路径
        dept.setDept_level(dept_parent.getDept_level() + 1);
        dept.setDept_path(dept_parent.getDept_path() + "," + dept.getId());
        Integer i = deptGroupMapper.updateById(dept);
        log.info("新增门店===保存门店:operator_id={},", operator_id, JSON.toJSONString(dept));
        return dept.getId();
    }

    /**
     * @return java.lang.Long
     * @author 代廷波
     * @description: 保存门店
     * @param: store_id 门店
     * @param: operator_id 操作人
     * @param: vo
     * @create 2018/07/04 20:03
     **/
    public void saveStoreDet(Long store_id, Long operator_id, StoreInfoRequestVO vo) {
        StoreInfo storeInfo = new StoreInfo();
        storeInfo.setId(store_id);//为门店在部门组织的id
        storeInfo.setProvince_code(vo.getProvince_code());//所在省代码
        storeInfo.setProvince_name(vo.getProvince_name());//所在省
        storeInfo.setCity_code(vo.getCity_code());//所在市代码
        storeInfo.setCity_name(vo.getCity_name());//所在市
        storeInfo.setArea_code(vo.getArea_code());//所在区代码
        storeInfo.setArea_name(vo.getArea_name());//所在区
        storeInfo.setStreet_code(vo.getStreet_code());//所在街道代码
        storeInfo.setStreet_name(vo.getStreet_name());//所在街道
        storeInfo.setDetail_address(vo.getDetail_address());//详细地址
        storeInfo.setLocations(vo.getLocations());//门店坐标,经纬度","分隔
        storeInfo.setTelephone(vo.getTelephone());//联系电话
        storeInfo.setContact_name(vo.getContact_name());//联系人
        storeInfo.setBusiness_time_start(vo.getBusiness_time_start());//营业时间起
        storeInfo.setBusiness_time_end(vo.getBusiness_time_end());//营业时间止
        storeInfo.setOpen_date(vo.getOpen_date());//开业时间

        storeInfo.setService_rule(vo.getService_rule());//服务提成规则:01-自定义,02-平均分配
        storeInfo.setShare_cost_flag(vo.getShare_cost_flag());//是否分摊成本1-分摊,0-不分摊
        storeInfo.setStore_num(vo.getStore_num());//门店编号
        storeInfo.setOperator_id(operator_id);//操作人id

        storeInfoMapper.insert(storeInfo);
        log.info("新增门店===保存门店:store_id={},operator_id={},del_staff_ids={}", store_id, operator_id, JSON.toJSONString(storeInfo));

    }

    /**
     * @return void
     * @author 代廷波
     * @description: 保存门店送货区块
     * @param: store_id 门店id
     * @param: distributionAreaVoList
     * @create 2018/07/04 20:30
     **/
    public void saveStoreDistributionArea(Long store_id, Long operator_id, List<StoreDistributionAreaRequestVO> distributionAreaVoList, String addr) {
        StoreDistributionArea area = null;
        List<StoreDistributionArea> list = new ArrayList<>();
        for (StoreDistributionAreaRequestVO vo : distributionAreaVoList) {
            area = new StoreDistributionArea();
            area.setDept_id(store_id);//门店id
            area.setPoi_id(vo.getId());//poi位置id
            area.setLocation_type(vo.getType());//位置类型:地图获取
            area.setLocation_name(vo.getName());//位置名称:地图获取
            area.setAddress(addr + vo.getAddress());//省市区+位置地址
            area.setLocations(vo.getLocations());//区域坐标,经纬度","分隔
            area.setDistance(vo.getDistance());//距离(米)
            area.setOperator_id(operator_id);//操作人id
            list.add(area);
        }
        if (list.size() > 0) {
            storeDistributionAreaService.insertBatch(list);
            log.info("新增门店===保存门店送货区块:store_id={},operator_id={},del_staff_ids={}", store_id, operator_id, JSON.toJSONString(list));
        }
    }

    /**
     * @return void
     * @author 代廷波
     * @description: 保存门店员工
     * @param: store_id 门店id
     * @param: operator_id 操作人
     * @param: staffDeptRelationVoList
     * @param: service_rule 服务提成规则:01-自定义,02-平均分配
     * @create 2018/07/04 20:52
     **/
    public void saveStoreStaff(Long store_id, Long operator_id, List<StoreStaffRelationRequestVO> staffDeptRelationVoList,String service_rule) {
        List<StaffDeptRelation> insert_list = new ArrayList<>();
        List<StaffDeptRelation> update_list = new ArrayList<>();
        Date date = new Date();
        StaffDeptRelation staff = null;
        Map<Long, Long> staff_ids_map = new HashMap<>();

        //判断员工是否以前离过职,离职的员工只需要将状态改回来
        int size = staffDeptRelationVoList.size();
        Long[] staff_ids = new Long[size];
        for (int i = 0; i < size; i++) {
            staff_ids[i] = staffDeptRelationVoList.get(i).getStaff_id();
        }
        EntityWrapper<StaffDeptRelation> staffInsert = new EntityWrapper<StaffDeptRelation>();
        staffInsert.in("staff_id", staff_ids);
        List<StaffDeptRelation> staffOdList = staffDeptRelationService.selectList(staffInsert);
        if (null != staffOdList && staffOdList.size() > 0) {
            for (StaffDeptRelation vo : staffOdList) {
                staff_ids_map.put(vo.getStaff_id(), vo.getId());//员工id,关系主键id
            }
        }

        for (StoreStaffRelationRequestVO vo : staffDeptRelationVoList) {
            staff = new StaffDeptRelation();
            staff.setStaff_id(vo.getStaff_id());//员工id
            staff.setDept_id(store_id);//门id
            staff.setStaff_job(vo.getStaff_job());//员工岗位
            staff.setUpdate_time(date);
            staff.setStaff_status(true);//员工状态:1-正常,0-离职
            staff.setOperator_id(operator_id);//操作人id
            if (SysCodeEnmu.SAFFTYPE_01.getCodeValue().equals(vo.getStaff_type())) {
                ////配送提成
                staff.setDistribution_amt(null == vo.getDistribution_amt() ? StaffConstants.DEFAULT_BIG_ZERO_NO : vo.getDistribution_amt());
                //工资金额
                staff.setSalary_amt(null == vo.getSalary_amt() ? StaffConstants.DEFAULT_BIG_ZERO_NO : vo.getSalary_amt());
                ////代下单提成比例
                staff.setHelp_order_ratio(null == vo.getHelp_order_ratio() ? StaffConstants.DEFAULT_BIG_ZERO_NO : vo.getHelp_order_ratio());
                //服务提成比例
                if(SysCodeEnmu.SERVICE_RULE_01.getCodeValue().equals(service_rule)){
                    staff.setService_ratio(null == vo.getService_ratio() ? StaffConstants.DEFAULT_BIG_ZERO_NO : vo.getService_ratio());
                }else{
                    staff.setService_ratio(StaffConstants.DEFAULT_BIG_ZERO_NO);
                }
                //成本分摊比例
               staff.setShare_cost_ratio(null == vo.getShare_cost_ratio() ? StaffConstants.DEFAULT_BIG_ZERO_NO : vo.getShare_cost_ratio());

            }

            //店长代下单为0.00
            if (null != vo.getStaff_job() && vo.getStaff_job().equals(SysCodeEnmu.STAFFJOB_01.getCodeValue())) {
                staff.setHelp_order_ratio(StaffConstants.DEFAULT_BIG_ZERO_NO);
            }
            if (staff_ids_map.containsKey(vo.getStaff_id())) {//数据库有的
                staff.setId(staff_ids_map.get(vo.getStaff_id()));//设置主键
                update_list.add(staff);
            } else {
                insert_list.add(staff);
            }
        }

        if (insert_list.size() > 0) {
            staffDeptRelationService.insertBatch(insert_list);
            log.info("新增门店===添加门店员工:store_id={},operator_id={},del_staff_ids={}", store_id, operator_id, JSON.toJSONString(insert_list));
        }
        if (update_list.size() > 0) {
            staffDeptRelationService.updateBatchById(update_list);
            log.info("新增门店===更新门店员工:store_id={},operator_id={},del_staff_ids={}", store_id, operator_id, JSON.toJSONString(insert_list));

        }

    }

    /**
     * @return void
     * @author 代廷波
     * @description: 保存修改门店员工
     * @param: store_id
     * @param: operator_id
     * @param: staffDeptRelationVoList
     * @create 2018/07/06 15:11
     **/

    public void updateStoreStaff(Long store_id, Long operator_id, List<StoreStaffRelationRequestVO> staffDeptRelationVoList,String service_rule) {

        List<StaffDeptRelation> insert_list = new ArrayList<>();//新增员工
        List<StaffDeptRelation> update_list = new ArrayList<>();//修改员工
        List<StaffDeptRelation> move_list = new ArrayList<>();//转移员工
        List<Long> toke_stff_ids = new ArrayList<>();//删除和转移员工
        StaffDeptRelation store_staff = null;//员工与组织对象
        Map<Long, Long> db_staff_list = new HashMap<>();//数据库中的员工列表:map.key=员工id,map.value=员工组织关系表主建id
        List<Long> new_stff_ids = new ArrayList<>();//前端传过来的新增员工id
        Map<Long, Long> update_staff_status_ids = new HashMap<>();//需要将离职的员工变成在职的员工
        Date date = new Date();
        EntityWrapper<StaffDeptRelation> store_wrapper = null;

        //如果是修改,先查找当前门店的员工,将当前门店的员工 id 放入map中
        store_wrapper = new EntityWrapper<StaffDeptRelation>();
        store_wrapper.where("dept_id={0}", store_id).and("staff_status={0}", 1);
        List<StaffDeptRelation> staffList = staffDeptRelationService.selectList(store_wrapper);//当前门店数据库中员工列表
        if (staffList.size() > 0) {
            for (StaffDeptRelation db : staffList) {
                db_staff_list.put(db.getStaff_id(), db.getId());//员工id
            }
        }

        //前端传过来的新增员工id
        for (StoreStaffRelationRequestVO storeList : staffDeptRelationVoList) {
            if (!db_staff_list.containsKey(storeList.getStaff_id())) {
                new_stff_ids.add(storeList.getStaff_id());
            }
        }

        //需要将离职的员工变成在职的员工
        if (null != new_stff_ids && new_stff_ids.size() > 0) {
            store_wrapper = new EntityWrapper<StaffDeptRelation>();
            store_wrapper.in("staff_id", new_stff_ids);
            List<StaffDeptRelation> staffOdList = staffDeptRelationService.selectList(store_wrapper);
            if (null != staffOdList && staffOdList.size() > 0) {
                for (StaffDeptRelation vo : staffOdList) {
                    update_staff_status_ids.put(vo.getStaff_id(), vo.getId());//员工id,关系主键id
                }
            }
        }
        for (StoreStaffRelationRequestVO vo : staffDeptRelationVoList) {
            store_staff = new StaffDeptRelation();
            store_staff.setStaff_job(vo.getStaff_job());//员工岗位
            store_staff.setStaff_status(true);//员工状态:1-正常,0-离职
            store_staff.setOperator_id(operator_id);//操作人id
            store_staff.setStaff_id(vo.getStaff_id());//设置员工id
            store_staff.setDept_id(store_id);//设置门店id
            store_staff.setUpdate_time(date);

            if (SysCodeEnmu.SAFFTYPE_01.getCodeValue().equals(vo.getStaff_type())) {
                //工资金额
                store_staff.setSalary_amt(null == vo.getSalary_amt() ? StaffConstants.DEFAULT_BIG_ZERO_NO : vo.getSalary_amt());
                ////配送提成
                store_staff.setDistribution_amt(null == vo.getDistribution_amt() ? StaffConstants.DEFAULT_BIG_ZERO_NO : vo.getDistribution_amt());
                ////代下单提成比例
                store_staff.setHelp_order_ratio(null == vo.getHelp_order_ratio() ? StaffConstants.DEFAULT_BIG_ZERO_NO : vo.getHelp_order_ratio());
                //服务提成比例
                if(SysCodeEnmu.SERVICE_RULE_01.getCodeValue().equals(service_rule)){
                    store_staff.setService_ratio(null == vo.getService_ratio() ? StaffConstants.DEFAULT_BIG_ZERO_NO : vo.getService_ratio());
                }else{
                    store_staff.setService_ratio(StaffConstants.DEFAULT_BIG_ZERO_NO);
                }
                //成本分摊比例
                store_staff.setShare_cost_ratio(null == vo.getShare_cost_ratio() ? StaffConstants.DEFAULT_BIG_ZERO_NO : vo.getShare_cost_ratio());
            }
            //店长代下单为0.00
            if (null != vo.getStaff_job() && vo.getStaff_job().equals(SysCodeEnmu.STAFFJOB_01.getCodeValue())) {
                store_staff.setHelp_order_ratio(StaffConstants.DEFAULT_BIG_ZERO_NO);
            }

            //部门有员工:判断员工是新增员工还是修改员工:map有表示为修改,没有为新增
            if (db_staff_list.size() > 0) {
                //当前数据库中有的员工
                if (db_staff_list.containsKey(vo.getStaff_id())) {
                    store_staff.setId(db_staff_list.get(vo.getStaff_id()));//设置主键
                    db_staff_list.remove(vo.getStaff_id());//删除 map中的员工id key
                    //判断是否是转移员工
                    if (null != vo.getStore_id() && (store_id.longValue() != vo.getStore_id().longValue())) {
                        store_staff.setDept_id(vo.getStore_id());
                        move_list.add(store_staff);
                        toke_stff_ids.add(vo.getStaff_id());
                    } else {
                        update_list.add(store_staff);
                    }
                } else {
                    //将离职员工变成在职员工
                    if (update_staff_status_ids.containsKey(vo.getStaff_id())) {//数据库有的
                        store_staff.setId(update_staff_status_ids.get(vo.getStaff_id()));//设置主键
                        update_list.add(store_staff);
                    } else {
                        insert_list.add(store_staff);
                    }
                }
                //部门没有员工全部是新增
            } else {
                //将离职员工变成在职员工
                if (update_staff_status_ids.containsKey(vo.getStaff_id())) {
                    store_staff.setId(update_staff_status_ids.get(vo.getStaff_id()));//设置主键
                    update_list.add(store_staff);
                } else {
                    insert_list.add(store_staff);
                }
            }
        }
        //map剩下的员工表示是要删除的员工
        if (db_staff_list.size() > 0) {
            List<StaffDeptRelation> del_list = new ArrayList<>();
            for (Map.Entry<Long, Long> entry : db_staff_list.entrySet()) {
                store_staff = new StaffDeptRelation();
                store_staff.setId(entry.getValue());
                store_staff.setStaff_id(entry.getKey());
                store_staff.setStaff_status(false);//员工状态:1-正常,0-离职
                store_staff.setStaff_job("");//删除员工清空员工类型
                del_list.add(store_staff);
                toke_stff_ids.add(entry.getKey());
            }
            if (del_list.size() > 0) {
                staffDeptRelationService.updateBatchById(del_list);
                //变动类型：01-员工离职，02-转移部门
                //添加离职员工记录
                staffDeptChangeRecordService.saveStaffDeptChangeRecord(del_list, "01", store_id);
                log.info("修改门店===删除门店员工:store_id={},operator_id={},del_staff_ids={}", store_id, operator_id, JSON.toJSONString(del_list));
            }

        }
        if (insert_list.size() > 0) {
            staffDeptRelationService.insertBatch(insert_list);
            log.info("修改门店===添加门店员工：store_id={},operator_id={},updateStore======{}", store_id, operator_id, JSON.toJSONString(insert_list));
        }
        if (update_list.size() > 0) {
            staffDeptRelationService.updateBatchById(update_list);
            log.info("修改门店===修改门店员工：store_id={},operator_id={},updateStore======{}", store_id, operator_id, JSON.toJSONString(update_list));
        }
        if (move_list.size() > 0) {
            staffDeptRelationService.updateBatchById(move_list);
            staffDeptChangeRecordService.saveStaffDeptChangeRecord(move_list, "02", store_id);
            log.info("修改门店===转移门店员工：store_id={},operator_id={},updateStore======{}", store_id, operator_id, JSON.toJSONString(move_list));
        }
        //删除和转移员工 清除token
        if (toke_stff_ids.size() > 0) {
            staffLoginTokenService.updateClearStaffLoginTokenBatch(toke_stff_ids);
        }
    }


    /**
     * @return void
     * @author 代廷波
     * @description: 保存门店分润
     * @param: store_id 门店id
     * @param: operator_id 操作人
     * @param: deptShareConfigReqestVOList
     * @create 2018/07/04 21:09
     **/
    public void saveStoreShareConfig(Long store_id, Long operator_id, List<DeptShareConfigReqestVO> deptShareConfigReqestVOList) {
        List<DeptShareConfig> list = new ArrayList<>();
        DeptShareConfig shareConfig = null;
        for (DeptShareConfigReqestVO vo : deptShareConfigReqestVOList) {
            shareConfig = new DeptShareConfig();
            shareConfig.setDept_id(vo.getDept_id());//部门id
            shareConfig.setStore_id(store_id);//门店id
            shareConfig.setShare_ratio(vo.getShare_ratio());//分润比例
            shareConfig.setStaff_id(vo.getStaff_id());//分润的员工id
            shareConfig.setOperator_id(operator_id);//操作人id
            list.add(shareConfig);
        }
        if (list.size() > 0) {
            deptShareConfigService.insertBatch(list);
            log.info("保存门店分润：operator_id={},updateStore======{}", operator_id, JSON.toJSONString(list));
        }

    }

    /**
     * @return void
     * @author 代廷波
     * @description: 保存门店成本
     * @param: store_id 门店id
     * @param: operator_id 操作人
     * @param: deptCostVoList
     * @create 2018/07/04 21:19
     **/
    public void saveStoreCost(Long store_id, Long operator_id, List<DeptCostRequestVO> deptCostVoList) {
        List<DeptCost> list = new ArrayList<>();
        DeptCost deptCost = null;

        for (DeptCostRequestVO vo : deptCostVoList) {
            deptCost = new DeptCost();
            deptCost.setDept_id(store_id);//部门id
            deptCost.setCost_amt(vo.getCost_amt());//成本金额
            deptCost.setCost_name(vo.getCost_name());//成本名称
            deptCost.setData_status(1);//数据状态:1-正常,0-删除
            deptCost.setOperator_id(operator_id);//操作人id
            list.add(deptCost);
        }

        if (list.size() > 0) {
            deptCostService.insertBatch(list);
            log.info("保存门店成本：operator_id={},updateStore======{}", operator_id, JSON.toJSONString(list));
        }


    }

    /**
     * @return com.hryj.common.Result
     * @author 代廷波
     * @description: 修改门店
     * @param: vo
     * @create 2018/07/21 20:40
     **/
    @Transactional
    public Result updateStore(StoreInfoRequestVO vo) {

        StaffAdminLoginVO loginVO = LoginCache.getStaffAdminLoginVO(vo.getLogin_token());
        if (null == loginVO) {
            return new Result(CodeEnum.FAIL_TOKEN_INVALID);
        }
        Long operator_id = loginVO.getStaff_id();
        log.info("修改门店：operator_id={},updateStore======{}", operator_id, JSON.toJSONString(vo));

        // 0.验证 ---分润列表是否为空       deptShareListVOList
        if (CollectionUtil.isEmpty(vo.getDeptShareConfigVOList())) {

            return new Result(CodeEnum.FAIL_PARAMCHECK, "分润列表为空");
        }
        //验证---验证分润节点长度是否正确
        // 计算从当前分润节点 所有父节点长度=等于当前点节点等级-1
        int upDeptGroupSize = deptService.getDeptIdByleve(vo.getDept_id());
        if (vo.getDeptShareConfigVOList().size() != upDeptGroupSize) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "分润列表数据不全");
        }
        //分润校验
        String share = judgeStoreShareConfig(vo.getDeptShareConfigVOList());
        if (StrUtil.isNotEmpty(share)) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, share);
        }

        //验证---门店名称
        if (deptService.validatoDeptName(vo.getStore_name(), vo.getStore_id())) {

            return new Result(CodeEnum.FAIL_PARAMCHECK, "门店名称重复");
        }

        //验证---基本信息
        String msg = validatorStore(vo);
        if (StrUtil.isNotEmpty(msg)) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, msg);
        }
        //验证---门店编号
        if (validatorStoreSum(vo.getStore_num(), vo.getStore_id())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "门店编号重复");
        }
        if (null == vo.getStore_id()) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "没有传门店id");
        }
        vo.setProvince_name(getCityName(vo.getProvince_code()));//所在省
        vo.setCity_name(getCityName(vo.getCity_code()));//所在市
        vo.setArea_name(getCityName(vo.getArea_code()));//所在区

        //1.保存组织
        Long store_id = updateDeptGroup(operator_id, vo);

        //2.保存门店信息
        updateStoreDet(operator_id, vo);

        //3.保存门店送货区块
        String addr = vo.getProvince_name() + vo.getCity_name() + vo.getArea_name();
        updateStoreDistributionArea(store_id, operator_id, vo.getStoreDistributionAreaVOList(), addr);


        //4.保存门店员工
        updateStoreStaff(store_id, operator_id, vo.getStoreStaffRelationVOList(),vo.getService_rule());
        //5.保存分润
        updateStoreShareConfig(store_id, operator_id, vo.getDeptShareConfigVOList());
        //6 保存门店成本
        updateStoreCost(store_id, operator_id, vo.getDeptCostVOList());

        //7、 更新用户常用门店或仓库的基本信息
        ThreadUtil.excAsync(() -> {
            userFeignClient.updateUserOftenPartyInfo(vo.getStore_id(),vo.getStore_name(),addr+vo.getDetail_address());
        },false);

        return new Result();
    }


    /**
     * @return java.lang.Long
     * @author 代廷波
     * @description: 保存修改组织信息
     * @param: operator_id
     * @param: vo
     * @create 2018/07/06 14:15
     **/
    public Long updateDeptGroup(Long operator_id, StoreInfoRequestVO vo) {


        //1.创建组织
        DeptGroup dept = new DeptGroup();


        //2.获取部门上级等级和路径
        DeptGroup dept_parent = deptGroupMapper.selectById(vo.getDept_id());
        //3.设置当前组织的等级和路径
        dept.setDept_level(dept_parent.getDept_level() + 1);
        dept.setDept_path(dept_parent.getDept_path() + "," + vo.getStore_id());

        dept.setDept_name(vo.getStore_name());//部门上级id
        dept.setEnd_flag(1);//门店设置为未节点店
        dept.setOperator_id(operator_id);//操作人
        dept.setDept_pid(vo.getDept_id());
        dept.setCompany_flag(0);//是否为区域公司:1-是,0-否
        dept.setDept_type("01");//部门类型:01-门店,02-仓库,03-普通部门
        dept.setId(vo.getStore_id());
        dept.setUpdate_time(new Date());

        deptGroupMapper.updateById(dept);
        log.info("保存修改组织信息:operator_id={},,deptCostVoList==={}", operator_id, JSON.toJSONString(dept));
        return dept.getId();
    }

    /**
     * @return void
     * @author 代廷波
     * @description: 修改门店基本信息
     * @param: operator_id 操作人
     * @param: vo
     * @create 2018/07/06 14:13
     **/
    public void updateStoreDet(Long operator_id, StoreInfoRequestVO vo) {

        StoreInfo storeInfo = new StoreInfo();
        storeInfo.setId(vo.getStore_id());//为门店在部门组织的id
        storeInfo.setProvince_code(vo.getProvince_code());//所在省代码
        storeInfo.setProvince_name(getCityName(vo.getProvince_code()));//所在省
        storeInfo.setCity_code(vo.getCity_code());//所在市代码
        storeInfo.setCity_name(getCityName(vo.getCity_code()));//所在市
        storeInfo.setArea_code(vo.getArea_code());//所在区代码
        storeInfo.setArea_name(getCityName(vo.getArea_code()));//所在区
        storeInfo.setStreet_code(vo.getStreet_code());//所在街道代码
        storeInfo.setStreet_name(vo.getStreet_name());//所在街道
        storeInfo.setDetail_address(vo.getDetail_address());//详细地址
        storeInfo.setLocations(vo.getLocations());//门店坐标,经纬度","分隔
        storeInfo.setTelephone(vo.getTelephone());//联系电话
        storeInfo.setContact_name(vo.getContact_name());//联系人
        storeInfo.setBusiness_time_start(vo.getBusiness_time_start());//营业时间起
        storeInfo.setBusiness_time_end(vo.getBusiness_time_end());//营业时间止
        storeInfo.setOperator_id(operator_id);//操作人id
        storeInfo.setOpen_date(vo.getOpen_date());//开业时间
        storeInfo.setService_rule(vo.getService_rule());//服务提成规则:01-自定义,02-平均分配
        storeInfo.setShare_cost_flag(vo.getShare_cost_flag());//是否分摊成本1-分摊,0-不分摊
        storeInfo.setUpdate_time(new Date());
        storeInfo.setStore_num(vo.getStore_num());//门店编号
        storeInfoMapper.updateById(storeInfo);
        log.info("修改门店基本信息:operator_id={},,deptCostVoList==={}", operator_id, JSON.toJSONString(storeInfo));

    }


    /**
     * @return void
     * @author 代廷波
     * @description: 保存门店送货区域
     * @param: store_id 门店id
     * @param: distributionAreaVoList
     * @create 2018/07/04 20:30
     **/
    public void updateStoreDistributionArea(Long store_id, Long operator_id, List<StoreDistributionAreaRequestVO> distributionAreaVoList, String addr) {

        //修改,先删除部门配送区域
        EntityWrapper<StoreDistributionArea> ew = new EntityWrapper<StoreDistributionArea>();
        ew.where("dept_id={0}", store_id);
        storeDistributionAreaService.delete(ew);
        if (CollectionUtil.isNotEmpty(distributionAreaVoList)) {
            log.info("门店送货区域请求数据yge:store_id={},deptCostVoList==={}",store_id, JSON.toJSONString(distributionAreaVoList));
            List<StoreDistributionArea> list = new ArrayList<>();
            StoreDistributionArea area = null;
            for (StoreDistributionAreaRequestVO vo : distributionAreaVoList) {
                area = new StoreDistributionArea();
                area.setPoi_id(null == vo.getId() ? "" : vo.getId());//poi位置id
                area.setLocation_type(null == vo.getType() ? "":vo.getType());//位置类型
                area.setLocation_name(null == vo.getName() ? "" :vo.getName());//位置名称
                area.setAddress(null == vo.getAddress() ? "" : (addr + vo.getAddress()));//省市区+位置地址
                area.setLocations(null == vo.getLocations() ? "" : vo.getLocations());//区域坐标,经纬度","分隔
                area.setDistance(null == vo.getDistance() ? BigDecimal.valueOf(0):vo.getDistance());//距离(米)
                area.setOperator_id(operator_id);//操作人id
                area.setDept_id(store_id);//门店id
                list.add(area);
            }
            if (list.size() > 0) {
                storeDistributionAreaService.insertBatch(list);
                log.info("门店送货区区域:operator_id={},store_id={},deptCostVoList==={}", operator_id, store_id, JSON.toJSONString(list));
            }
        }

    }

    /**
     * @return void
     * @author 代廷波
     * @description: 保存修改门店分润
     * @param: store_id 门店id
     * @param: operator_id 操作人
     * @param: deptShareConfigReqestVOList
     * @create 2018/07/04 21:09
     **/
    public void updateStoreShareConfig(Long store_id, Long operator_id, List<DeptShareConfigReqestVO> deptShareConfigReqestVOList) {
        List<DeptShareConfig> list = new ArrayList<>();
        //如果修改,先删除部门组织节点分润配置
        EntityWrapper<DeptShareConfig> ew = new EntityWrapper<DeptShareConfig>();
        ew.where("store_id={0}", store_id);
        deptShareConfigService.delete(ew);

        DeptShareConfig shareConfig = null;
        for (DeptShareConfigReqestVO vo : deptShareConfigReqestVOList) {
            shareConfig = new DeptShareConfig();
            shareConfig.setDept_id(vo.getDept_id());//部门id
            shareConfig.setShare_ratio(vo.getShare_ratio());//分润比例
            shareConfig.setStaff_id(vo.getStaff_id());//分润的员工id
            shareConfig.setOperator_id(operator_id);//操作人id
            shareConfig.setStore_id(store_id);//门店id

            list.add(shareConfig);
        }
        if (list.size() > 0) {
            deptShareConfigService.insertBatch(list);
            log.info("修改门店分润operator_id={},store_id={},deptCostVoList==={}", operator_id, store_id, JSON.toJSONString(deptShareConfigReqestVOList));
        }

    }

    public String getCityName(String code) {
        String srt = CodeCache.getNameByValue("CityArea", code);
        return srt;
    }

    /**
     * @return void
     * @author 代廷波
     * @description: 保存修改门店成本
     * @param: store_id 门店id
     * @param: operator_id操作人
     * @param: deptCostVoList
     * @create 2018/07/06 14:48
     **/
    public void updateStoreCost(Long store_id, Long operator_id, List<DeptCostRequestVO> deptCostVoList) {

        List<DeptCost> list = new ArrayList<>();
        DeptCost deptCost = null;

        /**
         * 修改逻辑:1.通过门店id查询当有效的成本列表,将id放成map中,以待删除用
         *         2.将前端传的 门店成本id 与map比较,看是否成在key,如果 map中有门店成本的id,表示未删除,将map中的成本庆id删除
         *         3.map中最后剩下的就是要删除的门店
         */
        //如果修改,先删除部门配送区域
        EntityWrapper<DeptCost> ew = new EntityWrapper<DeptCost>();
        ew.where("dept_id={0}", store_id).and("data_status=1");
        List<DeptCost> db_List = deptCostService.selectList(ew);
        //数据库数据成入map
        Map<Long, Long> map = null;
        if (null != db_List && db_List.size() > 0) {
            map = new HashMap<>();
            for (DeptCost cost : db_List) {
                map.put(cost.getId(), cost.getId());
            }
        }

        for (DeptCostRequestVO vo : deptCostVoList) {
            deptCost = new DeptCost();
            //有成本主键 id
            if (null != vo.getDept_cost_id()) {
                if (null != map) {
                    if (map.containsKey(vo.getDept_cost_id())) {
                        map.remove(vo.getDept_cost_id());
                    }
                }
            } else {//没有id新增
                deptCost.setDept_id(store_id);//部门id
                deptCost.setCost_amt(vo.getCost_amt());//成本金额
                deptCost.setCost_name(vo.getCost_name());//成本名称
                deptCost.setData_status(1);//数据状态:1-正常,0-删除
                deptCost.setOperator_id(operator_id);//操作人id
                list.add(deptCost);
            }
        }

        //删除部门成本
        if (null != map) {
            List<DeptCost> deptCostList = new ArrayList<>();
            Iterator<Map.Entry<Long, Long>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                deptCost = new DeptCost();
                Map.Entry<Long, Long> entry = iterator.next();
                deptCost.setId(entry.getValue());
                deptCost.setUpdate_time(new Date());
                deptCost.setData_status(0);//标记为删除
                deptCostList.add(deptCost);
            }
            if (deptCostList.size() > 0) {
                deptCostService.updateBatchById(deptCostList);
                log.info("删除成本operator_id={},store_id={},deptCostVoList==={}", operator_id, store_id, JSON.toJSONString(deptCostList));
            }
        }
        if (list.size() > 0) {
            deptCostService.insertBatch(list);
            log.info("添加成本operator_id={},store_id={},deptCostVoList==={}", operator_id, store_id, JSON.toJSONString(list));
        }


    }

    /**
     * @return com.hryj.common.Result
     * @author 代廷波
     * @description: 根据门店id查询详情
     * @param: Store_id 门店id
     * @create 2018/06/27 19:24
     **/
    public Result<StoreInfoResponseVO> getStoreIdByDet(StoreIdRequestVO vo) throws GlobalException {

        StoreInfoResponseVO storeInfoResponseVO = storeInfoMapper.getStoreIdByDet(vo);

        if (null != storeInfoResponseVO) {
            if (CollectionUtil.isNotEmpty(storeInfoResponseVO.getStoreStaffRelationVOList())) {
                for (StaffDeptRelationResponseVO staffDeptRelationResponseVO : storeInfoResponseVO.getStoreStaffRelationVOList()) {
                    staffDeptRelationResponseVO.setStore_name(storeInfoResponseVO.getStore_name());
                    staffDeptRelationResponseVO.setStore_id(storeInfoResponseVO.getStore_id());
                }
            }
        }
        //根据当前仓库id查询分润列表
        List<DeptShareListResponseVO> deptList = deptService.getDeptShareList(storeInfoResponseVO.getStore_id());
        storeInfoResponseVO.setDeptShareConfigVOList(deptList);
        return new Result(CodeEnum.SUCCESS, storeInfoResponseVO);
    }

    /**
     * @return com.hryj.common.Result
     * @author 代廷波
     * @description: 修改门店状态
     * @param: vo
     * @create 2018/07/06 10:23
     **/
    @Transactional
    public Result updateStoreStatus(StoreUpdateStatusRequestVO vo) {

        StaffAdminLoginVO loginVO = LoginCache.getStaffAdminLoginVO(vo.getLogin_token());
        if (null == loginVO) {
            return new Result(CodeEnum.FAIL_TOKEN_INVALID);
        }
        Long operator_id = loginVO.getStaff_id();
        log.info("修改门店状态：operator_id={},updateStaff======{}", operator_id, JSON.toJSONString(vo));

        DeptGroup deptGroup = new DeptGroup();
        deptGroup.setId(vo.getStore_id());
        deptGroup.setDept_status(vo.getStore_status());
        deptGroup.setOperator_id(operator_id);//操作人
        deptGroup.setUpdate_time(new Date());
        deptGroupMapper.updateById(deptGroup);
        if (vo.getStore_status() == 0) {//停用门店删除 token
            staffDeptRelationService.updateClearStaffLoginToken(vo.getStore_id());
        }

        return new Result(CodeEnum.SUCCESS);
    }


    public Result<PageResponseVO<StoreListResponseVO>> getStoreList(StoreListParamRequestVO vo) {

        PageResponseVO<StoreListResponseVO> pageResponseVO = new PageResponseVO<>();
        Page page = new Page(vo.getPage_num(), vo.getPage_size());

        List<StoreListResponseVO> list = storeInfoMapper.getStoreList(vo, page);
        pageResponseVO.setTotal_count(page.getTotal());
        pageResponseVO.setTotal_page(page.getPages());
        pageResponseVO.setRecords(list);

        return new Result(CodeEnum.SUCCESS, pageResponseVO);

    }

}
