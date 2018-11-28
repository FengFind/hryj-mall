package com.hryj.entity.vo.order;

import com.hryj.entity.vo.staff.user.StaffAdminLoginVO;
import com.hryj.entity.vo.staff.user.StaffAppLoginVO;
import com.hryj.entity.vo.user.UserLoginVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 叶方宇
 * @className: OrderStatusRecordInsertVo
 * @description:
 * @create 2018/7/11 0011 14:38
 **/

@Data
@ApiModel(value = "订单状态记录表添加数据关键字段")
public class OrderStatusRecordInsertVo {

    @ApiModelProperty(value="订单状态",hidden=true)
    private String orderStatus;

    @ApiModelProperty(value="状态说明",hidden=true)
    private String status_remark;

    @ApiModelProperty(value="变更原因",hidden=true)
    private String change_reason;

    @ApiModelProperty(value="订单ID",hidden=true)
    private Long order_id;

    @ApiModelProperty(value="用户")
    private UserLoginVO userLoginVO;

    @ApiModelProperty(value="员工")
    private StaffAppLoginVO staffAppLoginVO;

    @ApiModelProperty(value="后端")
    private StaffAdminLoginVO staffAdminLoginVO;

}
