package com.hryj.controller.app;

import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.userparty.request.UserPartyRequestVO;
import com.hryj.entity.vo.userparty.response.UserPartyResponseItem;
import com.hryj.exception.BizException;
import com.hryj.exception.ServerException;
import com.hryj.service.app.UserPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 王光银
 * @className: UserPartyController
 * @description:
 * @create 2018/8/15 0015 16:43
 **/
@RestController
@RequestMapping("/userParty")
public class UserPartyController {

    @Autowired
    private UserPartyService userPartyService;

    /**
     * @author 王光银
     * @methodName: searchManyPartyPolymerizationProduct
     * @methodDesc:
     * @description:
     * @param: [userPartyRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.userparty.response.UserPartyResponseItem>>
     * @create 2018-08-15 16:51
     **/
    @PostMapping("/findAroundPartyList")
    public Result<ListResponseVO<UserPartyResponseItem>> findAroundPartyList(@RequestBody UserPartyRequestVO userPartyRequestVO) throws ServerException {
        return userPartyService.findAroundPartyList(userPartyRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: getUserUniqueParty
     * @methodDesc: 返回用户关联的唯一门店ID
     * @description:
     * @param: [userPartyRequestVO]
     * @return com.hryj.common.Result<java.lang.Long>
     * @create 2018-08-16 15:30
     **/
    @PostMapping("/getUserUniqueParty")
    public Result<Long> getUserUniqueParty(@RequestBody UserPartyRequestVO userPartyRequestVO) throws BizException {
        return userPartyService.getUserUniqueParty(userPartyRequestVO);
    }
}
