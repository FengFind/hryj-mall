package com.hryj.entity.vo.staff.dept.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 代廷波
 * @className: DeptGroupTreeResponseVO
 * @description:
 * @create 2018/6/27 0027-11:54
 **/
@Data
@ApiModel(value="根据组织id获取组织的基本信息与配送范围VO")
public class DeptDetAndDistributionAreaResponseVO {

    @ApiModelProperty(value = "id,部门组织表")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ApiModelProperty(value = "部门名称")
    private String dept_name;

    @ApiModelProperty(value = "部门上级id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long dept_pid;

    @ApiModelProperty(value = "部门级别")
    private Integer dept_level;

    @ApiModelProperty(value = "部门状态:1-正常,0-关闭")
    private Integer dept_status;

    @ApiModelProperty(value = "部门类型:01-门店,02-仓库,03-普通部门")
    private String dept_type;

    @ApiModelProperty(value = "是否为区域公司:1-是,0-否")
    private Integer company_flag;


    @ApiModelProperty(value = "组织路径:\",\"分隔")
    private String dept_path;

    @ApiModelProperty(value = "所在省代码")
    private String province_code;

    @ApiModelProperty(value = "所在省")
    private String province_name;

    @ApiModelProperty(value = "所在市代")
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

    @ApiModelProperty(value = "门店坐标,经纬度\",\"分隔")
    private String locations;


    /**
     * 门店或者仓库组织配送区域结构VO
     */
    List<DeptDistributionAreaResponseVO> deptDistributionAreaResponseVOList;
}

