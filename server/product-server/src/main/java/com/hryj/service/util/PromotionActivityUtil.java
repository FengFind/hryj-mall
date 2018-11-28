package com.hryj.service.util;

import com.hryj.cache.CodeCache;
import com.hryj.utils.UtilValidate;

import java.util.Map;

/**
 * @author 王光银
 * @className: PromotionActivityUtil
 * @description:
 * @create 2018/7/11 0011 20:36
 **/
public class PromotionActivityUtil {

    public static final String ACTIVITY_TYPE_BK = "01";
    public static final String ACTIVITY_TYPE_TG = "02";

    public static final String PROMOTION_ACTIVITY_TYPE_TEMPLETE_STRING_KEY = "ActivityIntroduceTemplete";

    public static String expandActivityTempleteString(String activity_type, Map<String, Object> context) {
        if (UtilValidate.isEmpty(activity_type)) {
            return "parameter:activity_type is null";
        }
        String templete_string = CodeCache.getValueByKey(PROMOTION_ACTIVITY_TYPE_TEMPLETE_STRING_KEY, "S" + activity_type);
        if (UtilValidate.isEmpty(templete_string)) {
            return "activity_type templete config is null";
        }
        return StringTempleteUtil.expandString(templete_string, context);
    }

    public static class BK {
        public static final String PARAMETER_NAME_DISCOUNT = "discount_num";
    }
}
