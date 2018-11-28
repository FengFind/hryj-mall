package com.hryj.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.hryj.cache.LoginCache;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.staff.dept.DeptGroup;
import com.hryj.entity.vo.profit.request.DataQueryRequestVO;
import com.hryj.entity.vo.profit.response.DataQueryResponseVO;
import com.hryj.feign.OrderFeignClient;
import com.hryj.feign.StaffFeignClient;
import com.hryj.mapper.DataQueryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 李道云
 * @className: DataQueryService
 * @description: 数据查询service
 * @create 2018/7/7 14:05
 **/
@Slf4j
@Service
public class DataQueryService {

    @Autowired
    private DataQueryMapper dataQueryMapper;

    @Autowired
    private StaffFeignClient staffFeignClient;

    @Autowired
    private OrderFeignClient orderFeignClient;

    /**
     * @author 李道云
     * @methodName: findPersonalOrTeamData
     * @methodDesc: 查询个人或团队数据
     * @description:
     * @param: [dataQueryRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.profit.response.DataQueryResponseVO>
     * @create 2018-07-07 16:00
     **/
    public Result<DataQueryResponseVO> findPersonalOrTeamData(DataQueryRequestVO dataQueryRequestVO){
        log.info("查询个人或团队数据:dataQueryRequestVO===" + JSON.toJSONString(dataQueryRequestVO));
        String start_date = dataQueryRequestVO.getStart_date();
        String end_date = dataQueryRequestVO.getEnd_date();
        Long staff_id = dataQueryRequestVO.getStaff_id();
        Long store_id = dataQueryRequestVO.getStore_id();
        Long dept_id = dataQueryRequestVO.getDept_id();
        Long wh_id = dataQueryRequestVO.getWh_id();
        if(StrUtil.isEmpty(start_date) || StrUtil.isEmpty(end_date)){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"查询日期不能为空");
        }
        DataQueryResponseVO dataQueryResponseVO = null;
        //都为空的时候查询当前登录员工的数据
        if(staff_id ==null && store_id==null && dept_id==null && wh_id ==null){
            String login_token = dataQueryRequestVO.getLogin_token();
            staff_id = LoginCache.getStaffAppLoginVO(login_token).getStaff_id();
        }
        //查询单个员工的数据
        if(staff_id !=null){
            dataQueryResponseVO = dataQueryMapper.findPersonalData(staff_id, start_date, end_date);
        }
        //查询门店的数据
        if(store_id !=null){
            dataQueryResponseVO = dataQueryMapper.findStoreTeamData(store_id, start_date, end_date);
            if(dataQueryResponseVO !=null){
                Result result = orderFeignClient.statisTradeUserNum(store_id,null, start_date, end_date);
                if (null != result && result.isSuccess()){
                    dataQueryResponseVO.setTrade_user_num((Integer)result.getData());
                }
            }
        }
        //查询部门下的所有数据
        if(dept_id !=null){
            Result<DeptGroup> result = staffFeignClient.getDeptGroupByDeptId(dept_id);
            if (null != result && result.isSuccess()){
                DeptGroup deptGroup = result.getData();
                if(null != deptGroup){
                    dataQueryResponseVO = dataQueryMapper.findDeptTeamData(deptGroup.getDept_path(), start_date, end_date);
                    if(dataQueryResponseVO !=null ){
                        Result result2 = orderFeignClient.statisTradeUserNum(null, deptGroup.getDept_path(), start_date, end_date);
                        if (null != result2 && result2.isSuccess()){
                            dataQueryResponseVO.setTrade_user_num((Integer)result2.getData());
                        }
                    }
                }
            }
        }
        //查询仓库的数据
        if(wh_id !=null){
            dataQueryResponseVO = dataQueryMapper.findWhTeamData(wh_id,start_date,end_date);
            if(dataQueryResponseVO !=null){
                Result result = orderFeignClient.statisTradeUserNum(wh_id,null, start_date, end_date);
                if (null != result && result.isSuccess()){
                    dataQueryResponseVO.setTrade_user_num((Integer)result.getData());
                }
            }
        }
        if(dataQueryResponseVO !=null){
            log.info("查询个人或团队数据:dataQueryResponseVO===" + JSON.toJSONString(dataQueryResponseVO));
        }else{
            dataQueryResponseVO = new DataQueryResponseVO();
        }
        return new Result<>(CodeEnum.SUCCESS, dataQueryResponseVO);
    }

}
