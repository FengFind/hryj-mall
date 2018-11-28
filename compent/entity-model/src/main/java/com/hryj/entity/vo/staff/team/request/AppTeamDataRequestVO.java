package com.hryj.entity.vo.staff.team.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: AppTeamDataResponseVO
 * @description:
 * @create 2018/7/9 0009-22:24
 **/
@Data
@ApiModel(value = "团队请求对象VO")
public class AppTeamDataRequestVO extends RequestVO {


    @ApiModelProperty(value = "传了组织dept_id根据组织dept_id查,不传dept_id根据login_token查询",required = false)
    private Long dept_id;

}
