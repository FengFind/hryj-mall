package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hryj.entity.bo.profit.DeptGrossProfitBalance;
import com.hryj.entity.vo.profit.response.DeptGrossProfitResponseVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 李道云
 * @className: DeptGrossProfitBalanceMapper
 * @description: 部门毛利结算表
 * @create 2018/8/16 11:24
 **/
@Component
public interface DeptGrossProfitBalanceMapper extends BaseMapper<DeptGrossProfitBalance> {

    /**
     * @author 李道云
     * @methodName: searcheDeptGrossProfit
     * @methodDesc: 分页查询部门毛利分润
     * @description:
     * @param: [dept_id, page]
     * @return java.util.List<com.hryj.entity.vo.profit.response.DeptGrossProfitResponseVO>
     * @create 2018-08-16 11:26
     **/
    List<DeptGrossProfitResponseVO> searcheDeptGrossProfit(@Param("dept_id") Long dept_id,Page page);

}
