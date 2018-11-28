package com.hryj.service.worktask;

import com.hryj.service.util.cacheutil.PartyActivityMonitorCacheUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 王光银
 * @className: CleanPartyActivityThruDateTask
 * @description: 清除一个门店关联的过期活动日期数据
 * @create 2018/8/31 0031 12:12
 **/
@Slf4j
public class CleanPartyActivityThruDateTask implements Runnable {

    private Long party_id;

    public CleanPartyActivityThruDateTask(Long party_id) {
        this.party_id = party_id;
    }

    @Override
    public void run() {
        if (this.party_id == null || this.party_id <= 0L) {
            return;
        }

        PartyActivityMonitorCacheUtil.cleanThruDate(this.party_id);
    }
}
