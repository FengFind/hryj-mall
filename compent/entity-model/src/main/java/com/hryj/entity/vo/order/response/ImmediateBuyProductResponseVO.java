package com.hryj.entity.vo.order.response;

import com.hryj.entity.vo.order.ImmediateBuyProductInfoVO;
import com.hryj.entity.vo.order.response.opentime.OpenTimeResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 罗秋涵
 * @className: ImmediateBuyProductResponseVO
 * @description: 立即购买商品信息
 * @create 2018/7/9 0009 14:33
 **/
@Data
@ApiModel(value = "立即购买商品信息")
public class ImmediateBuyProductResponseVO {

    @ApiModelProperty(value = "仓库或门店id", required = true)
    private Long party_id;

    @ApiModelProperty(value = "部门类型", notes = "01-门店,02-仓库", required = true)
    private String dept_type;

    @ApiModelProperty(value = "仓库或门店名称", required = true)
    private String party_name;

    @ApiModelProperty(value = "仓库或门店联系人姓名")
    private String party_contact_name;

    @ApiModelProperty(value = "仓库或门店联系人电话")
    private String party_contact_phone;

    @ApiModelProperty(value = "仓库或门店地址")
    private String party_address;

    @ApiModelProperty(value = "商品信息")
    private ImmediateBuyProductInfoVO productInfo;

    @ApiModelProperty(value = "送货上门的日期", notes = "Map<当前日期及向上六天,List<每个日期对应的时间段>>")
    private  List<OpenTimeResponse> delivery_time;

    @ApiModelProperty(value = "送货上门的说明")
    private String delivery_info;

    @ApiModelProperty(value = "订单金额")
    private String order_amt;

    @ApiModelProperty(value = "优惠金额")
    private String discount_amt;
}
