package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.promotion.ActivityProductItem;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * @author 汪豪
 * @className: ActivityProductItemMapper
 * @description: 活动对应商品mapper
 * @create 2018/7/7 0007 15:29
 **/
@Component
public interface ActivityProductItemMapper extends BaseMapper<ActivityProductItem> {

    Long getSortNumMaxByActivityId(Long activity_id);

    ActivityProductItem getActivityProductItemBySortNumAndActivityId(@Param("activity_id")Long activity_id,@Param("sort_num")Long sort_num);
}
