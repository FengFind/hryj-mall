package com.hryj.entity.vo.staff.store.response;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 根据门店id匹配仓库配送区域响应VO
 *
 * @author daitingbo
 * @since 2018-06-27
 */
@Data
@ApiModel(value = "根据门店id匹配仓库配送区域响应VO")
public class StoreWhDistributionAreaResponseVO {

    @ApiModelProperty(value = "仓库配送区域id")
    @JSONField(serializeUsing= ToStringSerializer.class)
    private Long id;

    @ApiModelProperty(value = "城市名称")
    private String city_name;

    @ApiModelProperty(value = "城市id")
    @JSONField(serializeUsing= ToStringSerializer.class)
    private Long city_id;




}