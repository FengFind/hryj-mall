package com.hryj.entity.vo.staff.store.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hryj.entity.vo.staff.dept.response.DeptCostResponseVO;
import com.hryj.entity.vo.staff.dept.response.DeptShareListResponseVO;
import com.hryj.entity.vo.staff.user.response.StaffDeptRelationResponseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 李道云
 * @className: StoreInfoRequestVO
 * @description: 门店信息请求VO
 * @create 2018-06-26 9:09
 **/
@Data
@ApiModel(value = "门店编辑信息响应VO")
public class StoreInfoResponseVO{

    
    @ApiModelProperty(value = "门店在部门组织的id 新境不传，编辑必须传")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long store_id;

    @ApiModelProperty(value = "门店名称")
    private String store_name;

    @ApiModelProperty(value = "所属组织名称")
    private String dept_name;

    @ApiModelProperty(value = "所属组织id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long dept_id;

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

    @ApiModelProperty(value = "联系电话")
    private String telephone;

    @ApiModelProperty(value = " 联系人")
    private String contact_name;

    @ApiModelProperty(value = "营业时间起")
    private String business_time_start;

    @ApiModelProperty(value = "营业时间止")
    private String business_time_end;

    @ApiModelProperty(value = " 门店编号")
    private String store_num;

    @ApiModelProperty(value = " 服务提成规则:01-自定义,02-平均分配")
    private String service_rule;

    @ApiModelProperty(value = "开业时间:格式年-月-日(yyyy-MM-dd)")
    private String open_date;

    @ApiModelProperty(value = "分摊成本标识:1-分摊,0-不分摊")
    private Integer share_cost_flag;

    /**
     * 引来对象信息 开始********************************************************
     */


    /**
     * StaffDeptRelationResponseVO:员工组织关系请求VO
     */
    List<StaffDeptRelationResponseVO> storeStaffRelationVOList;

    /**
     * DistributionAreaReponseVO：门店配送区域请求VO
     */
    List<StoreDistributionAreaResponseVO> storeDistributionAreaVOList;
    /**
     * DeptParentListResponseVO：当前部门的所有父节点列表vo
     */
     List<DeptShareListResponseVO> deptShareConfigVOList;

    /**
     * DeptCostRequestVO：部门组织成本请求VO
     */
    List<DeptCostResponseVO> deptCostVOList;



/*
    List<StoreStaffRelationRequestVO> storeStaffDeptRelationVOList;


    List<StoreDistributionAreaRequestVO> storeDistributionAreaVOList;


    List<DeptShareConfigReqestVO> deptShareConfigVOList;

    List<DeptCostRequestVO> deptCostVOList;
*/



    /**
     * 引来对象信息 结束********************************************************
     */

}
