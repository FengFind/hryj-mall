package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.order.ReturnProduct;
import com.hryj.entity.vo.order.response.AdminOrderReturnResponseVO;
import org.springframework.stereotype.Component;

/**
 * @author 叶方宇
 * @className: ReturnProductMapper
 * @description: 退货单产品信息
 * @create 2018/7/4 0004 17:56
 **/
@Component
public interface ReturnProductMapper extends BaseMapper<ReturnProduct> {
    AdminOrderReturnResponseVO getAdminOorderReturnResponseVO(Long order_id);
}
