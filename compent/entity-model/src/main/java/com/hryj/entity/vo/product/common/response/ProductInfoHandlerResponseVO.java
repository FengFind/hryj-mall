package com.hryj.entity.vo.product.common.response;

import com.hryj.entity.vo.promotion.activity.request.PartyProductActivityRequestVO;
import com.hryj.utils.UtilMisc;
import com.hryj.utils.UtilValidate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 王光银
 * @className: ProductInfoHandlerResponseVO
 * @description:
 * @create 2018/8/17 0017 10:39
 **/
@ApiModel(value = "商品简单信息集合响应包装VO", description = "商品简单信息集合响应包装VO")
@Data
public class ProductInfoHandlerResponseVO {

    @ApiModelProperty(value = "商品信息集合")
    private List<ProductSimpleInfoItem> prod_list;

    public Map<Long, List<ProductSimpleInfoItem>> groupByPartyId() {
        if (UtilValidate.isEmpty(prod_list)) {
            return null;
        }
        Map<Long, List<ProductSimpleInfoItem>> group_by_party_id_map = new HashMap<>(prod_list.size());
        for (ProductSimpleInfoItem item : prod_list) {
            if (item.getParty_id() == null) {
                item.setParty_id(-1L);
            }
            if (group_by_party_id_map.containsKey(item.getParty_id())) {
                group_by_party_id_map.get(item.getParty_id()).add(item);
            } else {
                group_by_party_id_map.put(item.getParty_id(), UtilMisc.toList(item));
            }
        }
        return group_by_party_id_map;
    }

    public ProductSimpleInfoItem getOneItem(Long party_id, Long product_id, Long activity_id) {
        if (party_id == null || party_id <= 0L) {
            throw new NullPointerException("party_id 不能为空且必须大于0");
        }
        if (product_id == null || product_id <= 0L) {
            throw new NullPointerException("product_id 不能为空且必须大于0");
        }
        if (UtilValidate.isEmpty(this.prod_list)) {
            return null;
        }
        for (ProductSimpleInfoItem item : prod_list) {
            if (party_id.equals(item.getParty_id()) && product_id.equals(item.getProduct_id())) {
                if (activity_id != null && activity_id > 0L) {
                    if (activity_id.equals(item.getActivity_id())) {
                        return item;
                    }
                } else {
                    return item;
                }
            }
        }
        return null;
    }

    public ProductSimpleInfoItem getOneItem(PartyProductActivityRequestVO requestVO) {
        return getOneItem(requestVO.getParty_id(), requestVO.getProduct_id(), requestVO.getActivity_id());
    }
}
