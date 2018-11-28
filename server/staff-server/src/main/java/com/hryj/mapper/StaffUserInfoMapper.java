package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hryj.entity.bo.staff.user.StaffUserInfo;
import com.hryj.entity.vo.staff.team.response.AppTeamDataClerkResponseVO;
import com.hryj.entity.vo.staff.team.response.AppTeamDataDeptResponseVO;
import com.hryj.entity.vo.staff.user.StaffDeptVO;
import com.hryj.entity.vo.staff.user.StaffUserVO;
import com.hryj.entity.vo.staff.user.request.StaffIdRequestVO;
import com.hryj.entity.vo.staff.user.request.StaffListParamRequestVO;
import com.hryj.entity.vo.staff.user.request.StaffUserExcelVO;
import com.hryj.entity.vo.staff.user.response.StaffListResponseVO;
import com.hryj.entity.vo.staff.user.response.StaffResponseVO;
import com.hryj.entity.vo.user.UserPartyVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 员工用户信息表 Mapper 接口
 *
 * @author daitingbo
 * @since 2018-06-26
 */
@Component
public interface StaffUserInfoMapper extends BaseMapper<StaffUserInfo> {

    /**
     * @return
     * @author 代廷波
     * @description: 根据id查找员工
     * @param: staff_id 员工id
     * @create 2018/06/26 14:50
     **/
    StaffResponseVO getStaffByIdDet(StaffIdRequestVO staffIdRequestVO);

    /**
     * @return
     * @author 代廷波
     * @description: 查询员工列表
     * @param: vo
     * @create 2018/06/30 10:51
     **/
    List<StaffListResponseVO> getStaffList(StaffListParamRequestVO staffListParamRequestVO, Page page);

    /**
     * @return
     * @author 代廷波
     * @description: 根据员工账号获取员工详情
     * @param: staff_account 员工账号
     * @create 2018/06/30 10:51
     **/
    Map<String, Object> getStaffAccountByDet(@Param("staff_account") String staff_account);

    /**
     * @return
     * @author 代廷波
     * @description: 推荐码统计
     * @param: code 推荐码
     * @create 2018/06/30 15:25
     **/
    Integer getStaffReferralCodeCount(@Param("referral_code") String referral_code);

    /**
     * @return java.util.List<com.hryj.entity.vo.staff.user.StaffUserVO>
     * @author 李道云
     * @methodName: findStaffListByDeptId
     * @methodDesc: 根据部门id查询员工列表
     * @description:
     * @param: [dept_id]
     * @create 2018-07-05 13:50
     **/
    List<StaffUserVO> findStaffListByDeptId(@Param("dept_id") Long dept_id);

    /**
     * @return java.util.Map<java.lang.String   ,   java.lang.Object>
     * @author 李道云
     * @methodName: findStaffUserVOByStaffId
     * @methodDesc: 根据员工id查询员工基本信息
     * @description:
     * @param: [staff_id]
     * @return com.hryj.entity.vo.staff.user.StaffUserVO
     * @create 2018-07-10 20:54
     **/
    StaffUserVO findStaffUserVOByStaffId(@Param("staff_id") Long staff_id);

    /**
     * @author 李道云
     * @methodName: findStaffPermResource
     * @methodDesc: 查询员工的权限资源
     * @description:
     * @param: [staff_id]
     * @create 2018-07-05 17:18
     **/
    Map<String, Object> findStaffPermResource(@Param("staff_id") Long staff_id);

    /**
     * @return com.hryj.entity.vo.staff.user.StaffDeptVO
     * @author 李道云
     * @methodName: findStaffDeptVO
     * @methodDesc: 查询员工部门信息
     * @description:
     * @param: [referral_code, phone_num, staff_id]
     * @create 2018-07-06 12:28
     **/
    StaffDeptVO findStaffDeptVO(@Param("referral_code") String referral_code, @Param("phone_num") String phone_num, @Param("staff_id") Long staff_id);

    /**
     * @return java.util.List<com.hryj.entity.vo.user.UserPartyVO>
     * @author 李道云
     * @methodName: findStoreListByPoiId
     * @methodDesc: 根据poi位置获取门店列表
     * @description:
     * @param: [poi_id]
     * @create 2018-07-06 14:40
     **/
    List<UserPartyVO> findStoreListByPoiId(@Param("poi_id") String poi_id);

    /**
     * @return com.hryj.entity.vo.user.UserPartyVO
     * @author 李道云
     * @methodName: findWarehouseByCityCode
     * @methodDesc: 根据城市代码查询仓库中心
     * @description:
     * @param: [city_code]
     * @create 2018-07-06 15:03
     **/
    UserPartyVO findWarehouseByCityCode(@Param("city_code") String city_code);

    /**
     * @author 李道云
     * @methodName: findStoreStaffUserList
     * @methodDesc: 查询底下所有店员列表
     * @description:
     * @param: [dept_path]
     * @return java.util.List<com.hryj.entity.vo.staff.team.response.AppTeamDataClerkResponseVO>
     * @create 2018-07-25 9:47
     **/
    List<AppTeamDataClerkResponseVO> findStoreStaffUserList(@Param("dept_path") String dept_path);

    /**
     * @author 李道云
     * @methodName: findUnderDeptList
     * @methodDesc: 查询底下所有部门列表
     * @description:
     * @param: [dept_path]
     * @return java.util.List<com.hryj.entity.vo.staff.team.response.AppTeamDataDeptResponseVO>
     * @create 2018-07-25 9:47
     **/
    List<AppTeamDataDeptResponseVO> findUnderDeptList(@Param("dept_path") String dept_path);

    /**
     * @author 李道云
     * @methodName: findUnderStoreList
     * @methodDesc: 查询底下所有部门列表
     * @description:
     * @param: [dept_path]
     * @return java.util.List<com.hryj.entity.vo.staff.team.response.AppTeamDataStoreResponseVO>
     * @create 2018-07-25 9:47
     **/
    List<AppTeamDataDeptResponseVO> findUnderStoreList(@Param("dept_path") String dept_path);

    /**
     * @author 李道云
     * @methodName: findUnderWHList
     * @methodDesc: 查询底下所有仓库列表
     * @description:
     * @param: [dept_path]
     * @return java.util.List<com.hryj.entity.vo.staff.team.response.AppTeamDataDeptResponseVO>
     * @create 2018-07-25 9:47
     **/
    List<AppTeamDataDeptResponseVO> findUnderWHList(@Param("dept_path") String dept_path);
    /**
     * @author 代廷波
     * @description: 查询员工身份证或者手机
     * @param: null
     * @return
     * @create 2018/09/27 16:00
     **/
    List<StaffUserExcelVO> getStaffIdCardAndPhonNum(List<StaffUserExcelVO> list);
}
