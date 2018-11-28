package com.hryj.service.util;

import com.hryj.cache.LoginCache;
import com.hryj.entity.vo.staff.user.StaffAdminLoginVO;
import com.hryj.utils.UtilValidate;

/**
 * @author 王光银
 * @className: CommonUtil
 * @description:
 * @create 2018/9/12 0012 17:38
 **/
public class CommonUtil {

    public static StaffAdminLoginVO getStaffAdminFromCache(String token) {
        if (UtilValidate.isEmpty(token)) {
            return null;
        }
        return LoginCache.getStaffAdminLoginVO(token);
    }

    public static Long getStaffIdFromCache(String token) {
        StaffAdminLoginVO loginVO = getStaffAdminFromCache(token);
        return loginVO == null ? null : loginVO.getStaff_id();
    }
}
