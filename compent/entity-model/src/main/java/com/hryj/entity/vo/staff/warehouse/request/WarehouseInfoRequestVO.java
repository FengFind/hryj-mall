package com.hryj.entity.vo.staff.warehouse.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 仓库信息表
 *
 * @author daitingbo
 * @since 2018-06-27
 */
@Data
@ApiModel(description = "仓库信息对象")
public class WarehouseInfoRequestVO extends RequestVO {

    @ApiModelProperty(value = "仓库id,新增不传,编辑必须传", required = false)
    private Long warehouse_id;

    @ApiModelProperty(value = "仓库名称", required = true)
    private String warehouse_name;

    @ApiModelProperty(value = "所属组织id", required = true)
    private Long dept_id;

    @ApiModelProperty(value = "所在省代码", required = true)
    private String province_code;

    @ApiModelProperty(value = "所在省", required = true)
    private String province_name;

    @ApiModelProperty(value = "所在市代码", required = true)
    private String city_code;

    @ApiModelProperty(value = "所在市", required = true)
    private String city_name;

    @ApiModelProperty(value = "所在区代码", required = true)
    private String area_code;

    @ApiModelProperty(value = "所在区", required = true)
    private String area_name;

    @ApiModelProperty(value = "所在街道代码", required = true)
    private String street_code;

    @ApiModelProperty(value = "所在街道", required = true)
    private String street_name;

    @ApiModelProperty(value = "详细地址", required = true)
    private String detail_address;

    @ApiModelProperty(value = "仓库坐标,经纬度\",\"分隔", required = true)
    private String locations;

    @ApiModelProperty(value = "联系电话", required = true)
    private String telephone;

    @ApiModelProperty(value = " 联系人", required = true)
    private String contact_name;



    //*****************引用对象开始**********************************************************

    /**
     * WhDistributionAreaRequestVO:仓库配送区域Vo
     */
    List<WhDistributionAreaRequestVO> whDistributionAreaVOList;

    /**
     * StoreStaffRelationRequestVO:仓库员工与组织关系请求VO
     */
    List<WarehouseStaffRequestVO> warehouseStaffVOList;

    /**
     * DeptShareConfigReqestVO：仓库组织节点分润对象
     */
    //List<WarehouseConfigReqestVO> warehouseConfigReqestVOList;


    //*****************引用对象结束**********************************************************

}
