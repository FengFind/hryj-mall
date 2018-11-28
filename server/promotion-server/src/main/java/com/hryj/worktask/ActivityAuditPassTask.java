package com.hryj.worktask;

import cn.hutool.core.date.DateUtil;
import com.hryj.cacheutil.PartyActivityMonitorCacheUtil;
import com.hryj.threadpool.ThreadPoolUtil;
import com.hryj.utils.UtilMisc;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

/**
 * @author 王光银
 * @className: ActivityAuditPassTask
 * @description: 活动审核通过后的处理任务
 * @create 2018/8/28 0028 16:59
 **/
@Slf4j
public class ActivityAuditPassTask implements Runnable {

    private List<Long> activity_scope_party_id_list;

    private Date start_date;

    private Date end_date;

    public ActivityAuditPassTask(List<Long> activity_scope_party_id_list, Date start_date, Date end_date) {
        if (UtilValidate.isEmpty(activity_scope_party_id_list)) {
            throw new NullPointerException("activity_scope_party_id_list 参数不能是空值");
        }
        if (start_date == null) {
            throw new NullPointerException("start_date 参数不能是空值");
        }
        if (end_date == null) {
            throw new NullPointerException("end_date 参数不能是空值");
        }
        if (!start_date.before(end_date)) {
            throw new IllegalArgumentException("start_date 必须在 end_date 之前");
        }
        this.activity_scope_party_id_list = activity_scope_party_id_list;
        this.start_date = start_date;
        this.end_date = end_date;
    }

    @Override
    public void run() {
        /**
         * 活动审核通过对缓存的处理分为如下几种情况:
         * 1、活动已经开始
         * 2、活动即将开始
         * 3、活动已结束
         */

        //处理类型：1活动已经开始，2活动即将开始，3活动已结束
        int process_type = -1;
        Date curr = new Date();

        if (curr.after(this.start_date) && curr.before(this.end_date)) {
            process_type = 1;
        } else if (curr.before(this.start_date)) {
            process_type = 2;
        } else {
            process_type = 3;
        }

        curr.setTime(System.currentTimeMillis() - 1000);

        /**
         * 调用缓存工具类，将开始日期与结束日期追加到缓存中
         */
        for (Long party_id : this.activity_scope_party_id_list) {
            PartyActivityMonitorCacheUtil.appendTo(party_id, UtilMisc.toList(this.start_date, this.end_date));

            switch (process_type) {
                case 1:
                    ThreadPoolUtil.submitTask(new RefreshProductSearchResultExpireTimeTask(party_id, DateUtil.formatDateTime(curr)));
                    break;
                case 2:
                    Date first = PartyActivityMonitorCacheUtil.getFirst(party_id);
                    if (first == null) {
                        ThreadPoolUtil.submitTask(new RefreshProductSearchResultExpireTimeTask(party_id, DateUtil.formatDateTime(curr)));
                    } else if (this.start_date.before(first)) {
                        ThreadPoolUtil.submitTask(new RefreshProductSearchResultExpireTimeTask(party_id, DateUtil.formatDateTime(this.start_date)));
                    }
                    break;
                case 3:
                    log.info("审核通过的活动为已结束活动，不做处理...");
                    break;
            }
        }
    }
}
