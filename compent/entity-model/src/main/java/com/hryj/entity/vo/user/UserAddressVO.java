package com.hryj.entity.vo.user;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: UserAddressVO
 * @description: 用户地址VO
 * @create 2018/6/27 20:55
 **/
@Data
@ApiModel(value="用户地址VO")
public class UserAddressVO extends RequestVO {

    @ApiModelProperty(value = "用户地址id",notes = "修改用户地址时有该值")
    private Long address_id;

    @ApiModelProperty(value = "poi位置id", required = true)
    private String poi_id;

    @ApiModelProperty(value = "位置类型code", required = true, notes = "poi的类型代码")
    private String location_type_code;

    @ApiModelProperty(value = "位置类型名称", required = true, notes = "poi的类型名称")
    private String location_type_name;

    @ApiModelProperty(value = "位置名称", required = true, notes = "poi的名称")
    private String location_name;

    @ApiModelProperty(value = "位置地址", required = true, notes = "poi的地址")
    private String location_address;

    @ApiModelProperty(value = "位置坐标", required = true, notes = "poi的坐标")
    private String locations;

    @ApiModelProperty(value = "所在省代码", required = true, notes = "poi的所在省代码")
    private String province_code;

    @ApiModelProperty(value = "所在省名称", required = true, notes = "poi的所在省名称")
    private String province_name;

    @ApiModelProperty(value = "所在城市代码", required = true, notes = "poi的所在城市代码")
    private String city_code;

    @ApiModelProperty(value = "所在城市名称", required = true, notes = "poi的所在城市名称")
    private String city_name;

    @ApiModelProperty(value = "所在区代码", required = true, notes = "poi的所在区代码")
    private String area_code;

    @ApiModelProperty(value = "所在区名称", required = true, notes = "poi的所在区名称")
    private String area_name;

    @ApiModelProperty(value = "详细地址", required = true, notes = "用户填写的详细地址")
    private String detail_address;

    @ApiModelProperty(value = "收货人姓名", required = true, notes = "用户填写的收货人姓名")
    private String receive_name;

    @ApiModelProperty(value = "收货人手机号", required = true, notes = "用户填写的收货人手机号")
    private String receive_phone;

    @ApiModelProperty(value = "默认标识:1-是,0-否",hidden = true)
    private Boolean default_flag = true;
}
