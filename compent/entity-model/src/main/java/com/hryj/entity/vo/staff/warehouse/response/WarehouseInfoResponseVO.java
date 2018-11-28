package com.hryj.entity.vo.staff.warehouse.response;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 代廷波
 * @className: WarehouseListResponseVO
 * @description: 仓库列表查询VO
 * @create 2018/06/27 16:43
 **/
@Data
@ApiModel(description = "仓库列表查询VO")
public class WarehouseInfoResponseVO {

    @ApiModelProperty(value = "仓库id,新增不传,编辑必须传", required = false)
    @JSONField(serializeUsing= ToStringSerializer.class)
    private Long warehouse_id;

    @ApiModelProperty(value = "仓库名称")
    private String warehouse_name;

    @ApiModelProperty(value = "所属组织id")
    @JSONField(serializeUsing= ToStringSerializer.class)
    private Long dept_id;

    @ApiModelProperty(value = "所属组织名称")
    private String dept_name;

    @ApiModelProperty(value = "所在省代码")
    private String province_code;

    @ApiModelProperty(value = "所在省")
    private String province_name;

    @ApiModelProperty(value = "所在市代码")
    private String city_code;

    @ApiModelProperty(value = "所在市")
    private String city_name;

    @ApiModelProperty(value = "所在区代码")
    private String area_code;

    @ApiModelProperty(value = "所在区")
    private String area_name;

    @ApiModelProperty(value = "所在街道代码")
    private String street_code;

    @ApiModelProperty(value = "所在街道")
    private String street_name;

    @ApiModelProperty(value = "详细地址")
    private String detail_address;

    @ApiModelProperty(value = "仓库坐标,经纬度\",\"分隔")
    private String locations;

    @ApiModelProperty(value = "联系电话")
    private String telephone;

    @ApiModelProperty(value = " 联系人")
    private String contact_name;


    //*****************引用对象开始**********************************************************

    /**
     * WhDistributionAreaRequestVO:仓库配送区域Vo
     */
    List<WhDistributionAreaResponseVO> whDistributionAreaVOList;

    /**
     * WarehouseStaffResponseVO:仓库员工与组织关系请求VO
     */
    List<WarehouseStaffResponseVO> warehouseStaffVOList;

    /**
     * DeptShareConfigReqestVO：当前部门的所有父节点列表VO
     */
   // List<DeptShareListResponseVO> deptShareListResPonseVOList;

    //*****************引用对象结束**********************************************************

}
