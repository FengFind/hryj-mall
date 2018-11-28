package com.hryj.controller;

import cn.hutool.core.codec.Base64;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.exception.ServerException;
import com.hryj.service.CebNotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 白飞
 * @className: CebNotifyController
 * @description:  http://192.168.50.137:10080/ceb/notify/geteway
 * http://feitianmao1122.xicp.net:33061/ceb/notify/geteway
 * @create 2018/9/26 13:10
 **/
@Slf4j
@RestController
@RequestMapping("/ceb/notify")
public class CebNotifyController {

    @Autowired
    private CebNotifyService cebNotifyService;

    /**
     * @author 白飞
     * @methodName: notify
     * @methodDesc: 海关回执接收
     * @description:
     * @param: [data]
     * @return
     * @create 2018-09-26 13:16
     **/
    @PostMapping("/geteway")
    public Result geteway(@RequestParam String data) throws ServerException {
        log.info("data海关通知=====================" + data);
        data = data.replace(" ", "+").replace("\n", "");
        cebNotifyService.notify(Base64.decodeStr(data));
        return new Result(CodeEnum.SUCCESS);
    }
}
