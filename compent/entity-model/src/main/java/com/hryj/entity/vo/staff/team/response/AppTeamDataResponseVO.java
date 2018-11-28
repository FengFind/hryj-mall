package com.hryj.entity.vo.staff.team.response;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @author 代廷波
 * @className: AppTeamDataResponseVO
 * @description:
 * @create 2018/7/9 0009-22:24
 **/
@Data
@ApiModel(value = "团队数据VO")
public class AppTeamDataResponseVO {

    /**
     * 店员数据
     */
    List<AppTeamDataClerkResponseVO> clerkRList;
    /**
     * 部门数据
     */
    List<AppTeamDataDeptResponseVO> deptList;
    /**
     * 门店数据
     */
    List<AppTeamDataDeptResponseVO> storeList;
    /**
     * 仓库数据
     */
    List<AppTeamDataDeptResponseVO> whList;


}
