package com.hryj.entity.vo.staff.dept.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: StoreInfoRequestVO
 * @description: 门店信息请求VO
 * @create 2018-06-26 9:09
 **/
@Data
@ApiModel(value = "app-根据组织id获取门店或者仓库详情VO")
public class DeptIdByStoreOrWarehouseResponseVO {

    
    @ApiModelProperty(value = "组织id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long dept_id;

    @ApiModelProperty(value = "所属组织名称")
    private String dept_name;

    @ApiModelProperty(value = "部门类型:01-门店,02-仓库,03-普通部门")
    private String dept_type;

    @ApiModelProperty(value = "详细地址")
    private String detail_address;

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

    @ApiModelProperty(value = "联系电话")
    private String telephone;

    @ApiModelProperty(value = "联系人")
    private String contact_name;

    @ApiModelProperty(value = "营业时间起")
    private String business_time_start;

    @ApiModelProperty(value = "营业时间止")
    private String business_time_end;

    @ApiModelProperty(value = "送货上门的说明")
    private String delivery_info;

    @ApiModelProperty(value = "部门状态:1-正常,0-关闭")
    private Integer dept_status;

    @ApiModelProperty(value = "组织路径:\",\"分隔")
    private String dept_path;


}
