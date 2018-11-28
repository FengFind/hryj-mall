package com.hryj.entity.vo.staff.store.request;

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
@ApiModel(value = "门店id请求VO")
public class StoreIdRequestVO {

    
    @ApiModelProperty(value = "门店id ", required = true)
    private Long store_id;


}
