package com.hryj.service.worktask;

import com.alibaba.fastjson.JSON;
import com.hryj.common.Result;
import com.hryj.entity.vo.user.UserPartyVO;
import com.hryj.entity.vo.user.request.DefaultPartyRequestVO;
import com.hryj.feign.UserFeignClient;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 王光银
 * @className: SetUserDefaultPartyTask
 * @description:
 * @create 2018/8/28 0028 15:47
 **/
@Slf4j
public class SetUserDefaultPartyTask implements Runnable {

    private String login_token;

    private UserPartyVO partyVO;

    private UserFeignClient userFeignClient;

    public SetUserDefaultPartyTask(String login_token, UserPartyVO partyVO, UserFeignClient userFeignClient) {
        this.login_token = login_token;
        this.partyVO = partyVO;
        this.userFeignClient = userFeignClient;
    }

    @Override
    public void run() {
        if (UtilValidate.isEmpty(login_token) || partyVO == null) {
            log.warn("设置用户默认门店任务, 参数检查未通过, login_token=[" + login_token + "], partyVo=" + (partyVO == null ? null : JSON.toJSONString(partyVO)));
            return;
        }

        DefaultPartyRequestVO requestVO = new DefaultPartyRequestVO();
        requestVO.setLogin_token(this.login_token);
        requestVO.setParty_id(this.partyVO.getParty_id());
        requestVO.setParty_name(this.partyVO.getParty_name());
        requestVO.setParty_address(this.partyVO.getParty_address());
        requestVO.setDistance(this.partyVO.getDistance());
        requestVO.setDept_type(this.partyVO.getDept_type());

        try {
            Result result = this.userFeignClient.setDefaultParty(requestVO);
            if (result.isFailed()) {
                log.warn("设置用户默认门店任务失败, login_token=[" + login_token + "], partyVo=" + (partyVO == null ? null : JSON.toJSONString(partyVO)));
            }
        } catch (Exception e) {
            log.error("设置用户默认门店任务失败:", e);
        }
    }
}
