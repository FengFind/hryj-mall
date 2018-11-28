package com.hryj.entity.vo.staff.warehouse.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 仓库配送区域
 *
 * @author daitingbo
 * @since 2018-06-27
 */
@Data
@ApiModel(description = "仓库配送区域VO")
public class WhDistributionAreaRequestVO {

  /*  @ApiModelProperty(value = "仓库配送区域id", required = false)
    private Long wh_distribution_area_id;
*/

   /* @ApiModelProperty(value = "仓库id", required = false)
    private Long dept_id;*/

    @ApiModelProperty(value = "城市名称", required = true)
    private String city_name;

    @ApiModelProperty(value = "城市id", required = true)
    private Long city_id;




}
