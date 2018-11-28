package com.hryj.service.util;

import com.hryj.constant.CommonConstantPool;
import com.hryj.entity.vo.product.common.PageProductTypePermissionRequestVO;

import java.util.List;
import java.util.Map;

/**
 * @author 王光银
 * @className: ProductTypeConditionUtil
 * @description:
 * @create 2018/9/18 0018 15:58
 **/
public class ProductTypeConditionUtil {

    public static void setProductTypeCondition(PageProductTypePermissionRequestVO requestVO, Map<String, Object> params_map, List<String> permission_list) {
        if (CommonConstantPool.STR_ALL.equals(requestVO.getProduct_type_id())) {
            if (permission_list.size() == 1) {
                params_map.put("product_type_id", permission_list.get(0));
            } else {
                params_map.put("product_type_id_list", permission_list);
            }
        } else {
            params_map.put("product_type_id", requestVO.getProduct_type_id());
        }
    }
}
