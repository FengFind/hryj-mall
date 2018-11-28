package com.hryj.entity.vo.staff.user.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: StaffIdRequestVO
 * @description: 员工IDVO
 * @create 2018/7/5 0005-11:25
 **/
@Data
@ApiModel(description = "员工IDVO")
public class StaffIdRequestVO extends RequestVO {

    @ApiModelProperty(value = "员工id", required = true)
    private Long staff_id;

}
