package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hryj.entity.bo.promotion.ActivityAuditRecord;
import com.hryj.entity.vo.promotion.activity.request.SearchPromotionActivityRequestVO;
import com.hryj.entity.vo.promotion.activity.response.PromotionActivityAuditRecordResponseVO;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 汪豪
 * @className: ActivityAuditMapper
 * @description: 活动审核相关Mapper
 * @create 2018/7/4 0004 20:31
 **/
@Component
public interface ActivityAuditMapper extends BaseMapper<ActivityAuditRecord> {
    /**
     * @author 汪豪
     * @methodName: searchActivityAuditRecordList
     * @methodDesc: 分页查询促销活动的所有审核记录
     * @description:
     * @param: [searchPromotionActivityRequestVO,page]
     * @return List<PromotionActivityAuditRecordResponseVO>
     * @create 2018-07-06 9:20
     **/
    List<PromotionActivityAuditRecordResponseVO> searchActivityAuditRecordList(SearchPromotionActivityRequestVO searchPromotionActivityRequestVO, Page page);

}
