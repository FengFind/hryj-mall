package com.hryj.entity.vo.staff.storewarehouse.request;

import com.hryj.entity.vo.PageRequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: SearchPartyRequestVO
 * @description:
 * @create 2018/6/27 0027 16:08
 **/
@ApiModel(value = "门店或仓库查询请求VO", description = "门店或仓库查询请求VO")
@Data
public class SearchPartyRequestVO extends PageRequestVO {

    @ApiModelProperty(value = "组织类型，store门店，warehouse仓库, 缺省时为 store", required = true)
    private String party_type;

    @ApiModelProperty(value = "组织名称，门店或仓库的名称")
    private String party_name;

    @ApiModelProperty(value = "组织负责人，比如店长")
    private String party_leader;

    @ApiModelProperty(value = "所在地省的代码")
    private String province_code;

    @ApiModelProperty(value = "所在地市的代码")
    private String city_code;

    @ApiModelProperty(value = "所在地区的代码")
    private String area_code;

    @ApiModelProperty(value = "是否统计门店或仓库的商品数量, true统计， false不统计，默认true")
    private Boolean statistics_party_product = true;

}
