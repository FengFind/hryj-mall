package com.hryj.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.cache.LoginCache;
import com.hryj.entity.bo.staff.user.StaffLoginToken;
import com.hryj.mapper.StaffLoginTokenMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 李道云
 * @className: StaffLoginTokenService
 * @description: 员工登录token
 * @create 2018/7/20 21:44
 **/
@Service
@Slf4j
public class StaffLoginTokenService extends ServiceImpl<StaffLoginTokenMapper,StaffLoginToken> {

    /**
     * 获取员工登录token
     * @param staff_id
     * @return
     */
    private StaffLoginToken findStaffLoginToken(Long staff_id){
        EntityWrapper<StaffLoginToken> wrapper = new EntityWrapper<>();
        wrapper.eq("staff_id",staff_id);
        return super.selectOne(wrapper);
    }

    /**
     * 更新员工app登录token
     * @param staff_id
     * @param app_login_token
     * @param device_id
     */
    public void updateStaffAppLoginToken(Long staff_id,String app_login_token,String device_id){
        StaffLoginToken staffLoginToken = findStaffLoginToken(staff_id);
        if(staffLoginToken ==null){
            staffLoginToken = new StaffLoginToken();
        }else{
            String old_login_token = staffLoginToken.getApp_login_token();
            if(StrUtil.isNotEmpty(old_login_token)){
                LoginCache.clearStaffAppLoginVO(old_login_token);
            }
        }
        staffLoginToken.setStaff_id(staff_id);
        staffLoginToken.setApp_login_token(app_login_token);
        staffLoginToken.setApp_login_time(DateUtil.date());
        staffLoginToken.setDevice_id(device_id);
        super.insertOrUpdate(staffLoginToken);
    }

    /**
     * 更新员工后台登录token
     * @param staff_id
     * @param admin_login_token
     */
    public void updateStaffAdminLoginToken(Long staff_id,String admin_login_token){
        StaffLoginToken staffLoginToken = findStaffLoginToken(staff_id);
        if(staffLoginToken ==null){
            staffLoginToken = new StaffLoginToken();
        }else{
            String old_login_token = staffLoginToken.getAdmin_login_token();
            if(StrUtil.isNotEmpty(old_login_token)){
                LoginCache.clearStaffAdminLoginVO(old_login_token);
            }
        }
        staffLoginToken.setStaff_id(staff_id);
        staffLoginToken.setAdmin_login_token(admin_login_token);
        staffLoginToken.setAdmin_login_time(DateUtil.date());
        super.insertOrUpdate(staffLoginToken);
    }

    /**
     * @return void
     * @author 代廷波
     * @description: 清空员工 token
     * @param: staff_ids 员工集合
     * @create 2018/07/31 19:31
     **/

    public void updateClearStaffLoginTokenBatch(List<Long> staff_ids) {
        EntityWrapper<StaffLoginToken> shareConfig_ew = new EntityWrapper<StaffLoginToken>();
        shareConfig_ew.in("staff_id", staff_ids);
        List<StaffLoginToken> staffLoginTokenList = super.selectList(shareConfig_ew);
        List<Long> token_ids = new ArrayList<>();
        for (StaffLoginToken loginToken : staffLoginTokenList) {
            if (StrUtil.isNotEmpty(loginToken.getAdmin_login_token())) {
                LoginCache.clearStaffAdminLoginVO(loginToken.getAdmin_login_token());
                log.info("批量清空员工admin_login_token:staff_id={},loginToken={}",loginToken.getStaff_id(),loginToken.getAdmin_login_token());
            }
            if (StrUtil.isNotEmpty(loginToken.getApp_login_token())) {
                LoginCache.clearStaffAppLoginVO(loginToken.getApp_login_token());
                log.info("批量清空员工app_login_token:staff_id={},loginToken={}",loginToken.getStaff_id(),loginToken.getApp_login_token());
            }
            token_ids.add(loginToken.getId());
        }
        if (token_ids.size() > 0) {
            super.deleteBatchIds(token_ids);
            log.info("批量删除员工登录令牌:staff_id={},loginToken={}",token_ids.toString());
        }
    }

}
