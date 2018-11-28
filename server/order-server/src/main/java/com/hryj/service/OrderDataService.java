package com.hryj.service;

import cn.hutool.core.util.StrUtil;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.staff.dept.DeptGroup;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.order.UserTradeVO;
import com.hryj.entity.vo.profit.request.DataQueryRequestVO;
import com.hryj.feign.StaffFeignClient;
import com.hryj.mapper.OrderInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author 李道云
 * @className: OrderDataService
 * @description: 订单数据service
 * @create 2018/7/17 11:59
 **/
@Service
public class OrderDataService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private StaffFeignClient staffFeignClient;

    /**
     * @author 李道云
     * @methodName: statisTradeUserNum
     * @methodDesc: 统计交易用户数量
     * @description:
     * @param: [party_id, dept_path, start_date, end_date]
     * @return com.hryj.common.Result
     * @create 2018-08-06 19:46
     **/
    public Result statisTradeUserNum(Long party_id,String dept_path,String start_date,String end_date){
        if(StrUtil.isEmpty(start_date) || StrUtil.isEmpty(end_date)){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"查询日期不能为空");
        }
        if(party_id==null && StrUtil.isEmpty(dept_path)){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"门店仓库id和部门id不能都为空");
        }
        Map map = orderInfoMapper.statisTradeUserNum(party_id,dept_path, start_date, end_date);
        Integer trade_user_num = Integer.parseInt(map.get("trade_user_num").toString());
        return new Result(CodeEnum.SUCCESS,trade_user_num);
    }


    /**
     * @author 李道云
     * @methodName: findTradeUserList
     * @methodDesc: 查询交易用户列表
     * @description:
     * @param: [dataQueryRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.order.UserTradeVO>>
     * @create 2018-07-17 12:00
     **/
    public Result<ListResponseVO<UserTradeVO>> findTradeUserList(DataQueryRequestVO dataQueryRequestVO){
        String start_date = dataQueryRequestVO.getStart_date();
        String end_date = dataQueryRequestVO.getEnd_date();
        Long store_id = dataQueryRequestVO.getStore_id();
        Long dept_id = dataQueryRequestVO.getDept_id();
        Long wh_id = dataQueryRequestVO.getWh_id();
        if(StrUtil.isEmpty(start_date) || StrUtil.isEmpty(end_date)){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"查询日期不能为空");
        }
        if(store_id==null && dept_id==null && wh_id ==null){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"门店id、部门id、仓库id不能都为空");
        }
        List<UserTradeVO> userTradeVOList = null;
        //查询门店的交易用户数据
        if(store_id !=null){
            userTradeVOList = orderInfoMapper.findTradeUserList(store_id,null, start_date, end_date);
        }
        //查询部门下所有门店的交易用户数据
        if(dept_id !=null){
            Result<DeptGroup> result = staffFeignClient.getDeptGroupByDeptId(dept_id);
            DeptGroup deptGroup = result.getData();
            userTradeVOList = orderInfoMapper.findTradeUserList(null,deptGroup.getDept_path(), start_date, end_date);
        }
        if(wh_id !=null){
            userTradeVOList = orderInfoMapper.findTradeUserList(wh_id,null, start_date, end_date);
        }
        return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>(userTradeVOList));
    }

    /**
     * @author 李道云
     * @methodName: findNewTradeUserList
     * @methodDesc: 查询新增交易用户列表
     * @description:
     * @param: [dataQueryRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.order.UserTradeVO>>
     * @create 2018-07-17 12:00
     **/
    public Result<ListResponseVO<UserTradeVO>> findNewTradeUserList(DataQueryRequestVO dataQueryRequestVO){
        String start_date = dataQueryRequestVO.getStart_date();
        String end_date = dataQueryRequestVO.getEnd_date();
        Long store_id = dataQueryRequestVO.getStore_id();
        Long dept_id = dataQueryRequestVO.getDept_id();
        Long wh_id = dataQueryRequestVO.getWh_id();
        if(StrUtil.isEmpty(start_date) || StrUtil.isEmpty(end_date)){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"查询日期不能为空");
        }
        if(store_id==null && dept_id==null && wh_id ==null){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"门店id、部门id、仓库id不能都为空");
        }
        List<UserTradeVO> userTradeVOList = null;
        //查询门店的新增交易用户数据
        if(store_id !=null){
            userTradeVOList = orderInfoMapper.findNewTradeUserList(store_id,null, start_date, end_date);
        }
        //查询部门下所有门店的新增交易用户数据
        if(dept_id !=null){
            Result<DeptGroup> result = staffFeignClient.getDeptGroupByDeptId(dept_id);
            DeptGroup deptGroup = result.getData();
            userTradeVOList = orderInfoMapper.findNewTradeUserList(null,deptGroup.getDept_path(), start_date, end_date);
        }
        if(wh_id !=null){
            userTradeVOList = orderInfoMapper.findNewTradeUserList(wh_id,null, start_date, end_date);
        }
        return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>(userTradeVOList));
    }
}
