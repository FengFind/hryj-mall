package com.hryj.entity.vo.order.response;

import com.hryj.entity.vo.order.ReturnProductVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author 罗秋涵
 * @className: ReturnOrderDetailsResponseVO
 * @description: 退货单商品详情VO
 * @create 2018/7/4 0004 10:12
 **/
@Data
@ApiModel(value = "退货单商品详情VO")
public class ReturnOrderDetailsResponseVO {

    @ApiModelProperty(value = "退货单编号", required = true)
    private String return_id;

    @ApiModelProperty(value = "退货申请人姓名", required = true)
    private String return_apply_name;

    @ApiModelProperty(value = "退货申请时间", required = true)
    private Date return_apply_time;

    @ApiModelProperty(value = "退货申请原因", required = true)
    private String return_reason;

    @ApiModelProperty(value = "退货申请图片", required = true)
    private String return_image;

    @ApiModelProperty(value = "用户姓名", required = true)
    private String user_name;

    @ApiModelProperty(value = "用户电话", required = true)
    private String user_phone;

    @ApiModelProperty(value = "用户地址", required = true)
    private String user_address;

    @ApiModelProperty(value = "申请退货次数", required = true)
    private Integer return_num;

    @ApiModelProperty(value = "退货商品列表", required = true)
    private List<ReturnProductVO> returnProductList;
}
