package com.hryj.entity.vo.product.partyprod.response;

import com.hryj.entity.vo.PageResponseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: PartyProductDataResponseVO
 * @description:
 * @create 2018/6/27 0027 16:51
 **/
@ApiModel(value = "门店或仓库商品、基本信息响应VO", description = "门店或仓库商品、基本信息响应VO")
@Data
public class PartyProductDataResponseVO {

    @ApiModelProperty(value = "门店或仓库商品分页查询结果")
    private PageResponseVO<PartyProductListItemResponseVO> page_result;

    @ApiModelProperty(value = "门店或仓库的基本信息")
    private PartySimpleInfoResponseVO party_info;
}
