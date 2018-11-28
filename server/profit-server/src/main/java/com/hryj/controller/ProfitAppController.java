package com.hryj.controller;

import com.hryj.common.Result;
import com.hryj.entity.vo.PageRequestVO;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.profit.request.DataQueryRequestVO;
import com.hryj.entity.vo.profit.response.DataQueryResponseVO;
import com.hryj.entity.vo.profit.response.DeptGrossProfitResponseVO;
import com.hryj.entity.vo.profit.response.ProfitDataDetailVO;
import com.hryj.exception.GlobalException;
import com.hryj.service.DataQueryService;
import com.hryj.service.ProfitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 李道云
 * @className: ProfitController
 * @description: 分润模块（门店app端）
 * @create 2018/7/7 13:42
 **/
@Slf4j
@RestController
@RequestMapping("/profitApp")
public class ProfitAppController {

    @Autowired
    private ProfitService profitService;

    @Autowired
    private DataQueryService dataQueryService;

    /**
     * @author 李道云
     * @methodName: findPersonalOrTeamData
     * @methodDesc: 查询个人或团队数据
     * @description:
     * @param: [dataQueryRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.profit.response.DataQueryResponseVO>
     * @create 2018-07-07 13:45
     **/
    @RequestMapping("/findPersonalOrTeamData")
    public Result<DataQueryResponseVO> findPersonalOrTeamData(@RequestBody DataQueryRequestVO dataQueryRequestVO) throws GlobalException {
        return dataQueryService.findPersonalOrTeamData(dataQueryRequestVO);
    }

    /**
     * @author 李道云
     * @methodName: findProfitDataDetail
     * @methodDesc: 查询分润数据明细
     * @description:
     * @param: [requestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.profit.response.ProfitDataDetailVO>
     * @create 2018-07-09 17:13
     **/
    @PostMapping("/findProfitDataDetail")
    public Result<ProfitDataDetailVO> findProfitDataDetail(@RequestBody RequestVO requestVO){
        return profitService.findProfitDataDetail(requestVO);
    }

    /**
     * @author 李道云
     * @methodName: searchProfitDataDetail
     * @methodDesc: 分页查询任润数据明细
     * @description:
     * @param: [pageRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.profit.response.ProfitDataDetailVO>>
     * @create 2018-07-09 17:13
     **/
    @PostMapping("/searchProfitDataDetail")
    public Result<PageResponseVO<ProfitDataDetailVO>> searchProfitDataDetail(@RequestBody PageRequestVO pageRequestVO){
        return profitService.searchProfitDataDetail(pageRequestVO);
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
    @PostMapping("/searcheDeptGrossProfit")
    public Result<PageResponseVO<DeptGrossProfitResponseVO>> searcheDeptGrossProfit(@RequestBody PageRequestVO pageRequestVO){
        return profitService.searcheDeptGrossProfit(pageRequestVO);
    }

}
