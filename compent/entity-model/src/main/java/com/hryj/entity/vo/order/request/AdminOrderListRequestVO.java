package com.hryj.entity.vo.order.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: AdminOrderListRequestVO
 * @description: 后台订单列表查询请求VO
 * @create 2018/6/30 16:18
 **/
@Data
@ApiModel(value = "订单列表查询VO")
public class AdminOrderListRequestVO extends RequestVO {

    @ApiModelProperty(value = "订单编号")
    private String order_num;

    @ApiModelProperty(value = "手机号码")
    private String user_phone;

    @ApiModelProperty(value = "用户姓名")
    private String user_name;

    @ApiModelProperty(value = "支付方式")
    private String pay_method;

    @ApiModelProperty(value = "订单状态", notes = "订单状态多个用逗号分隔")
    private String order_status_list;

    @ApiModelProperty(value = "组织路径",hidden = true)
    private String party_path;

    @ApiModelProperty(value = "开始日期")
    private String start_date;

    @ApiModelProperty(value = "截止日期")
    private String end_date;

    @ApiModelProperty(value = "页码")
    private Integer page_num = 1;

    @ApiModelProperty(value = "每页大小")
    private Integer page_size = 10;

    @ApiModelProperty(value = "门店ID",hidden = true)
    private Long party_id;

    @ApiModelProperty(value = "订单类型")
    private String permission_type_id;

    @ApiModelProperty(value = "员工id",hidden = true)
    private Long staff_id;

}
