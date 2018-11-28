package com.hryj.entity.vo.sys.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: CityAreaListRequestVO
 * @description: 城市区域列表查询请求VO
 * @create 2018/7/9 18:51
 **/
@Data
@ApiModel(value="城市区域列表查询请求VO")
public class CityAreaListRequestVO extends RequestVO {

    @ApiModelProperty(value = "父级id", required = true)
    private Long pid;
}
