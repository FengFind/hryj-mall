package com.hryj.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
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
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.staff.dept.request.*;
import com.hryj.entity.vo.staff.dept.response.*;
import com.hryj.entity.vo.staff.user.StaffAdminLoginVO;
import com.hryj.exception.GlobalException;
import com.hryj.mapper.DeptGroupMapper;
import com.hryj.mapper.DeptShareConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author 代廷波
 * @className: DeptService
 * @description: 部门服务
 * @create 2018/6/27 0027-17:09
 **/
@Slf4j
@Service
public class DeptService extends ServiceImpl<DeptGroupMapper, DeptGroup> {

    @Resource
    private DeptGroupMapper deptGroupMapper;

    @Resource
    private StaffDeptRelationService staffDeptRelationService;

    @Resource
    private DeptShareConfigMapper deptShareConfigMapper;

    @Resource
    private DeptShareConfigService deptShareConfigService;

    @Resource
    private StaffDeptChangeRecordService staffDeptChangeRecordService;
    /**
     * @author 代廷波
     * @description: 数据去重
     * @param: list
     * @return java.util.List<com.hryj.entity.vo.staff.dept.response.DeptGroupTreeResponseVO>
     * @create 2018/09/29 15:21
     **/
    public static  List<DeptGroupTreeResponseVO> removeDuplicate( List<DeptGroupTreeResponseVO> list) {
        HashSet h = new HashSet(list);
        list.clear();
        list.addAll(h);
        return list;
    }
    /**
     * 获取部门树
     *
     * @param deptTreeRequestVO
     * @return
     * @throws GlobalException
     */

    public Result<ListResponseVO<DeptGroupTreeResponseVO>> getDeptTree(DeptTreeRequestVO deptTreeRequestVO) throws GlobalException {
        log.info("获取部门树：deptTreeRequestVO=" + JSON.toJSONString(deptTreeRequestVO));
        List<DeptGroupTreeResponseVO> db_list = baseMapper.getDeptTree(deptTreeRequestVO);
        if (null != db_list && db_list.size()>0 && StrUtil.isNotEmpty(deptTreeRequestVO.getDept_name())){
            Set<Long> ids = new HashSet<>();
            String []dept_ids=null;
            for (DeptGroupTreeResponseVO treeResponseVO : db_list) {
                dept_ids =  treeResponseVO.getDept_path().split(",");
                for (int i = 0; i < dept_ids.length-1; i++) {
                    ids.add(Long.parseLong(dept_ids[i]));
                }

            }
            if (ids.size() > 0){
                List<DeptGroupTreeResponseVO> pid_lis = baseMapper.getDeptIdsList(ids);
                db_list.addAll(pid_lis);
                db_list = removeDuplicate(db_list);
            }


        }
        //设置分润节点
        //只有未节节点的父节点才可以设置分润
        Set<Long> ids = new HashSet<>();
        for (DeptGroupTreeResponseVO vo : db_list) {
            if (vo.getEnd_flag() == 1 && vo.getDept_type().equals(SysCodeEnmu.DEPTETYPE_01.getCodeValue())) {//01
                ids.add(vo.getDept_pid());
            }
        }
        for (Long id : ids) {
            for (DeptGroupTreeResponseVO vo : db_list) {
                if (id.equals(vo.getKey())) {
                    vo.setDept_share(1);
                }
            }
        }
        // 最后的结果
        List<DeptGroupTreeResponseVO> package_List = new ArrayList<DeptGroupTreeResponseVO>();
        // 先找到所有的一级对象
        for (int i = 0; i < db_list.size(); i++) {
            // parentId=0
            if (db_list.get(i).getDept_pid() == 0) {
                package_List.add(db_list.get(i));
            }
        }
        // 为一级对象设置子对象，getChild是递归调用的
        for (DeptGroupTreeResponseVO obj : package_List) {
            obj.setChildren(getChild(obj.getKey(), db_list));
        }
        //搜索数据封装
        if (package_List.size() == 0) {
            Map<Long, Long> map = new HashMap<>();
            List<Long> pid_ids = new ArrayList<>();//所有父点节
            List<Long> tree_ids = new ArrayList<>();//所有节点
            for (DeptGroupTreeResponseVO tree : db_list) {
                if (!map.containsKey(tree.getDept_pid())) {
                    map.put(tree.getDept_pid(), tree.getKey());
                    pid_ids.add(tree.getDept_pid());
                }
                tree_ids.add(tree.getKey());
            }
            //算出所有父节点差集 = 所有父点节-所有节点
            pid_ids.removeAll(tree_ids);
            //封装所有父节点
            List<DeptGroupTreeResponseVO> pid_list = new ArrayList<>();
            for (Long pid_id : pid_ids) {
                for (DeptGroupTreeResponseVO t : db_list) {
                    if (pid_id.longValue() == t.getDept_pid().longValue()) {
                        pid_list.add(t);
                    }
                }
            }
            //设置子节点
            for (DeptGroupTreeResponseVO t : pid_list) {
                t.setChildren(setTreeMtd(t.getKey(), pid_list));
            }
            log.info("获取部门树：pid_list=" + JSON.toJSONString(pid_list));
            return new Result(CodeEnum.SUCCESS, new ListResponseVO(pid_list));
        }
        log.info("获取部门树：package_List=" + JSON.toJSONString(package_List));
        return new Result(CodeEnum.SUCCESS, new ListResponseVO(package_List));
    }

    /**
     * @return java.util.List<com.hryj.entity.vo.staff.dept.response.DeptGroupTreeResponseVO>
     * @author 代廷波
     * @description: 递归封装数据
     * @param: pid
     * @param: list
     * @create 2018/07/17 17:04
     **/
    private List<DeptGroupTreeResponseVO> setTreeMtd(Long pid, List<DeptGroupTreeResponseVO> list) {
        List<DeptGroupTreeResponseVO> children = new ArrayList<>();
        //根据pid装备子对象
        for (DeptGroupTreeResponseVO tree : list) {
            if (pid.equals(tree.getDept_pid())) {
                children.add(tree);
            }
        }
        //递归设置子对象
        for (DeptGroupTreeResponseVO child : children) {
            for (DeptGroupTreeResponseVO tree : list) {
                if (child.getKey().equals(tree.getDept_pid())) {
                    child.setChildren(setTreeMtd(child.getKey(), list));
                }
            }
        }
        // 递归退出条件*/
        if (children.size() == 0) {
            return null;
        }
        return children;
    }

    /**
     * @return java.util.List<com.hryj.entity.vo.staff.dept.response.DeptGroupTreeResponseVO>
     * @author 代廷波
     * @description: 递归查找子对象
     * @param: id 当前id
     * @param: db_list 要查找的列表
     * @create 2018/06/27 21:17
     **/
    private List<DeptGroupTreeResponseVO> getChild(Long id, List<DeptGroupTreeResponseVO> db_list) throws GlobalException {
        // 子对象
        List<DeptGroupTreeResponseVO> childList = new ArrayList<>();
        for (DeptGroupTreeResponseVO menu : db_list) {
            // 遍历所有节点，将父对象id与传过来的id比较
            if (menu.getDept_pid() != 0) {
                if (menu.getDept_pid().equals(id)) {
                    childList.add(menu);
                }
            }
        }
        // 把子对象的子对象再循环一遍
        for (DeptGroupTreeResponseVO menu : childList) {//是否末级节点:1-是,0-否
            if (menu.getEnd_flag() == 0) {
                // 递归
                menu.setChildren(getChild(menu.getKey(), db_list));
            }
        }
        // 递归退出条件*/
        if (childList.size() == 0) {
            return null;
        }
        return childList;
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.dept.response.DeptGroupTreeResponseVO>
     * @author 代廷波
     * @description: 根据当前根节点的dept_pid查询所有父节点
     * @param: dept_pid 当前根节点的dept_pid
     * @create 2018/06/29 11:48
     **/
    public List<DeptParentListResponseVO> getDeptParentList(DeptIdRequestVO vo) {
        DeptGroup deptGroup = baseMapper.selectById(vo.getDept_id());
        String[] ids = deptGroup.getDept_path().split(",");
        List<DeptParentListResponseVO> db_list = baseMapper.getDeptParentList(ids);
        log.info("根据当前根节点的dept_pid查询所有父节点：db_list=" + JSON.toJSONString(db_list));
        return db_list;
    }

    /**
     * @return java.util.List<com.hryj.entity.vo.staff.dept.response.DeptShareListResPonseVO>
     * @author 代廷波
     * @description: 根据门店或者仓库id查询分润列表
     * @param: dept_id 门店或者仓库的id
     * @create 2018/07/18 20:57
     **/
    public List<DeptShareListResponseVO> getDeptShareList(Long dept_id) {
        List<DeptShareListResponseVO> db_list = deptShareConfigMapper.getDeptShareList(dept_id);
        log.info("根据门店或者仓库id查询分润列表：db_list=" + JSON.toJSONString(db_list));
        return db_list;
    }

    /**
     * @return boolean true:可以创建 false,不可以创建
     * @author 代廷波
     * @description: 查询名称是否成在
     * @param: dept_name 部门名称是否成在
     * @create 2018/07/13 14:19
     **/
    public boolean validatoDeptName(String dept_name, Long dept_id) {
        EntityWrapper<DeptGroup> wrapper = new EntityWrapper<DeptGroup>();
        wrapper.eq("dept_name", dept_name);
        DeptGroup deptGroup = super.selectOne(wrapper);
        if (deptGroup != null && !deptGroup.getId().equals(dept_id)) {
            return true;
        }
        return false;

    }

    /**
     * 保存部门
     *
     * @param deptRequestVO
     * @return
     * @throws GlobalException
     */
    @Transactional
    public Result saveDept(DeptRequestVO deptRequestVO) throws GlobalException {
        log.info("保存部门：deptRequestVO=" + JSON.toJSONString(deptRequestVO));
        StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(deptRequestVO.getLogin_token());
        if (null == staffAdminLoginVO) {
            return new Result(CodeEnum.FAIL_TOKEN_INVALID);
        }
        Long operator_id = staffAdminLoginVO.getStaff_id();
        //参数校验
        String validator = validatorDept(deptRequestVO);
        if (StrUtil.isNotEmpty(validator)) {
            return new Result(CodeEnum.FAIL_BUSINESS, validator);
        }
        if (validatoDeptName(deptRequestVO.getDept_name(), null)) {
            return new Result(CodeEnum.FAIL_BUSINESS, "部门名称重复");
        }
        //判断父节点是否有门店或者仓库
       /* if (null != deptRequestVO.getDept_pid()){
            EntityWrapper<DeptGroup> wrapper = new EntityWrapper<DeptGroup>();
            wrapper.where(" dept_pid={0}", deptRequestVO.getDept_pid())
                    .in("dept_type", new String[]{ SysCodeEnmu.DEPTETYPE_01.getCodeValue(), SysCodeEnmu.DEPTETYPE_02.getCodeValue()});
            Integer count = baseMapper.selectCount(wrapper);
            if(count != 0){
                return new Result(CodeEnum.FAIL_BUSINESS, "组织下面有仓库或者门店");
            }
        }*/

        DeptGroup deptGroup = new DeptGroup();
        deptGroup.setDept_name(deptRequestVO.getDept_name());
        deptGroup.setCompany_flag(deptRequestVO.getCompany_flag());
        if (null == deptRequestVO.getDept_pid()) {
            deptGroup.setDept_pid(0L);
        } else {
            deptGroup.setDept_pid(deptRequestVO.getDept_pid());
        }
        deptGroup.setDept_type(CodeCache.getValueByKey("DeptType", "S03"));//普通部门
        deptGroup.setOperator_id(operator_id);//操作人
        deptGroup.setEnd_flag(0);

        Long id=IdWorker.getId();
        //设置部门级别 和 组织路径:","分隔
        //如果有父节点，当前节点路径=父路径+自己的id，部门级别=父等级+1
        if (null != deptRequestVO.getDept_pid()) {
            DeptGroup parentDeptGroup = baseMapper.selectById(deptRequestVO.getDept_pid());
            deptGroup.setDept_path(parentDeptGroup.getDept_path() + "," + id);
            deptGroup.setDept_level(parentDeptGroup.getDept_level() + 1);
            if(!parentDeptGroup.getDept_type().equals(SysCodeEnmu.DELIVERYTYPE_03.getCodeValue())){
                return new Result(CodeEnum.FAIL_BUSINESS, "组织只能挂在组织上");
            }
        } else {//如果没有父节点，当前节点路径=自己的id，部门级别=1
            deptGroup.setDept_level(1);
            deptGroup.setDept_path(id + "");
        }
        deptGroup.setId(id);
        baseMapper.insert(deptGroup);
        //保存员工部门关系
        if (CollectionUtil.isNotEmpty(deptRequestVO.getDeptStaffDetRequestVOList())) {
            staffDeptRelationService.saveDeptStaff(deptRequestVO.getDeptStaffDetRequestVOList(), deptGroup.getId(), operator_id);
        }
        return new Result(CodeEnum.SUCCESS);
    }

    /**
     * @return com.hryj.common.Result
     * @author 代廷波
     * @description: 部门验证
     * @param: deptRequestVO
     * @create 2018/06/26 15:36
     **/
    private String validatorDept(DeptRequestVO deptRequestVO) {
        if (StrUtil.isEmpty(deptRequestVO.getDept_name())) {
            return "部门名为空";
        }
        if (null == deptRequestVO.getCompany_flag()) {
            return "区域公司标识为空";
        }
        if (CollectionUtil.isEmpty(deptRequestVO.getDeptStaffDetRequestVOList())) {
            return "员工列表为空";
        } else {
            Boolean b = true;
            String msg = null;
            for (DeptStaffListRequestVO staff : deptRequestVO.getDeptStaffDetRequestVOList()) {
                if(SysCodeEnmu.SAFFTYPE_01.getCodeValue().equals(staff.getStaff_type())){
                    if (null != staff.getSalary_amt()) {
                        b = ReUtil.isMatch(StaffConstants.NUMBER_REGEX, staff.getSalary_amt().toString());
                        if (!b) {
                            msg = "工资金额数字无效";
                            break;
                        }
                    }
                }

            }
            if (null != msg) {
                return msg;
            }
        }
        return null;
    }

    /**
     * @return com.hryj.common.Result
     * @author 代廷波
     * @description: 修改部门名称
     * @param: vo
     * @create 2018/07/02 10:31
     **/
    @Transactional
    public Result updateDeptName(DeptUpdataNameRequestVO deptUpdataNameRequestVO) throws GlobalException {
        log.info("修改部门名称：deptUpdataNameRequestVO=" + JSON.toJSONString(deptUpdataNameRequestVO));
        StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(deptUpdataNameRequestVO.getLogin_token());
        if (null == staffAdminLoginVO) {
            return new Result(CodeEnum.FAIL_TOKEN_INVALID);
        }
        Long operator_id = staffAdminLoginVO.getStaff_id();
        if (StrUtil.isEmpty(deptUpdataNameRequestVO.getDept_name())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "部门名称为空");
        }
        if (deptUpdataNameRequestVO.getDept_id() == null || "0".equals(deptUpdataNameRequestVO.getDept_id())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "部门id为空");
        }
        if (validatoDeptName(deptUpdataNameRequestVO.getDept_name(), deptUpdataNameRequestVO.getDept_id())) {
            return new Result(CodeEnum.FAIL_BUSINESS, "部门名称重复");
        }
        if (1 == deptUpdataNameRequestVO.getCompany_flag()){
            if(validataUpdateDeptCreateCompany(deptUpdataNameRequestVO.getDept_id())){
                return new Result(CodeEnum.FAIL_BUSINESS, "组织上以有区域公司,不能在创建区域公司");
            }
        }else{
            deptUpdataNameRequestVO.setCompany_flag(0);
        }

        DeptGroup deptGroup = new DeptGroup();
        deptGroup.setId(deptUpdataNameRequestVO.getDept_id());
        deptGroup.setDept_name(deptUpdataNameRequestVO.getDept_name());
        deptGroup.setCompany_flag(deptUpdataNameRequestVO.getCompany_flag());
        deptGroup.setUpdate_time(new Date());
        deptGroup.setOperator_id(operator_id);
        baseMapper.updateById(deptGroup);
        return new Result(CodeEnum.SUCCESS);
    }
    /**
     * @author 代廷波
     * @description: 判断是否可以创建区域公司
     * @param: dept_id
     * @return boolean
     * @create 2018/08/06 17:34
     **/
    public boolean validataUpdateDeptCreateCompany(Long dept_id) {

        //判断父类
        DeptGroup deptGroup = baseMapper.selectById(dept_id);
        String[] ids_str = deptGroup.getDept_path().split(",");
        List<Long> ids = new ArrayList<>();
        for (String s : ids_str) {
            ids.add(Convert.toLong(s));
        }
        List<DeptGroup> pid_list = baseMapper.selectBatchIds(ids);
        for (DeptGroup group : pid_list) {
            if (group.getCompany_flag() == 1 && group.getId().longValue() != dept_id) {
                return true;
            }
        }
        //判断子类
        EntityWrapper<DeptGroup> wrapper = new EntityWrapper<DeptGroup>();
        wrapper.like("dept_path", Convert.toStr(dept_id));
        List<DeptGroup> child_list = baseMapper.selectList(wrapper);

        for (DeptGroup group : child_list) {
            if (group.getCompany_flag() == 1 && group.getId().longValue() != dept_id) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return com.hryj.common.Result
     * @author 代廷波
     * @description: 根据部门id查询员工列表
     * @param: dept_id 部门id
     * @create 2018/06/27 19:24
     **/
    public Result<ListResponseVO<DeptStaffListResponseVO>> getDeptIdByStaffList(DeptIdRequestVO deptIdRequestVO) throws GlobalException {
        log.info("根据部门id查询员工列表：deptIdRequestVO=" + JSON.toJSONString(deptIdRequestVO));
        List<DeptStaffListResponseVO> deptStaffList = baseMapper.getStaffListByDeptId(deptIdRequestVO);
        log.info("根据部门id查询员工列表：deptStaffList=" + JSON.toJSONString(deptStaffList));
        return new Result(CodeEnum.SUCCESS, new ListResponseVO<>(deptStaffList));
    }


    /**
     * @return com.hryj.common.Result
     * @author 代廷波
     * @description: 保存部门员工列表
     * @param: vo
     * @create 2018/06/28 18:54
     **/
    @Transactional
    public Result saveDeptStaff(DeptStaffRequestVO deptStaffRequestVO) throws GlobalException {
        log.info("保存部门员工列表：deptStaffRequestVO=" + JSON.toJSONString(deptStaffRequestVO));
        StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(deptStaffRequestVO.getLogin_token());
        if (null == staffAdminLoginVO) {
            return new Result(CodeEnum.FAIL_TOKEN_INVALID);
        }
        Long operator_id = staffAdminLoginVO.getStaff_id();
        if (CollectionUtil.isEmpty(deptStaffRequestVO.getDeptStaffListRequestVOList())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "部门员工列表为空");
        }
        if (deptStaffRequestVO.getDept_id() == null || "0".equals(deptStaffRequestVO.getDept_id())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "部门id为空");
        }
        boolean b = false;
        DeptGroup dept=null;
        String msg=null;
        int staff_conut = 0;//统计员工的总数
        for (DeptStaffListRequestVO staff : deptStaffRequestVO.getDeptStaffListRequestVOList()) {
            if (null != staff.getSalary_amt()) {
                b = ReUtil.isMatch(StaffConstants.NUMBER_REGEX, staff.getSalary_amt().toString());
                if (!b) {
                    msg="无效的数据字金额";
                    break;
                }
            }
            //判断转移员工
            if(null != staff.getDept_id()){
                if(staff.getDept_id().longValue()!= deptStaffRequestVO.getDept_id().longValue()){
                    dept = deptGroupMapper.selectById(staff.getDept_id());
                    if(null == dept){
                        msg = "没有找到转移的部门";
                        break;
                    }
                    if(!dept.getDept_type().equals(SysCodeEnmu.DEPTETYPE_03.getCodeValue())){
                        msg = "部门员工只能转移到部门";
                        break;
                    }
                } else {
                    ++staff_conut; //修改
                }
            }else {
                ++staff_conut; //新增
            }
        }
        if (null != msg) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, msg);
        }
        if (staff_conut == 0) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "不能将部门员工转移为空");
        }

        return staffDeptRelationService.saveDeptStaff(operator_id,deptStaffRequestVO);

    }


    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.dept.response.DeptStaffShareResPonseVO>
     * @author 代廷波
     * @description: 根据组织或者门店和仓库id查询分润列表
     * @param: vo
     * @create 2018/07/18 19:28
     **/
    public Result<DeptStaffShareResPonseVO> getDeptIdByShareList(DeptShareRequestVO deptShareRequestVO) throws GlobalException {
        log.info("根据组织或者门店和仓库id查询分润列表：deptShareRequestVO=" + JSON.toJSONString(deptShareRequestVO));
        DeptStaffShareResPonseVO deptStaffShareResPonseVO = new DeptStaffShareResPonseVO();
        //查询分润部门员工
        if (deptShareRequestVO.getSearch_type().equals("02")) {
            deptStaffShareResPonseVO = deptShareConfigMapper.getDeptStaffShare(deptShareRequestVO);
        }
        List<DeptStaffListResponseVO> list = baseMapper.getDeptIdByShareList(deptShareRequestVO);
        if(null == deptStaffShareResPonseVO){
            deptStaffShareResPonseVO = new DeptStaffShareResPonseVO();
        }
        deptStaffShareResPonseVO.setDeptStaffShareList(list);
        log.info("根据组织或者门店和仓库id查询分润列表：deptStaffShareResPonseVO=" + JSON.toJSONString(deptStaffShareResPonseVO));
        return new Result(CodeEnum.SUCCESS, deptStaffShareResPonseVO);
    }

    /**
     * @return com.hryj.common.Result
     * @author 代廷波
     * @description: 根据部门id查询向上的部门列表
     * @param: dept_id 部门id
     * @create 2018/06/27 19:24
     **/
    public Result<ListResponseVO<DeptGroupListResponseVO>> getDeptIdByUpDeptList(DeptIdRequestVO deptIdRequestVO) {
        log.info("根据部门id查询向上的部门列表：deptIdRequestVO=" + JSON.toJSONString(deptIdRequestVO));
        DeptGroup deptGroup = deptGroupMapper.selectById(deptIdRequestVO.getDept_id());
        String[] ids = deptGroup.getDept_path().split(",");
        List<DeptGroupListResponseVO> deptGroupList = deptGroupMapper.getDeptIdByUpDeptList(ids);
        log.info("根据部门id查询向上的部门列表：deptIdRequestVO=" + JSON.toJSONString(deptIdRequestVO));
        return new Result(CodeEnum.SUCCESS, new ListResponseVO<>(deptGroupList));
    }

    /**
     * @return int
     * @author 代廷波
     * @description: 根据部门id获取部门等级
     * @param: dept_id
     * @create 2018/07/13 21:01
     **/
    public int getDeptIdByleve(Long dept_id) {
        DeptGroup deptGroup = deptGroupMapper.selectById(dept_id);
        return deptGroup.getDept_level();
    }


    /**
     * @return com.hryj.common.Result
     * @author 代廷波
     * @description: 批量设置部门分润
     * @param: vo
     * @create 2018/07/07 17:49
     **/
    @Transactional
    public Result savaBatchDeptShare(DeptShareBatchReqestVO deptShareBatchReqestVO) {
        log.info("批量设置部门分润：deptShareBatchReqestVO=" + JSON.toJSONString(deptShareBatchReqestVO));
        StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(deptShareBatchReqestVO.getLogin_token());
        if (null == staffAdminLoginVO) {
            return new Result(CodeEnum.FAIL_TOKEN_INVALID);
        }
        Long operator_id = staffAdminLoginVO.getStaff_id();
        if (null == deptShareBatchReqestVO.getDept_id()) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "分润节点部门为空");
        }
        if (CollectionUtil.isEmpty(deptShareBatchReqestVO.getDeptShareBatchDataReqestVOList())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "分润列表为空");
        }
        //计算从当前分润节点 所有父节点长度=等于当前点节点等级-1
        int upDeptGroupSize = getDeptIdByleve(deptShareBatchReqestVO.getDept_id());
        if (deptShareBatchReqestVO.getDeptShareBatchDataReqestVOList().size() != upDeptGroupSize) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "分润列表数据不全");
        }
        DeptShareConfig shareConfig = null;
        List<DeptShareConfig> shareConfigList = null;
        if (deptShareBatchReqestVO.getDeptShareBatchDataReqestVOList().size() > 0) {
            BigDecimal share_conut = new BigDecimal("0");
            shareConfigList = new ArrayList<>();
            /**
             * 0:默认为值,
             * 1:员工id为空
             * 2.分润设置错误
             * 3.节点部门id为空
             * 4.分润比例为无效,最多两位小数
             */
            int err_type = 0;
            Boolean b = true;
            for (DeptShareBatchDataReqestVO config : deptShareBatchReqestVO.getDeptShareBatchDataReqestVOList()) {
                shareConfig = new DeptShareConfig();
                if (null == config.getShare_ratio()) {//判断比例
                    err_type = 2;
                    break;
                } else {
                    //验证分润的数据
                    b = ReUtil.isMatch(StaffConstants.NUMBER_REGEX_TWO_DECIMAL, config.getShare_ratio().toString());
                    if (!b) {
                        err_type = 4;
                        break;
                    }
                }
                if (null == config.getStaff_id()) {//判断员工
                    err_type = 1;
                    break;
                }
                if (null == config.getDept_id()) {//判断部门id
                    err_type = 3;
                    break;
                }
                shareConfig.setStaff_id(config.getStaff_id());//分润员工
                shareConfig.setShare_ratio(config.getShare_ratio());//分润比例
                shareConfig.setDept_id(config.getDept_id());
                shareConfigList.add(shareConfig);
                share_conut = share_conut.add(config.getShare_ratio());
            }
            if (err_type != 0) {
                return new Result(CodeEnum.FAIL_PARAMCHECK, StaffConstants.SHARE_ERROR_MSG.get(err_type));
            }
            //大于时返回1,  等于时返回0, 小于时返回 -1
            int i = share_conut.compareTo(StaffConstants.DEFAULT_MAX_SHARE_RATIO);
            if (i != -1) {
                return new Result(CodeEnum.FAIL_PARAMCHECK, "分润比例不能大于或者等于100");
            }
        } else {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "没有设置列表部门分润");
        }
        //1.根据当前节点id 查询所有未节子节点
        EntityWrapper<DeptGroup> ew = new EntityWrapper<DeptGroup>();
        ew.where("dept_pid={0}", deptShareBatchReqestVO.getDept_id()).and("end_flag={0}", 1)
                .and("dept_type={0}",SysCodeEnmu.DEPTETYPE_01.getCodeValue());//否末级节点:1-是,0-否
        List<DeptGroup> deptGroupList = deptGroupMapper.selectList(ew);
        //2.删除以前的分润
        int length = deptGroupList.size();
        Long[] store_ids = new Long[length];
        for (int i = 0; i < length; i++) {
            store_ids[i] = deptGroupList.get(i).getId();
        }
        EntityWrapper<DeptShareConfig> shareConfig_ew = new EntityWrapper<DeptShareConfig>();
        shareConfig_ew.in("store_id", store_ids);
        deptShareConfigMapper.delete(shareConfig_ew);
        log.info("批量设置部门分润:删除以前分润,operator_id={},门店ids={}", operator_id, store_ids);
        //3.添加新的批量分润
        List<DeptShareConfig> insert_list = new ArrayList<>();
        DeptShareConfig deptShareConfig = null;
        for (int i = 0; i < length; i++) {
            Long store_id = deptGroupList.get(i).getId();
            for (DeptShareConfig config : shareConfigList) {
                deptShareConfig = new DeptShareConfig();
                deptShareConfig.setOperator_id(operator_id);//操作人
                deptShareConfig.setStaff_id(config.getStaff_id());//分润员工
                deptShareConfig.setShare_ratio(config.getShare_ratio());//分润比例
                deptShareConfig.setDept_id(config.getDept_id());
                deptShareConfig.setStore_id(store_id);
                insert_list.add(deptShareConfig);
            }
        }
        deptShareConfigService.insertBatch(insert_list);
        log.info("批量设置部门分润：DeptShareConfig:" + JSON.toJSONString(insert_list));
        return new Result();
    }

    /**
     * @return com.hryj.common.Result
     * @author 代廷波
     * @description: 根据当前组织id, 判断是否可以创建区域公司
     * @param: vo
     * @create 2018/07/09 16:13
     **/
    public Result<DeptCreateCompanyFlag> getDeptCreateCompanyFlag(DeptIdRequestVO deptIdRequestVO) {
        log.info("根据当前组织id, 判断是否可以创建区域公司：deptIdRequestVO=" + JSON.toJSONString(deptIdRequestVO));
        DeptGroup deptGroup = baseMapper.selectById(deptIdRequestVO.getDept_id());
        String[] ids_str = deptGroup.getDept_path().split(",");
        List<Long> ids = new ArrayList<>();
        for (String s : ids_str) {
            ids.add(Convert.toLong(s));
        }
        List<DeptGroup> db_list = baseMapper.selectBatchIds(ids);
        DeptCreateCompanyFlag flag = new DeptCreateCompanyFlag();
        flag.setCompany_flag(1);//是否可以创建区域公司:1-是,0-否
        flag.setDept_name(deptGroup.getDept_name());//返回当前组织名称
        for (DeptGroup group : db_list) {
            if (group.getCompany_flag() == 1) {
                flag.setCompany_flag(0);
            }
        }
        return new Result(CodeEnum.SUCCESS, flag);
    }

    /**
     * @author 代廷波
     * @description: 根据当前登陆人查询所有门店或者仓库列表
     * @param: vo
     * @create 2018/07/14 14:34
     **/
    public Result<ListResponseVO<DeptStoreOrWarehouseResponseVO>> getDeptStoreOrWarehouseList(DeptStoreOrWarehouseRequestVO deptStoreOrWarehouseRequestVO) {
        log.info("根据当前登陆人查询所有门店或者仓库列表：deptStoreOrWarehouseRequestVO=" + JSON.toJSONString(deptStoreOrWarehouseRequestVO));
        StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(deptStoreOrWarehouseRequestVO.getLogin_token());
        if (null == staffAdminLoginVO) {
            return new Result(CodeEnum.FAIL_TOKEN_INVALID);
        }
        Long staff_id = staffAdminLoginVO.getStaff_id();
        EntityWrapper<StaffDeptRelation> wrapper = new EntityWrapper<StaffDeptRelation>();
        wrapper.eq("staff_id", staff_id);
        StaffDeptRelation staffDeptRelation = staffDeptRelationService.selectOne(wrapper);
        Long dept_id = staffDeptRelation.getDept_id();
        deptStoreOrWarehouseRequestVO.setDept_id(dept_id);
        List<DeptStoreOrWarehouseResponseVO> list = deptGroupMapper.getDeptStoreOrWarehouseList(deptStoreOrWarehouseRequestVO);
        log.info("根据当前登陆人查询所有门店或者仓库列表：list=" + JSON.toJSONString(list));
        return new Result(CodeEnum.SUCCESS, new ListResponseVO<>(list));
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO       <       com.hryj.entity.vo.staff.dept.response.DeptGroupTreeResponseVO>>
     * @author 代廷波
     * @description: 根据部门id或者员工或者登录token查询下面所有组织
     * @param: vo
     * @create 2018/07/23 14:50
     **/
    public Result<ListResponseVO<DeptGroupTreeResponseVO>> getDeptIdByDownDeptTree(DeptDownTreeRequestVO vo) {
        if (null != vo.getDept_id()) {
            List<DeptGroupTreeResponseVO> tree_list = setDeptIdByDownDeptTree(vo.getDept_id());
            log.info("根据部门id获取部门树：dept_id={},tree_list={}", vo.getDept_id(), JSON.toJSONString(tree_list));
            return new Result(CodeEnum.SUCCESS, new ListResponseVO(tree_list));

        }
        if (null != vo.getStaff_id()) {
            EntityWrapper<StaffDeptRelation> staffInsert = new EntityWrapper<StaffDeptRelation>();
            staffInsert.eq("staff_id", vo.getStaff_id());
            StaffDeptRelation staffOdList = staffDeptRelationService.selectOne(staffInsert);
            List<DeptGroupTreeResponseVO> tree_list = setDeptIdByDownDeptTree(staffOdList.getDept_id());
            log.info("根据id获取部门树：staffid={},tree_list={}", vo.getStaff_id(), JSON.toJSONString(tree_list));
            return new Result(CodeEnum.SUCCESS, new ListResponseVO(tree_list));
        }

        if (null != vo.getLogin_token()) {
            StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(vo.getLogin_token());
            if (null == staffAdminLoginVO) {
                return new Result(CodeEnum.FAIL_TOKEN_INVALID);
            }
            List<DeptGroupTreeResponseVO> tree_list = setDeptIdByDownDeptTree(staffAdminLoginVO.getDeptGroup().getId());
            log.info("根据login_token获取部门树：tree_list=" + JSON.toJSONString(tree_list));
            return new Result(CodeEnum.SUCCESS, new ListResponseVO(tree_list));
        }
        return new Result(CodeEnum.FAIL_PARAMCHECK,"没有找到你的查询条件");
    }

    public List<DeptGroupTreeResponseVO> setDeptIdByDownDeptTree(Long dept_id) {
        List<DeptGroupTreeResponseVO> db_list = deptGroupMapper.getDeptIdByDownDeptTree(dept_id);
        //封装所有父节点
        List<DeptGroupTreeResponseVO> pid_list = new ArrayList<>();
        for (DeptGroupTreeResponseVO db_vo : db_list) {
            if (dept_id.longValue() == db_vo.getKey().longValue()) {
                pid_list.add(db_vo);
            }
        }
        //设置子节点
        for (DeptGroupTreeResponseVO t : pid_list) {
            t.setChildren(setTreeMtd(t.getKey(), db_list));
        }
        return pid_list;
    }
    /**
     * @author 代廷波
     * @description: 根据当前部门id查询下一级部门
     * @param: vo
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.staff.dept.response.DeptGroupResponseVO>>
     * @create 2018/09/28 10:52
     **/
    public Result<ListResponseVO<DeptGroupPidResponseVO>> getDeptPidList(DeptIdRequestVO vo) {

        List<DeptGroupPidResponseVO> db_list = baseMapper.getDeptPidList(vo);
        return new Result(CodeEnum.SUCCESS, new ListResponseVO(db_list));
    }
}
