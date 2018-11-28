package com.hryj.entity.vo.staff.warehouse.response;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: WarehouseListResponseVO
 * @description: 仓库列表查询VO
 * @create 2018/06/27 16:43
 **/
@Data
@ApiModel(description = "仓库列表信息查询VO")
public class WarehouseListResponseVO {

    
    @ApiModelProperty(value = "仓库id")
    @JSONField(serializeUsing= ToStringSerializer.class)
    private Long warehouse_id;

    @ApiModelProperty(value = "仓库名称")
    private String warehouse_name;

    @ApiModelProperty(value = "员工数量")
    private int staff_total_count;

    @ApiModelProperty(value = "所属区域")
    private String area_name;

    @ApiModelProperty(value = "仓库状态:1-正常,0-关闭")
    private Integer warehouse_status;

}
