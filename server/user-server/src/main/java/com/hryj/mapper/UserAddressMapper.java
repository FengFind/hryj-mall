package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.user.UserAddress;
import com.hryj.entity.vo.user.UserAddressVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 李道云
 * @className: UserAddressMapper
 * @description: 用户地址信息mapper
 * @create 2018/6/26 10:46
 **/
@Component
public interface UserAddressMapper extends BaseMapper<UserAddress> {

    /**
     * @author 李道云
     * @methodName: findUserAddressList
     * @methodDesc: 查询用户地址列表
     * @description:
     * @param: [user_id]
     * @return java.util.List<com.hryj.entity.vo.user.UserAddressVO>
     * @create 2018-07-05 10:45
     **/
    List<UserAddressVO> findUserAddressList(@Param("user_id") Long user_id);

}
