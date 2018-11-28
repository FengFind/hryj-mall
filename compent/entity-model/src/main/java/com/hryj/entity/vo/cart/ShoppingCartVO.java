package com.hryj.entity.vo.cart;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 李道云
 * @className: ShoppingCartVO
 * @description: 购物车VO
 * @create 2018/6/29 19:47
 **/
@ApiModel(description = "购物车VO")
@Data
public class ShoppingCartVO {

    @ApiModelProperty(value = "用户编号")
    private Long user_id;

    @ApiModelProperty(value = "仓库或门店id", required = true)
    private Long party_id;

    @ApiModelProperty(value = "代下单员工编号", required = true)
    private Long help_staff_id;

    @ApiModelProperty(value = "部门类型：01-门店,02-仓库,03-普通部门", notes = "01-门店,02-仓库,03-普通部门", required = true)
    private String dept_type;

    @ApiModelProperty(value = "仓库或门店名称", required = true)
    private String party_name;

    @ApiModelProperty(value = "仓库或门店联系人姓名")
    private String party_contact_name;

    @ApiModelProperty(value = "仓库或门店联系人电话")
    private String party_contact_phone;

    @ApiModelProperty(value = "仓库或门店地址")
    private String party_address;

    @ApiModelProperty(value = "购物车商品列表", required = true)
    private List<ShoppingCartPoductVO> cartPorductList=new ArrayList<>();

}
