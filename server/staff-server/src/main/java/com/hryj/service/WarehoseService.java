package com.hryj.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.IdWorker;
import com.hryj.cache.CodeCache;
import com.hryj.cache.LoginCache;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.common.SysCodeEnmu;
import com.hryj.constant.StaffConstants;
import com.hryj.entity.bo.staff.dept.DeptGroup;
import com.hryj.entity.bo.staff.dept.DeptShareConfig;
import com.hryj.entity.bo.staff.user.StaffDeptRelation;
import com.hryj.entity.bo.staff.warehouse.WarehouseInfo;
import com.hryj.entity.bo.staff.warehouse.WhDistributionArea;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.staff.user.StaffAdminLoginVO;
import com.hryj.entity.vo.staff.warehouse.request.*;
import com.hryj.entity.vo.staff.warehouse.response.WarehouseCityAreaResponseVO;
import com.hryj.entity.vo.staff.warehouse.response.WarehouseInfoResponseVO;
import com.hryj.entity.vo.staff.warehouse.response.WarehouseListResponseVO;
import com.hryj.entity.vo.staff.warehouse.response.WarehouseStaffResponseVO;
import com.hryj.feign.UserFeignClient;
import com.hryj.mapper.DeptGroupMapper;
import com.hryj.mapper.StoreInfoMapper;
import com.hryj.mapper.WarehouseInfoMapper;
import com.hryj.mapper.WhDistributionAreaMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author 代廷波
 * @className: WarehoseService
 * @description:
 * @create 2018/6/27 0027-19:31
 **/
@Slf4j
@Service
public class WarehoseService {

    @Autowired
    private WarehouseInfoMapper warehouseInfoMapper;

    @Autowired
    private DeptShareConfigService deptShareConfigService;

    @Autowired
    private DeptCostService deppCostService;

    @Autowired
    private DeptService deptService;

    @Autowired
    private DeptGroupMapper deptGroupMapper;

    @Autowired
    private WhDistributionAreaMapper whDistributionAreaMapper;

    @Autowired
    private WhDistributionAreaService whDistributionAreaService;

    @Autowired
    private StaffDeptRelationService staffDeptRelationService;
    @Autowired
    private StaffDeptChangeRecordService staffDeptChangeRecordService;
    @Autowired
    private StaffLoginTokenService staffLoginTokenService;

    @Autowired
    private StoreInfoMapper storeInfoMapper;

    @Autowired
    private UserFeignClient userFeignClient;

    /**
     * @return com.hryj.common.Result
     * @author 代廷波
     * @description: 保存仓库
     * @param: vo
     * @create 2018/07/21 20:45
     **/
    @Transactional
    public Result saveWarehose(WarehouseInfoRequestVO vo) {

        Result result = new Result();
        StaffAdminLoginVO loginVO = LoginCache.getStaffAdminLoginVO(vo.getLogin_token());
        if (null == loginVO) {
            return new Result(CodeEnum.FAIL_TOKEN_INVALID);
        }
        Long operator_id = loginVO.getStaff_id();
        log.info("保存仓库：operator_id={},saveWarehose======{}", operator_id, JSON.toJSONString(vo));

        //share_key分润比例是否正确,number_regex_key数字是否有效 占时不设
       /* String share = judgeWarehoseShareConfig(vo.getWarehouseConfigReqestVOList());
        if(StrUtil.isNotEmpty(share)){
            return new Result(CodeEnum.FAIL_PARAMCHECK,share);
        }*/
        //验证---仓库名称
        if (deptService.validatoDeptName(vo.getWarehouse_name(), null)) {

            return new Result(CodeEnum.FAIL_PARAMCHECK, "仓库名称重复");
        }
        //验证---基本信息
        String msg = validatorWarehouse(vo);
        if (StrUtil.isNotEmpty(msg)) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, msg);
        }

        vo.setProvince_name(getCityName(vo.getProvince_code()));//所在省
        vo.setCity_name(getCityName(vo.getCity_code()));//所在市
        vo.setArea_name(getCityName(vo.getArea_code()));//所在区

        //1.创建组织
        Long warehouse_id = saveDeptGroup(operator_id, vo);
        //2.创建仓库
        saveWarehouseInfo(warehouse_id, operator_id, vo);
        //3.配送区域
        if (CollectionUtil.isNotEmpty(vo.getWhDistributionAreaVOList())) {
            savaWhDistributionArea(warehouse_id, operator_id, vo.getWhDistributionAreaVOList());
        }
        //4.员工
        saveWarehouseStaff(warehouse_id, operator_id, vo.getWarehouseStaffVOList());
        //5.分润
        //saveWarehouseConfig(warehouse_id, operator_id, vo.getWarehouseConfigReqestVOList());


        return result;
    }


    /**
     * @return boolean
     * @author 代廷波
     * @description: 验证分润
     * @param: list
     * @create 2018/07/06 16:46
     **/
    public String judgeWarehoseShareConfig(List<WarehouseConfigReqestVO> list) {
        BigDecimal share_conut = new BigDecimal("0");
        String msg = null;
        Boolean bl = false;
        for (WarehouseConfigReqestVO config : list) {
            //验证两位有数字最大 99.99 最小0.00
            if (null == config.getStaff_id() || config.getStaff_id().equals(0)) {//判断员工
                msg = "分润员工id为空";
                break;
            }
            if (null == config.getDept_id() || config.getDept_id().equals(0)) {//判断部门id
                msg = "分润部门id为空";
                break;
            }
            if (null == config.getShare_ratio()) {//判断比例
                msg = "分润比例设置为空";
                break;
            } else {
                bl = ReUtil.isMatch(StaffConstants.NUMBER_REGEX_TWO_DECIMAL, config.getShare_ratio().toString());
                if (!bl) {
                    msg = "分润比例数字设置错误";
                    break;
                }
            }

            share_conut = share_conut.add(config.getShare_ratio());
            //验证两位有数字最大 99.99 最小0.00
            bl = ReUtil.isMatch(StaffConstants.NUMBER_REGEX_TWO_DECIMAL, config.getShare_ratio().toString());
            if (!bl) {
                bl = true;
                break;
            }
        }

        //大于时返回1,  等于时返回0, 小于时返回 -1
        int i = share_conut.compareTo(StaffConstants.DEFAULT_MAX_SHARE_RATIO);
        if (i != -1) {
            msg = "分润比例不能大于100%";
        }
        return msg;
    }

    public String getCityName(String code) {
        String srt = CodeCache.getNameByValue("CityArea", code);
        return srt;
    }

    private String validatorWarehouse(WarehouseInfoRequestVO vo) {

        /*if (StrUtil.isEmpty(vo.getWarehouse_name())) {
            return "仓库名为空";
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
/*

        if (StrUtil.isEmpty(vo.getArea_name())) {
            return "所在区为空";
        }

        if (StrUtil.isEmpty(vo.getStreet_code())) {
            return "所在街道代码";
        }

        if (StrUtil.isEmpty(vo.getStreet_name())) {
            return "所在街道为空";
        }
*/

        if (StrUtil.isEmpty(vo.getDetail_address())) {
            return "详细地址为空";
        }

        if (StrUtil.isEmpty(vo.getLocations())) {
            return "仓库坐标为空";
        }

        if (StrUtil.isEmpty(vo.getTelephone())) {
            return "联系电话";
        }
        if (StrUtil.isEmpty(vo.getContact_name())) {
            return "联系人为空";
        }
        /*if (CollectionUtil.isEmpty(vo.getWhDistributionAreaVOList())) {
            return "仓库配送区域为空";
        }*/
        if (CollectionUtil.isNotEmpty(vo.getWarehouseStaffVOList())) {
            DeptGroup dept = null;
            String msg = null;
            int staff_conut = 0;//统计员工的总数
            for (WarehouseStaffRequestVO staff : vo.getWarehouseStaffVOList()) {
                if (null != staff.getWarehouse_id()) {
                    if (staff.getWarehouse_id().longValue() != vo.getWarehouse_id().longValue()) {
                        dept = deptGroupMapper.selectById(staff.getWarehouse_id());
                        if (null == dept) {
                            msg = "没有找到转移仓库";
                            break;
                        }
                        if (dept.getDept_status().equals(0)) {//部门状态:1-正常,0-关闭
                            msg = "转移到的仓库以关闭";
                            break;
                        }
                        if (!dept.getDept_type().equals(SysCodeEnmu.DEPTETYPE_02.getCodeValue())) {
                            msg = "仓库员工只能转移到仓库";
                            break;
                        }

                    } else {
                        ++staff_conut; //修改
                    }
                } else {
                    ++staff_conut;//新增
                }
            }
            if (null != msg) {
                return msg;
            }
            if (staff_conut == 0) {
                return "不能将仓库员工移为空";
            }
        } else {

            return "仓库员工为空";
        }
        if(null != vo.getWarehouse_id()){
            //送区域
            EntityWrapper<WhDistributionArea> ew = new EntityWrapper<WhDistributionArea>();
            ew.where("dept_id={0}", vo.getWarehouse_id());
            List<WhDistributionArea> list = whDistributionAreaService.selectList(ew);
            if (list.size() > 0) {
                Map<Long, Long> map = new HashMap<>();
                for (WhDistributionArea area : list) {
                    map.put(area.getCity_id(), area.getCity_id());
                }
                if (CollectionUtil.isNotEmpty(vo.getWhDistributionAreaVOList())) {
                    for (WhDistributionAreaRequestVO areaRequestVO : vo.getWhDistributionAreaVOList()) {
                        if (map.containsKey(areaRequestVO.getCity_id())) {
                            map.remove(areaRequestVO.getCity_id());
                        }
                    }
                }

                if (map.size() > 0) {
                    List<Long> city_ids = new ArrayList<>();
                    for (Map.Entry<Long, Long> entry : map.entrySet()) {
                        city_ids.add(entry.getKey());
                    }
                    if (city_ids.size() > 0) {
                       int total = storeInfoMapper.whDistributionAreamatchingStore(city_ids);
                        if (total > 0) {
                            return "仓库配送区域匹配有门店,不能删除对应区域";
                        }
                    }
                }
            }
        }

        return null;


    }

    /**
     * @return java.lang.Long
     * @author 代廷波
     * @description: 保存仓库组织
     * @param: operator_id 操作人
     * @param: vo
     * @param: new_type操作类型  新增:new_type 修改:update_type
     * @create 2018/07/04 21:51
     **/
    public Long saveDeptGroup(Long operator_id, WarehouseInfoRequestVO vo) {

        //1.创建组织
        DeptGroup dept = new DeptGroup();
        dept.setDept_name(vo.getWarehouse_name());//部门上级id
        dept.setEnd_flag(1);//仓库设置为未节点店
        dept.setOperator_id(operator_id);//操作人
        dept.setDept_pid(vo.getDept_id());
        dept.setCompany_flag(0);//是否为区域公司:1-是,0-否
        dept.setDept_type("02");//部门类型:01-门店,02-仓库,03-普通部门
        dept.setDept_status(1);//仓库状态:1-正常,0-关闭
        dept.setId(IdWorker.getId());
        deptGroupMapper.insert(dept);


        //2.获取部门上级等级和路径
        DeptGroup dept_parent = deptGroupMapper.selectById(vo.getDept_id());
        //3.设置当前组织的等级和路径
        dept.setDept_level(dept_parent.getDept_level() + 1);
        dept.setDept_path(dept_parent.getDept_path() + "," + dept.getId());
        Integer i = deptGroupMapper.updateById(dept);

        return dept.getId();
    }

    /**
     * @return void
     * @author 代廷波
     * @description: 保存仓库基本信息
     * @param: operator_id 操作人
     * @param: vo
     * @create 2018/07/06 16:53
     **/
    public void saveWarehouseInfo(Long warehouse_id, Long operator_id, WarehouseInfoRequestVO vo) {

        WarehouseInfo bo = new WarehouseInfo();
        bo.setProvince_code(vo.getProvince_code());//所在省代码
        bo.setProvince_name(vo.getProvince_name());//所在省
        bo.setCity_code(vo.getCity_code());//所在市代码
        bo.setCity_name(vo.getCity_name());//所在市
        bo.setArea_code(vo.getArea_code());//所在区代码
        bo.setArea_name(vo.getArea_name());//所在区
        bo.setStreet_code(vo.getStreet_code());//所在街道代码
        bo.setStreet_name(vo.getStreet_name());// 所在街道
        bo.setDetail_address(vo.getDetail_address());//详细地址
        bo.setLocations(vo.getLocations());//仓库坐标,经纬度","分隔
        bo.setTelephone(vo.getTelephone());//联系电话
        bo.setContact_name(vo.getContact_name());//联系人
        bo.setOperator_id(operator_id);
        bo.setId(warehouse_id);

        warehouseInfoMapper.insert(bo);
    }

    /**
     * @return void
     * @author 代廷波
     * @description: 保存仓库配送区域
     * @param: operator_id
     * @param: whDistributionAreaRequestVOList
     * @create 2018/07/06 16:54
     **/
    public void savaWhDistributionArea(Long warehouse_id, Long
            operator_id, List<WhDistributionAreaRequestVO> whDistributionAreaRequestVOList) {

        WhDistributionArea area = null;
        List<WhDistributionArea> list = new ArrayList<>();
        for (WhDistributionAreaRequestVO vo : whDistributionAreaRequestVOList) {
            area = new WhDistributionArea();

            area.setCity_id(vo.getCity_id());//城市id
            area.setCity_name(vo.getCity_name());//城市名称
            area.setDept_id(warehouse_id);//仓库id
            area.setOperator_id(operator_id);//操作人
            list.add(area);

        }
        if (list.size() > 0) {
            whDistributionAreaService.insertBatch(list);
        }
    }

    /**
     * @return void
     * @author 代廷波
     * @description: 保存仓库员工
     * @param: warehouse_id 仓库id
     * @param: operator_id 操作人
     * @param: warehouseStaffRequestList
     * @create 2018/07/06 17:11
     **/
    public void saveWarehouseStaff(Long warehouse_id, Long
            operator_id, List<WarehouseStaffRequestVO> warehouseStaffRequestList) {
        List<StaffDeptRelation> list = new ArrayList<>();
        List<StaffDeptRelation> update_list = new ArrayList<>();
        Map<Long, Long> staff_ids_map = new HashMap<>();
        BigDecimal zoo = new BigDecimal("0");
        StaffDeptRelation staff = null;
        Date date = new Date();

        //判断员工是否以前离过职,离职的员工只需要将状态改回来
        int size = warehouseStaffRequestList.size();
        Long[] staff_ids = new Long[size];
        for (int i = 0; i < size; i++) {
            staff_ids[i] = warehouseStaffRequestList.get(i).getStaff_id();
        }
        EntityWrapper<StaffDeptRelation> staffInsert = new EntityWrapper<StaffDeptRelation>();
        staffInsert.in("staff_id", staff_ids);
        List<StaffDeptRelation> staffOdList = staffDeptRelationService.selectList(staffInsert);
        if (null != staffOdList && staffOdList.size() > 0) {
            for (StaffDeptRelation vo : staffOdList) {
                staff_ids_map.put(vo.getStaff_id(), vo.getId());//员工id,关系主键id
            }
        }

        for (WarehouseStaffRequestVO vo : warehouseStaffRequestList) {
            staff = new StaffDeptRelation();
            staff.setStaff_id(vo.getStaff_id());//员工id
            staff.setDept_id(warehouse_id);//仓库id
            staff.setStaff_status(true);//员工状态:1-正常,0-离职
            staff.setUpdate_time(date);
            if (staff_ids_map.containsKey(vo.getStaff_id())) {//数据库有的
                staff.setId(staff_ids_map.get(vo.getStaff_id()));//设置主键
                update_list.add(staff);
            } else {
                list.add(staff);
            }
        }

        if (list.size() > 0) {
            staffDeptRelationService.insertBatch(list);

        }
        if (update_list.size() > 0) {
            staffDeptRelationService.updateBatchById(update_list);

        }
    }

    /**
     * @return void
     * @author 代廷波
     * @description: 保存仓库分润
     * @param: warehouse_id 仓库id
     * @param: operator_id 操作人
     * @param: warehouseConfigReqestVOList
     * @create 2018/07/06 17:19
     **/
    public void saveWarehouseConfig(Long warehouse_id, Long
            operator_id, List<WarehouseConfigReqestVO> warehouseConfigReqestVOList) {
        List<DeptShareConfig> list = new ArrayList<>();
        DeptShareConfig warehouseConfig = null;
        for (WarehouseConfigReqestVO vo : warehouseConfigReqestVOList) {
            warehouseConfig = new DeptShareConfig();

            warehouseConfig.setShare_ratio(vo.getShare_ratio());//分润比例
            warehouseConfig.setStaff_id(vo.getStaff_id());//分润的员工id
            warehouseConfig.setDept_id(vo.getDept_id());//部门id
            warehouseConfig.setStore_id(warehouse_id);//门店id
            warehouseConfig.setOperator_id(operator_id);//操作人id

            list.add(warehouseConfig);
        }
        if (list.size() > 0) {
            deptShareConfigService.insertBatch(list);
        }
    }

    /**
     * @return com.hryj.common.Result
     * @author 代廷波
     * @description: 修改仓库
     * @param: vo
     * @create 2018/07/21 20:46
     **/
    @Transactional
    public Result updateWarehose(WarehouseInfoRequestVO vo) {

        StaffAdminLoginVO loginVO = LoginCache.getStaffAdminLoginVO(vo.getLogin_token());
        if (null == loginVO) {
            return new Result(CodeEnum.FAIL_TOKEN_INVALID);
        }
        Long operator_id = loginVO.getStaff_id();
        log.info("修改仓库：operator_id={},updateWarehose======{}", operator_id, JSON.toJSONString(vo));
        //分润---验证
        /*String share = judgeWarehoseShareConfig(vo.getWarehouseConfigReqestVOList());
        if(StrUtil.isNotEmpty(share)){
            return new Result(CodeEnum.FAIL_PARAMCHECK,share);
        }*/
        if (null == vo.getWarehouse_id()) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "仓库名id为空");
        }
        if (deptService.validatoDeptName(vo.getWarehouse_name(), vo.getWarehouse_id())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "仓库名称重复");
        }

        //验证---基本信息
        String msg = validatorWarehouse(vo);
        if (StrUtil.isNotEmpty(msg)) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, msg);
        }
        String province_name=getCityName(vo.getProvince_code());
        vo.setProvince_name(province_name);//所在省

        String city_name=getCityName(vo.getCity_code());
        vo.setCity_name(city_name);//所在市

        String area_name=getCityName(vo.getArea_code());
        vo.setArea_name(area_name);//所在区

        //1.组织
        updateDeptGroup(operator_id, vo);
        //2.仓库
        updateWarehouseInfo(operator_id, vo);
        //3.配送区域
        updateWhDistributionArea(vo.getWarehouse_id(), operator_id, vo.getWhDistributionAreaVOList());
        //4.员工
        updageWarehouseStaff(vo.getWarehouse_id(), operator_id, vo.getWarehouseStaffVOList());
        //5.分润
        //updateWarehouseConfig(vo.getWarehouse_id(), operator_id, vo.getWarehouseConfigReqestVOList());

        //7、 更新用户常用门店或仓库的基本信息
        ThreadUtil.excAsync(() -> {
            userFeignClient.updateUserOftenPartyInfo(vo.getWarehouse_id(),vo.getWarehouse_name(),province_name+city_name+area_name+vo.getDetail_address());
        },false);
        return new Result();
    }

    /**
     * @return java.lang.Long
     * @author 代廷波
     * @description: 保存修改仓库组织
     * @param: operator_id 操作人
     * @param: vo
     * @param: new_type操作类型  新增:new_type 修改:update_type
     * @create 2018/07/04 21:51
     **/
    public void updateDeptGroup(Long operator_id, WarehouseInfoRequestVO vo) {

        //1.创建组织
        DeptGroup dept = new DeptGroup();
        dept.setId(vo.getWarehouse_id());//部门上级id
        dept.setDept_name(vo.getWarehouse_name());//部门名称
        dept.setEnd_flag(1);//仓库设置为未节点店
        dept.setOperator_id(operator_id);//操作人
        dept.setDept_pid(vo.getDept_id());
        dept.setCompany_flag(0);//是否为区域公司:1-是,0-否
        dept.setDept_type("02");//部门类型:01-门店,02-仓库,03-普通部门


        //2.获取部门上级等级和路径
        DeptGroup dept_parent = deptGroupMapper.selectById(vo.getDept_id());
        //3.设置当前组织的等级和路径
        dept.setDept_level(dept_parent.getDept_level() + 1);
        dept.setDept_path(dept_parent.getDept_path() + "," + dept.getId());

        Integer i = deptGroupMapper.updateById(dept);

    }

    /**
     * @return void
     * @author 代廷波
     * @description: 保存仓库基本信息
     * @param: operator_id 操作人
     * @param: vo
     * @create 2018/07/06 16:53
     **/
    public void updateWarehouseInfo(Long operator_id, WarehouseInfoRequestVO vo) {
        WarehouseInfo bo = new WarehouseInfo();
        bo.setProvince_code(vo.getProvince_code());//所在省代码
        bo.setProvince_name(vo.getProvince_name());//所在省
        bo.setCity_code(vo.getCity_code());//所在市代码
        bo.setCity_name(vo.getCity_name());//所在市
        bo.setArea_code(vo.getArea_code());//所在区代码
        bo.setArea_name(vo.getArea_name());//所在区
        bo.setStreet_code(vo.getStreet_code());//所在街道代码
        bo.setStreet_name(vo.getStreet_name());// 所在街道
        bo.setDetail_address(vo.getDetail_address());//详细地址
        bo.setLocations(vo.getLocations());//仓库坐标,经纬度","分隔
        bo.setTelephone(vo.getTelephone());//联系电话
        bo.setContact_name(vo.getContact_name());//联系人
        bo.setOperator_id(operator_id);
        bo.setId(vo.getWarehouse_id());
        bo.setUpdate_time(new Date());
        warehouseInfoMapper.updateById(bo);
    }

    /**
     * @return void
     * @author 代廷波
     * @description: 保存修改仓库配送区域
     * @param: operator_id
     * @param: whDistributionAreaRequestVOList
     * @create 2018/07/06 16:54
     **/
    public void updateWhDistributionArea(Long warehouse_id, Long
            operator_id, List<WhDistributionAreaRequestVO> whDistributionAreaRequestVOList) {

        WhDistributionArea area = null;
        List<WhDistributionArea> list = new ArrayList<>();

        //修改,先删除部门配送区域
        EntityWrapper<WhDistributionArea> ew = new EntityWrapper<WhDistributionArea>();
        ew.where("dept_id={0}", warehouse_id);
        whDistributionAreaService.delete(ew);

        if (CollectionUtil.isNotEmpty(whDistributionAreaRequestVOList)) {
            for (WhDistributionAreaRequestVO vo : whDistributionAreaRequestVOList) {
                area = new WhDistributionArea();
                area.setDept_id(warehouse_id);//仓库id
                area.setOperator_id(operator_id);//操作人
                area.setCity_id(vo.getCity_id());//城市id
                area.setCity_name(vo.getCity_name());//城市名称
                list.add(area);
            }
            if (list.size() > 0) {
                whDistributionAreaService.insertBatch(list);
            }
        }

    }

    /**
     * @return void
     * @author 代廷波
     * @description: 保存修改仓库员工
     * @param: warehouse_id 仓库id
     * @param: operator_id 操作人
     * @param: warehouseStaffRequestList
     * @create 2018/07/06 17:11
     **/
    public void updageWarehouseStaff(Long warehouse_id, Long
            operator_id, List<WarehouseStaffRequestVO> warehouseStaffRequestList) {

        List<StaffDeptRelation> insert_list = new ArrayList<>();//新增员工
        List<StaffDeptRelation> update_list = new ArrayList<>();//修改员工
        List<StaffDeptRelation> move_list = new ArrayList<>();//转移员工
        List<Long> toke_stff_ids = new ArrayList<>();//转移和删除员工
        StaffDeptRelation warehouse_staff = null;//员工与组织对象
        Map<Long, Long> db_staff_list = new HashMap<>();//数据库中的员工列表:map.key=员工id,map.value=员工组织关系表主建id
        List<Long> new_stff_ids = new ArrayList<>();//前端传过来的新增员工id
        Map<Long, Long> update_staff_status_ids = new HashMap<>();//虽要将离职的员工变成在职的员工
        Date date = new Date();
        EntityWrapper<StaffDeptRelation> warehousewrapper = null;

        //如果是修改,先查找当前门店的员工,将当前仓库的员工 id 放入map中
        warehousewrapper = new EntityWrapper<StaffDeptRelation>();
        warehousewrapper.where("dept_id={0}", warehouse_id).and("staff_status={0}", 1);
        List<StaffDeptRelation> staffList = staffDeptRelationService.selectList(warehousewrapper);//当前仓库数据库中员工列表
        if (staffList.size() > 0) {
            for (StaffDeptRelation db : staffList) {
                db_staff_list.put(db.getStaff_id(), db.getId());//员工id
            }
        }
        //前端传过来的新增员工id
        for (WarehouseStaffRequestVO storeList : warehouseStaffRequestList) {
            if (!db_staff_list.containsKey(storeList.getStaff_id())) {
                new_stff_ids.add(storeList.getStaff_id());
            }
        }

        if (new_stff_ids.size() > 0) {
            //需要将离职的员工变成在职的员工
            warehousewrapper = new EntityWrapper<StaffDeptRelation>();
            warehousewrapper.in("staff_id", new_stff_ids);
            List<StaffDeptRelation> staffOdList = staffDeptRelationService.selectList(warehousewrapper);
            if (null != staffOdList && staffOdList.size() > 0) {
                for (StaffDeptRelation vo : staffOdList) {
                    update_staff_status_ids.put(vo.getStaff_id(), vo.getId());//员工id,关系主键id
                }
            }
        }
        for (WarehouseStaffRequestVO vo : warehouseStaffRequestList) {
            warehouse_staff = new StaffDeptRelation();
            warehouse_staff.setStaff_id(vo.getStaff_id());//员工id
            warehouse_staff.setOperator_id(operator_id);//操作人id
            warehouse_staff.setStaff_status(true);//员工状态:1-正常,0-离职
            warehouse_staff.setUpdate_time(date);//修改时间
            warehouse_staff.setDept_id(warehouse_id);//仓库id

            //仓库有员工:判断员工是新增员工还是修改员工:map有表示为修改,没有为新增
            if (db_staff_list.size() > 0) {
                if (db_staff_list.containsKey(vo.getStaff_id())) {//当前部门员工
                    warehouse_staff.setId(db_staff_list.get(vo.getStaff_id()));//设置主键
                    db_staff_list.remove(vo.getStaff_id());//删除 map中的员工id key
                    //转移员工 员工组织关系表有而当前部门表没有
                    if (null != vo.getWarehouse_id() && (warehouse_id.longValue() != vo.getWarehouse_id().longValue())) {
                        warehouse_staff.setDept_id(vo.getWarehouse_id());
                        move_list.add(warehouse_staff);
                        toke_stff_ids.add(vo.getStaff_id());
                    } else {
                        update_list.add(warehouse_staff);
                    }
                } else {
                    //判断当前员工是否有部门,可能有新增和转移员工
                    if (update_staff_status_ids.containsKey(vo.getStaff_id())) {
                        warehouse_staff.setId(update_staff_status_ids.get(vo.getStaff_id()));//设置主键
                        update_list.add(warehouse_staff);
                    } else {
                        insert_list.add(warehouse_staff);
                    }
                }
                //仓库没有员工全部是新增
            } else {
                if (update_staff_status_ids.containsKey(vo.getStaff_id())) {//数据库有的
                    warehouse_staff.setId(update_staff_status_ids.get(vo.getStaff_id()));//设置主键
                    update_list.add(warehouse_staff);
                } else {
                    insert_list.add(warehouse_staff);
                }
            }
        }
        //map剩下的员工表示是要删除的仓库员工
        if (db_staff_list.size() > 0) {
            List<StaffDeptRelation> del_list = new ArrayList<>();
            for (Map.Entry<Long, Long> entry : db_staff_list.entrySet()) {
                warehouse_staff = new StaffDeptRelation();
                warehouse_staff.setStaff_id(entry.getKey());
                warehouse_staff.setId(entry.getValue());
                warehouse_staff.setStaff_status(false);//员工状态:1-正常,0-离职
                del_list.add(warehouse_staff);
                toke_stff_ids.add(entry.getKey());
            }
            if (del_list.size() > 0) {
                staffDeptRelationService.updateBatchById(del_list);
                //变动类型：01-员工离职，02-转移部门
                //添加离职员工记录
                staffDeptChangeRecordService.saveStaffDeptChangeRecord(del_list, "01", warehouse_id);
                log.info("仓库修改==删除员工:warehouse_id={},operator_id={},del_staff_ids={}", warehouse_id, operator_id, del_list.toString());
            }

        }
        //插入时需要判断员工是否成在,如果成在需要将原来的状态更新为在职
        if (insert_list.size() > 0) {
            staffDeptRelationService.insertBatch(insert_list);
            log.info("仓库修改===添加员工:warehouse_id={},operator_id={},del_staff_ids={}", warehouse_id, operator_id, insert_list.toString());
        }
        if (update_list.size() > 0) {
            staffDeptRelationService.updateBatchById(update_list);
            log.info("仓库修改===修改员工:warehouse_id={},operator_id={},del_staff_ids={}", warehouse_id, operator_id, update_list.toString());
        }
        if (move_list.size() > 0) {
            staffDeptRelationService.updateBatchById(move_list);
            //添加转移员工记录
            staffDeptChangeRecordService.saveStaffDeptChangeRecord(move_list, "02", warehouse_id);
            log.info("仓库修改===转移员工:warehouse_id={},operator_id={},del_staff_ids={}", warehouse_id, operator_id, move_list.toString());
        }
        //删除和转移员工 清除token
        if (toke_stff_ids.size() > 0) {
            staffLoginTokenService.updateClearStaffLoginTokenBatch(toke_stff_ids);
        }
    }

    /**
     * @return void
     * @author 代廷波
     * @description: 保存修改仓库分润
     * @param: warehouse_id 仓库id
     * @param: operator_id 操作人
     * @param: warehouseConfigReqestVOList
     * @create 2018/07/06 17:19
     **/
    public void updateWarehouseConfig(Long warehouse_id, Long
            operator_id, List<WarehouseConfigReqestVO> warehouseConfigReqestVOList) {
        List<DeptShareConfig> list = new ArrayList<>();

        DeptShareConfig warehouseConfig = null;
        //如果修改,先删除部门组织节点分润配置
        EntityWrapper<DeptShareConfig> ew = new EntityWrapper<DeptShareConfig>();
        ew.where("store_id={0}", warehouse_id);
        deptShareConfigService.delete(ew);

        for (WarehouseConfigReqestVO vo : warehouseConfigReqestVOList) {
            warehouseConfig = new DeptShareConfig();
            warehouseConfig.setDept_id(vo.getDept_id());//部门id
            warehouseConfig.setStore_id(warehouse_id);//门店id
            warehouseConfig.setOperator_id(operator_id);//操作人id
            warehouseConfig.setShare_ratio(vo.getShare_ratio());//分润比例
            warehouseConfig.setStaff_id(vo.getStaff_id());//分润的员工id

            list.add(warehouseConfig);
        }
        if (list.size() > 0) {
            deptShareConfigService.insertBatch(list);
        }
    }

    public Result<WarehouseInfoResponseVO> getWarehoseByIdDet(WarehouseIdRequestVO vo) {
        WarehouseInfoResponseVO warehouseInfoRequestVO = warehouseInfoMapper.getWarehoseByIdDet(vo);

        if (null != warehouseInfoRequestVO) {
            if (CollectionUtil.isNotEmpty(warehouseInfoRequestVO.getWarehouseStaffVOList())) {
                for (WarehouseStaffResponseVO warehouseStaff : warehouseInfoRequestVO.getWarehouseStaffVOList()) {
                    warehouseStaff.setWarehouse_id(warehouseInfoRequestVO.getWarehouse_id());
                    warehouseStaff.setWarehouse_name(warehouseInfoRequestVO.getWarehouse_name());
                }
            }
        }

        //根据当前仓库id查询分润列表
        // List<DeptShareListResponseVO> deptList = deptService.getDeptShareList(warehouseInfoRequestVO.getWarehouse_id());
        // warehouseInfoRequestVO.setDeptShareListResPonseVOList(deptList);

        return new Result(CodeEnum.SUCCESS, warehouseInfoRequestVO);
    }

    /**
     * @return com.hryj.common.Result
     * @author 代廷波
     * @description: 修改仓库状态
     * @param: vo
     * @create 2018/07/21 20:48
     **/
    @Transactional
    public Result updateWarehoseStatus(WarehouseUpdateStatusRequestVO vo) {

        StaffAdminLoginVO loginVO = LoginCache.getStaffAdminLoginVO(vo.getLogin_token());
        if (null == loginVO) {
            return new Result(CodeEnum.FAIL_TOKEN_INVALID);
        }
        Long operator_id = loginVO.getStaff_id();
        log.info("修改仓库状态：operator_id={},updateWarehoseStatus======{}", operator_id, JSON.toJSONString(vo));

        if(null == vo.getWarehouse_id()){
            return new Result(CodeEnum.FAIL_PARAMCHECK, "仓库id为空");
        }
        //停用核验数据
        if (vo.getWarehouse_status() == 0) {
            int total = storeInfoMapper.stopWhDistributionAreaStore(vo.getWarehouse_id());
            if (total > 0) {
                return new  Result(CodeEnum.FAIL_PARAMCHECK, "仓库配送区域匹配有门店,不能停用");
            }
        }
        DeptGroup deptGroup = new DeptGroup();
        deptGroup.setId(vo.getWarehouse_id());
        deptGroup.setOperator_id(operator_id);
        deptGroup.setDept_status(vo.getWarehouse_status());
        deptGroupMapper.updateById(deptGroup);
        if (vo.getWarehouse_status() == 0) {//停用门店删除 token
            staffDeptRelationService.updateClearStaffLoginToken(vo.getWarehouse_id());
        }

        return new Result();
    }

    public Result<PageResponseVO<WarehouseListResponseVO>> getWarehoseList(WarehuoseParamRequestVO vo) {
        PageResponseVO<WarehouseListResponseVO> pageResponseVO = new PageResponseVO<>();

        Page page = new Page(vo.getPage_num(), vo.getPage_size());

        List<WarehouseListResponseVO> list = warehouseInfoMapper.getWarehoseList(vo, page);
        pageResponseVO.setTotal_count(page.getTotal());
        pageResponseVO.setTotal_page(page.getPages());
        pageResponseVO.setRecords(list);

        return new Result(CodeEnum.SUCCESS, pageResponseVO);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO                                                                                                                               <                                                                                                                               com.hryj.entity.vo.staff.warehouse.response.WarehouseCityAreaResponseVO>>
     * @author 代廷波
     * @description: 根据省ia获取仓库的配送区域
     * @param: vo
     * @create 2018/07/17 11:52
     **/
    public Result<ListResponseVO<WarehouseCityAreaResponseVO>> getWarehoseCityAreaList(WareHouseCityIdsRequestVO vo) {
        List<Long> ids = null;
        if (null != vo.getIds() && vo.getIds().length > 0) {

        } else {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "没有省id");
        }

        List<WarehouseCityAreaResponseVO> list = warehouseInfoMapper.getWarehoseCityAreaList(vo);
        return new Result(CodeEnum.SUCCESS, new ListResponseVO<WarehouseCityAreaResponseVO>(list));

    }
}
