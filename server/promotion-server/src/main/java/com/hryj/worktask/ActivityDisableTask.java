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
 * @className: ActivityDisableTask
 * @description: 活动停用后的处理任务
 * @create 2018/8/29 0029 9:09
 **/
@Slf4j
public class ActivityDisableTask implements Runnable {

    private List<Long> activity_scope_party_id_list;

    private Date start_date;

    private Date end_date;

    public ActivityDisableTask(List<Long> activity_scope_party_id_list, Date start_date, Date end_date) {
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
         * 停用活动对缓存的处理分为如下几种情况:
         * 1、正在进行中的活动： 立即清除所有的缓存数据
         * 2、还未开始的活动，刷新缓存时效
         * 3、已结束的活动，不做处理
         */

        //处理类型：1表示进行中，2表示未开始，3表示已结束
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

        for (Long party_id : this.activity_scope_party_id_list) {
            PartyActivityMonitorCacheUtil.removeFrom(party_id, UtilMisc.toList(this.start_date, this.end_date));

            switch (process_type) {
                case 1:
                    ThreadPoolUtil.submitTask(new RefreshProductSearchResultExpireTimeTask(party_id, DateUtil.formatDateTime(curr)));
                    break;
                case 2:
                    Date first = PartyActivityMonitorCacheUtil.getFirst(party_id);
                    if (first == null) {
                        ThreadPoolUtil.submitTask(new RefreshProductSearchResultExpireTimeTask(party_id, DateUtil.formatDateTime(curr)));
                    } else {
                        ThreadPoolUtil.submitTask(new RefreshProductSearchResultExpireTimeTask(party_id, DateUtil.formatDateTime(first)));
                    }
                    break;
                case 3:
                    log.info("停用活动为已结束活动，不做处理...");
                    break;
            }
        }
    }
}
