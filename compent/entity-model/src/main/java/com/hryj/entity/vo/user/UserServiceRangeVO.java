package com.hryj.entity.vo.user;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 李道云
 * @className: UserServiceRangeVO
 * @description: 用户服务范围VO
 * @create 2018/7/2 20:56
 **/
@Data
public class UserServiceRangeVO implements Serializable {

    /**
     * 服务于用户的门店集合
     */
    private List<UserPartyVO> storeList;
    /**
     * 服务于用户的仓库中心
     */
    private UserPartyVO warehouse;
    /**
     * 用户默认门店或仓库
     */
    private UserPartyVO defaultParty;
}
