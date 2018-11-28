package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hryj.entity.bo.user.UserInfo;
import com.hryj.entity.vo.user.request.UserListRequestVO;
import com.hryj.entity.vo.user.UserInfoVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 李道云
 * @className: UserInfoMapper
 * @description: 用户信息mapper
 * @create 2018/6/26 10:45
 **/
@Component
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    /**
     * @author 李道云
     * @methodName: searchUserList
     * @methodDesc: 分页查询用户信息
     * @description:
     * @param: [userListRequestVO,page]
     * @return java.util.List<com.hryj.entity.vo.user.UserInfoVO>
     * @create 2018-06-28 10:45
     **/
    List<UserInfoVO> searchUserList(UserListRequestVO userListRequestVO, Page page);

    /**
     * @author 李道云
     * @methodName: findUserInfoVOByUserId
     * @methodDesc:
     * @description:
     * @param: [user_id, phone_num]
     * @return com.hryj.entity.vo.user.UserInfoVO
     * @create 2018-07-10 20:39
     **/
    UserInfoVO findUserInfoVOByUserId(@Param("user_id") Long user_id, @Param("phone_num") String phone_num);

    /**
     * @author 李道云
     * @methodName: findReferralRegisterUserList
     * @methodDesc: 查询推荐注册用户列表
     * @description:
     * @param: [staff_id, store_id, start_date, end_date]
     * @return java.util.List<com.hryj.entity.vo.user.UserInfoVO>
     * @create 2018-07-07 17:05
     **/
    List<UserInfoVO> findReferralRegisterUserList(@Param("staff_id") Long staff_id, @Param("store_id") Long store_id, @Param("dept_path") String dept_path,
                                                  @Param("start_date") String start_date, @Param("end_date") String end_date);


}
