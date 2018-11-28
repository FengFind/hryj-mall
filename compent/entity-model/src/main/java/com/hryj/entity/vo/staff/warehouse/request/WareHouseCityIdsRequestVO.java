package com.hryj.entity.vo.staff.warehouse.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: WareHouseCityIdsRequestVO
 * @description: 根据城市id查询配送区域VO
 * @create 2018/7/9 18:51
 **/
@Data
@ApiModel(value="根据城市id查询配送区域VO")
public class WareHouseCityIdsRequestVO extends RequestVO {

    @ApiModelProperty(value = "父级ids集合,数组", required = true)
    private Long [] ids;

    @ApiModelProperty(value = "仓库id")
    private Long wareHouse_id;
}
