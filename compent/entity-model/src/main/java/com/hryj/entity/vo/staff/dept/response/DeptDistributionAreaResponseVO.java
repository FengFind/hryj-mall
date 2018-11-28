package com.hryj.entity.vo.staff.dept.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: DeptGroupTreeResponseVO
 * @description:
 * @create 2018/6/27 0027-11:54
 **/
@Data
@ApiModel(value="门店或者仓库组织配送区域结构VO")
public class DeptDistributionAreaResponseVO {

    @ApiModelProperty(value = "id,可能是门店配送区域id,以可能是仓库配送区域id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;//主键id，key就是封装给前端用


    @ApiModelProperty(value = "配送区域区域名称,门店匹配address,仓库匹配city_name")
    private String address;

}

