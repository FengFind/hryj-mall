package com.hryj.entity.vo.staff.user.response;

import com.hryj.entity.bo.staff.user.StaffUserInfo;
import com.hryj.entity.vo.staff.user.request.StaffUserExcelVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 代廷波
 * @className: StaffUserUploadResponseVO
 * @description:
 * @create 2018/9/27 0027-11:17
 **/
@Data
@ApiModel(value = "员工上传响应信息VO")
public class StaffUserUploadResponseVO {

    @ApiModelProperty(value = "错误总数据", required = true)
    private Integer errer_count;

    @ApiModelProperty(hidden = true)
    List<StaffUserExcelVO> errer_list;

    @ApiModelProperty(hidden = true)
    List<StaffUserInfo> success_List;

}
