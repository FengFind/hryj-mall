package com.hryj.entity.vo.product.partyprod.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: PartyProductStatisticsRequestVO
 * @description:
 * @create 2018/7/5 0005 21:01
 **/
@ApiModel(value = "门店或仓库商品统计请求VO", description = "门店或仓库商品统计请求VO")
@Data
public class PartyProductStatisticsRequestVO extends RequestVO {

    @ApiModelProperty(value = "门店或仓库的ID集合", required = true)
    private List<Long> party_id_list;

    @ApiModelProperty(value = "是否返回门店或仓库的所有商品数量，包含下架和下架的,true返回，false不返回，缺省 true")
    private Boolean return_all = true;

    @ApiModelProperty(value = "是否返回门店或仓库的上架的商品数量, true返回，false不返回，缺省 false")
    private Boolean return_up = false;

    @ApiModelProperty(value = "是否返回门店或仓库的下架的商品数量, true返回，false不返回，缺省 false")
    private Boolean return_down = false;
}
