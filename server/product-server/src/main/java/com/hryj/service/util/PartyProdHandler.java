package com.hryj.service.util;

import com.hryj.entity.vo.product.response.app.AppProdListItemResponseVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 王光银
 * @className: PartyProdHandler
 * @description:
 * @create 2018/7/13 0013 11:20
 **/
public class PartyProdHandler {

    public Long party_id;
    public String party_type;
    public BigDecimal distance_to_user;
    public List<AppProdListItemResponseVO> activity_prod_list;
    public List<AppProdListItemResponseVO> normal_prod_list;

    @Override
    public int hashCode() {
        return party_id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof  PartyProdHandler) {
            PartyProdHandler item = (PartyProdHandler) obj;
            return this.party_id.equals(item.party_id);
        }
        return false;
    }
}
