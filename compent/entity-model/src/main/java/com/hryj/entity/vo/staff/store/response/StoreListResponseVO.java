package com.hryj.entity.vo.staff.store.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: StoreInfoRequestVO
 * @description: 门店列表查询
 * @create 2018-06-26 9:09
 **/
@Data
@ApiModel(value = "门店列表信息查询响应VO")
public class StoreListResponseVO {

    
    @ApiModelProperty(value = "门店在部门组织的id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long store_id;

    @ApiModelProperty(value = "门店名称")
    private String store_name;

    @ApiModelProperty(value = "店长姓名")
    private String store_manager_name;


    @ApiModelProperty(value = "所属区域,省-市")
    private String area_name;

    @ApiModelProperty(value = "门店状态:1-正常,0-关闭")
    private Integer store_status;

    @ApiModelProperty(value = " 门店编号")
    private String store_num;


    @ApiModelProperty(value = "分摊成本标识:1-分摊,0-不分摊")
    private Integer share_cost_flag;

    @ApiModelProperty(value = "开业结束时间")
    private String open_date;

}
