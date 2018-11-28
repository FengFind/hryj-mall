package com.hryj.controller;

import com.hryj.common.Result;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.promotion.activity.request.SearchPromotionActivityRequestVO;
import com.hryj.entity.vo.promotion.activity.request.SubmitActivityRequestVO;
import com.hryj.entity.vo.promotion.activity.response.PromotionActivityAuditRecordResponseVO;
import com.hryj.exception.BizException;
import com.hryj.service.ActivityAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 王光银
 * @className: ActivityAuditController
 * @description: 活动审核
 * @create 2018/6/28 0028 16:39
 **/
@RestController
@RequestMapping("/activityAuditMgr")
public class ActivityAuditController {

    @Autowired
    private ActivityAuditService activityAuditService;

    /**
     * @author 王光银
     * @methodName: searchPromotionActivityAuditRecord
     * @methodDesc: 查询促销活动的所有审核记录
     * @description:
     * @param: [searchPromotionActivityRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.promotion.activity.response.PromotionActivityAuditRecordResponseVO>>
     * @create 2018-06-30 9:23
     **/
    @PostMapping("/searchActivityAuditRecord")
    public Result<PageResponseVO<PromotionActivityAuditRecordResponseVO>> searchPromotionActivityAuditRecord(@RequestBody SearchPromotionActivityRequestVO searchPromotionActivityRequestVO) {
        return activityAuditService.searchPromotionActivityAuditRecord(searchPromotionActivityRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: submitActivityHandleResult
     * @methodDesc: 提交促销活动审核处理结果
     * @description: handle_result 处理结果:1-通过,0-不通过
     * @param: [submitActivityRequestVo]
     * @return com.hryj.common.Result
     * @create 2018-06-28 17:03
     **/
    @PostMapping("/submitActivityHandleResult")
    public Result submitActivityHandleResult(@RequestBody SubmitActivityRequestVO submitActivityRequestVo) throws BizException {
        return activityAuditService.submitActivityHandleResult(submitActivityRequestVo);
    }
}
