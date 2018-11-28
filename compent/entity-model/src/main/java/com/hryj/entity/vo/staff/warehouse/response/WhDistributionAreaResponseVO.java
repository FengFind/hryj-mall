package com.hryj.entity.vo.staff.warehouse.response;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
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
@ApiModel(description = "仓库配送区域响应VO")
public class WhDistributionAreaResponseVO {

    /*@ApiModelProperty(value = "仓库配送区域id")
    @JSONField(serializeUsing= ToStringSerializer.class)
    private Long wh_distribution_area_id;*/


    @ApiModelProperty(value = "城市名称")
    private String city_name;

    @ApiModelProperty(value = "城市id")
    @JSONField(serializeUsing= ToStringSerializer.class)
    private Long city_id;




}