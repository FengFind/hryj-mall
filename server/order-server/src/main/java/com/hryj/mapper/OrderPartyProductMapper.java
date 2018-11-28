package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.product.PartyProduct;
import com.hryj.entity.vo.product.common.request.PartyProductInventoryAdjustItem;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * @author 叶方宇
 * @className: PartyProductMapper
 * @description: 门店商品信息操作
 * @create 2018/7/12 0012 21:12
 **/
@Component
public interface OrderPartyProductMapper extends BaseMapper<PartyProduct> {

    void updateProductQuantity(PartyProductInventoryAdjustItem item);

    Integer findProductQuantityById(@Param("party_id") Long party_id, @Param("product_id") Long product_id);
}
