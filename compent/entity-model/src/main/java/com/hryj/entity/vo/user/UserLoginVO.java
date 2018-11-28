package com.hryj.entity.vo.user;

import com.hryj.entity.bo.user.UserAddress;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 李道云
 * @className: UserLoginVO
 * @description: 用户登录VO
 * @create 2018/6/27 8:36
 **/
@Data
public class UserLoginVO implements Serializable {

    private static final long serialVersionUID = 1267841769765066754L;
    /**
     * 用户id
     */
    private Long user_id;
    /**
     * 手机号码
     */
    private String phone_num;
    /**
     * 推荐码
     */
    private String referral_code;
    /**
     * 用户地址poi的id
     */
    private String poi_id;
    /**
     * 用户所在城市代码
     */
    private String city_code;
    /**
     * 收货人
     */
    private String receive_name;
    /**
     * 收货手机号
     */
    private String receive_phone;
    /**
     * 收货地址
     */
    private String receive_address;
    /**
     * 位置坐标,经纬度","分隔
     */
    private String locations;
    /**
     * 用户默认地址信息
     */
    private UserAddress userAddress;
    /**
     * 用户默认门店或仓库
     */
    private UserPartyVO defaultParty;
    /**
     * 服务于用户的门店集合
     */
    private List<UserPartyVO> storeList;
    /**
     * 服务于用户的仓库中心
     */
    private UserPartyVO warehouse;
}
