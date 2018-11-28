package com.hryj.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.cache.CodeCache;
import com.hryj.cache.LoginCache;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.user.UserAddress;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.user.UserAddressVO;
import com.hryj.entity.vo.user.UserLoginVO;
import com.hryj.mapper.UserAddressMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 李道云
 * @className: UserAddressService
 * @description: 用户地址service
 * @create 2018/6/27 8:44
 **/
@Service
public class UserAddressService extends ServiceImpl<UserAddressMapper,UserAddress> {

    /**
     * 获取用户默认地址
     * @param user_id
     * @return
     */
    public UserAddress getUserDefaultAddressByUserId(Long user_id){
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("user_id",user_id);
        wrapper.eq("default_flag",1);
        return super.selectOne(wrapper);
    }

    /**
     * @author 李道云
     * @description: 保存用户地址信息
     * @param: [user_id, userRegisterRequestVO]
     * @return com.hryj.entity.bo.user.UserAddress
     * @create 2018-06-26 12:47
     **/
    @Transactional
    public UserAddress saveUserAddress(Long user_id, UserAddressVO userAddressVO){
        UserAddress userAddress = new UserAddress();
        BeanUtil.copyProperties(userAddressVO,userAddress);//对象属性拷贝
        userAddress.setUser_id(user_id);
        userAddress.setDefault_flag(true);
        super.insert(userAddress);
        return userAddress;
    }

    /**
     * @author 李道云
     * @methodName: findUserAddressList
     * @methodDesc: 查询用户地址列表
     * @description:
     * @param: [user_id]
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.user.UserAddressVO>>
     * @create 2018-07-05 10:53
     **/
    public Result<ListResponseVO<UserAddressVO>> findUserAddressList(Long user_id){
        List<UserAddressVO> addressVOList = baseMapper.findUserAddressList(user_id);
        return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>(addressVOList));
    }

    /**
     * @author 李道云
     * @methodName: updateUserAddress
     * @methodDesc: 更新用户地址信息
     * @description:
     * @param: [userAddressVO]
     * @return com.hryj.common.Result
     * @create 2018-07-02 10:49
     **/
    @Transactional
    public Result updateUserAddress(UserAddressVO userAddressVO){
        Result result = validateUserAddress(userAddressVO);
        if(result.isFailed()){
            return result;
        }
        if(userAddressVO.getAddress_id() ==null){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"地址id不能为空");
        }
        UserAddress userAddress = new UserAddress();
        BeanUtil.copyProperties(userAddressVO,userAddress);//对象属性拷贝
        userAddress.setId(userAddressVO.getAddress_id());
        UserLoginVO userLoginVO = LoginCache.getUserLoginVO(userAddressVO.getLogin_token());
        if(userLoginVO !=null){
            userAddress.setUser_id(userLoginVO.getUser_id());
        }
        super.updateById(userAddress);
        return new Result(CodeEnum.SUCCESS);
    }

    /**
     * 校验地址信息
     * @param userAddressVO
     * @return
     */
    public Result validateUserAddress(UserAddressVO userAddressVO){
        if(userAddressVO ==null){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"地址信息不能为空");
        }
        if(StrUtil.isEmpty(userAddressVO.getPoi_id())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"poi信息不能为空");
        }
        if(StrUtil.isEmpty(userAddressVO.getLocation_name())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"poi位置名称不能为空");
        }
        if(StrUtil.isEmpty(userAddressVO.getLocation_address())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"poi位置地址不能为空");
        }
        if(StrUtil.isEmpty(userAddressVO.getLocations())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"poi位置坐标不能为空");
        }
        if(StrUtil.isEmpty(userAddressVO.getReceive_name()) || StrUtil.isEmpty(userAddressVO.getReceive_phone())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"收货人信息不能为空");
        }
        if(userAddressVO.getReceive_name().length()>16){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"收货人姓名太长");
        }
        if(StrUtil.isEmpty(userAddressVO.getReceive_phone())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"收货人手机号不能为空");
        }
        if(StrUtil.isEmpty(userAddressVO.getProvince_code())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"省份不能为空");
        }
        userAddressVO.setProvince_name(CodeCache.getNameByValue("CityArea",userAddressVO.getProvince_code()));
        if(StrUtil.isEmpty(userAddressVO.getArea_code())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"区域不能为空");
        }
        if(StrUtil.isNotEmpty(userAddressVO.getArea_code())){
            userAddressVO.setCity_code(userAddressVO.getArea_code().substring(0,4)+"00");
            userAddressVO.setCity_name(CodeCache.getNameByValue("CityArea",userAddressVO.getCity_code()));
            userAddressVO.setArea_name(CodeCache.getNameByValue("CityArea",userAddressVO.getArea_code()));
        }
        if(StrUtil.isEmpty(userAddressVO.getDetail_address())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"收货人详细地址不能为空");
        }
        if(userAddressVO.getDetail_address().length()>64){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"收货人详细地址太长");
        }
        return new Result(CodeEnum.SUCCESS);
    }

}
