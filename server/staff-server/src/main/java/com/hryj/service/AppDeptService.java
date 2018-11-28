package com.hryj.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.cache.CodeCache;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.common.SysCodeEnmu;
import com.hryj.entity.bo.staff.dept.DeptGroup;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.staff.dept.request.DeptGroupBasicInfoRequestVO;
import com.hryj.entity.vo.staff.dept.request.DeptIdRequestVO;
import com.hryj.entity.vo.staff.dept.request.DeptIdsRequestVO;
import com.hryj.entity.vo.staff.dept.response.DeptDetAndDistributionAreaResponseVO;
import com.hryj.entity.vo.staff.dept.response.DeptGroupResponseVO;
import com.hryj.entity.vo.staff.dept.response.DeptIdByStoreOrWarehouseResponseVO;
import com.hryj.mapper.DeptGroupMapper;
import com.hryj.mapper.StoreInfoMapper;
import com.hryj.mapper.WarehouseInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 代廷波
 * @className: AppDeptService
 * @description: app端部门服务
 * @create 2018/6/27 0027-17:09
 **/
@Slf4j
@Service
public class AppDeptService extends ServiceImpl<DeptGroupMapper, DeptGroup> {

    @Autowired
    private DeptGroupMapper deptGroupMapper;

    @Autowired
    private StoreInfoMapper storeInfoMapper;

    @Autowired
    private WarehouseInfoMapper warehouseInfoMapper;

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.dept.response.DeptDetAndDistributionAreaResponseVO>
     * @author 代廷波
     * @description: 根据组织id获取组织的基本信息与配送范围VO
     * @param: vo
     * @create 2018/07/09 19:17
     **/
    public Result<DeptDetAndDistributionAreaResponseVO> getAppDeptDetAndDistributionArea(DeptIdRequestVO vo) {
        DeptGroup deptGroup = baseMapper.selectById(vo.getDept_id());
        DeptDetAndDistributionAreaResponseVO deptDet = null;
        //部门类型:01-门店,02-仓库,03-普通部门
        if (deptGroup.getDept_type().equals("01")) {//门店
            deptDet = deptGroupMapper.getDeptGroupStoreArea(vo);
        } else if (deptGroup.getDept_type().equals("02")) {//仓库
            deptDet = deptGroupMapper.getDeptGroupWHArea(vo);
        }
        return new Result(CodeEnum.SUCCESS, deptDet);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO                               <                               com.hryj.entity.vo.staff.dept.response.DeptGroupResponseVO>>
     * @author 代廷波
     * @description: 根据部门id集合查询详情
     * @param: basicInfoRequestVO
     * @create 2018/07/10 21:06
     **/
    public Result<ListResponseVO<DeptGroupResponseVO>> getAppDeptGroupBasicInfo(DeptGroupBasicInfoRequestVO basicInfoRequestVO) {
        List<DeptGroupResponseVO> list = deptGroupMapper.getAppDeptGroupBasicInfo(basicInfoRequestVO);
        return new Result(CodeEnum.SUCCESS, new ListResponseVO(list));
    }

    /**
     * @author 代廷波
     * @description: app 据组织id获取门店或者仓库查询详情
     * @param: vo
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.dept.response.DeptIdByStoreOrWarehouseResponseVO>
     * @create 2018/07/12 21:56
     **/
    public Result<DeptIdByStoreOrWarehouseResponseVO> getAppDeptIdByStoreOrWarehouseDet(DeptIdRequestVO vo) {
        String msg=CodeCache.getValueByKey("StoreDeliveryMsg","S01");
        DeptIdByStoreOrWarehouseResponseVO storeOrWarehouseVO = null;
        DeptGroup deptGroup = baseMapper.selectById(vo.getDept_id());

        if(null == deptGroup){
           return new Result(CodeEnum.FAIL_PARAMCHECK,"组织没有找到");
        }
        if(SysCodeEnmu.DEPTETYPE_01.getCodeValue().equals(deptGroup.getDept_type())){//门店
            storeOrWarehouseVO = storeInfoMapper.getAppStoreIdByDet(vo);
        }
        if(SysCodeEnmu.DEPTETYPE_02.getCodeValue().equals(deptGroup.getDept_type())){//仓库
            storeOrWarehouseVO = warehouseInfoMapper.getAppWarehouseIdByDet(vo);
        }
        if(null !=storeOrWarehouseVO){
            storeOrWarehouseVO.setDelivery_info(msg);
        }
        storeOrWarehouseVO.setDept_path(deptGroup.getDept_path());
        return  new Result(CodeEnum.SUCCESS,storeOrWarehouseVO);
    }

    public Result<ListResponseVO<DeptIdByStoreOrWarehouseResponseVO>> getAppDeptIdsByStoreOrWarehouseDet(DeptIdsRequestVO vo) {
        List<DeptIdByStoreOrWarehouseResponseVO> list = baseMapper.getAppDeptIdsByStoreOrWarehouseDet(vo);
        return new Result(CodeEnum.SUCCESS, new ListResponseVO(list));
    }
}
