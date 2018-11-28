package com.hryj.service.util;

import cn.hutool.core.util.ReUtil;
import com.hryj.utils.UtilValidate;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author 王光银
 * @className: StringTempleteUtil
 * @description:
 * @create 2018/7/30 0030 11:03
 **/
public class StringTempleteUtil {

    private static final String pattern_model = "[\\$\\{\\w+\\}]+";
    private static final Pattern pattern = Pattern.compile(pattern_model);

    public static String expandString(String templete, Map<String, Object> context) {
        if (UtilValidate.isEmpty(templete)) {
            return null;
        }
        if (UtilValidate.isEmpty(context)) {
            return templete;
        }
        List<String> list = ReUtil.findAll(pattern, templete, 0);
        if (UtilValidate.isNotEmpty(list)) {
            for (String s : list) {
                String value_key = s.substring(s.indexOf("{") + 1, s.lastIndexOf("}"));
                if (UtilValidate.isEmpty(value_key)) {
                    templete = templete.replace(s, "${}");
                    continue;
                }
                if (context.containsKey(value_key)) {
                    templete = templete.replace(s, context.get(value_key) == null ? "${null}" : context.get(value_key).toString());
                    continue;
                }
                templete = templete.replace(s, "${null}");
            }
        }
        return templete;
    }

}
