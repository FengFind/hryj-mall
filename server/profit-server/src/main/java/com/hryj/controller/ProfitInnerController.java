package com.hryj.controller;

import com.hryj.common.Result;
import com.hryj.service.ProfitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 李道云
 * @className: ProfitInnerController
 * @description: 分润模块内部接口
 * @create 2018/8/29 10:00
 **/
@Slf4j
@RestController
@RequestMapping("/profitInner")
public class ProfitInnerController {

    @Autowired
    private ProfitService profitService;

    /**
     * @author 李道云
     * @methodName: updateReferralStatisData
     * @methodDesc: 更新推荐用户的统计数据
     * @description:
     * @param: [statis_date, staff_id, store_id]
     * @return com.hryj.common.Result
     * @create 2018-08-29 10:03
     **/
    @PostMapping("/updateReferralStatisData")
    public Result updateReferralStatisData(@RequestParam(name = "statis_date") String statis_date,
                                           @RequestParam(name = "staff_id") Long staff_id,
                                           @RequestParam(name = "store_id") Long store_id){
        return profitService.updateReferralStatisData(statis_date,staff_id,store_id);
    }
}
