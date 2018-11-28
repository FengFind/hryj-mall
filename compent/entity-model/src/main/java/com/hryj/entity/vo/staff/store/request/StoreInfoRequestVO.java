package com.hryj.entity.vo.staff.store.request;

import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.staff.dept.request.DeptCostRequestVO;
import com.hryj.entity.vo.staff.dept.request.DeptShareConfigReqestVO;
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
@ApiModel(value = "门店信息请求VO")
public class StoreInfoRequestVO extends RequestVO {

    
    @ApiModelProperty(value = "门店在部门组织的id 新建不传，编辑必须传")
    private Long store_id;

    @ApiModelProperty(value = "门店名称", required = true)
    private String store_name;

    @ApiModelProperty(value = "所属组织id", required = true)
    private Long dept_id;

    @ApiModelProperty(value = "所在省代码", required = true)
    private String province_code;

    @ApiModelProperty(value = "所在省", required = true)
    private String province_name;

    @ApiModelProperty(value = "所在市代", required = true)
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

    @ApiModelProperty(value = "门店坐标,经纬度\",\"分隔", required = true)
    private String locations;

    @ApiModelProperty(value = "联系电话", required = true)
    private String telephone;

    @ApiModelProperty(value = " 联系人", required = true)
    private String contact_name;

    @ApiModelProperty(value = "营业时间起:以24小时时间格式,每次30分钟为一个刻度,格式如:00:00,23:30",required = true)
    private String business_time_start;

    @ApiModelProperty(value = "营业时间止:以24小时时间格式,每次30分钟为一个刻度,格式如:00:00,23:30",required = true)
    private String business_time_end;

    @ApiModelProperty(value = " 门店编号", required = true)
    private String store_num;

    @ApiModelProperty(value = " 服务提成规则:01-自定义,02-平均分配", required = true)
    private String service_rule;

    @ApiModelProperty(value = "开业时间:格式年-月-日(yyyy-MM-dd)", required = true)
    private String open_date;

    @ApiModelProperty(value = "分摊成本标识:1-分摊,0-不分摊", required = true)
    private Integer share_cost_flag;

    /**
     * 引来对象信息 开始********************************************************
     */


    /**
     * StaffDeptRelationResponseVO:员工组织关系请求VO
     */
    List<StoreStaffRelationRequestVO> storeStaffRelationVOList;

    /**
     * DistributionAreaReponseVO：门店配送区域请求VO
     */
    List<StoreDistributionAreaRequestVO> storeDistributionAreaVOList;

    /**
     * DeptShareConfigReqestVO：组织节点分润配置请求VO
     */
     List<DeptShareConfigReqestVO> deptShareConfigVOList;

    /**
     * DeptCostRequestVO：部门组织成本请求VO
     */
    List<DeptCostRequestVO> deptCostVOList;



    /**
     * 引来对象信息 结束********************************************************
     */

}
