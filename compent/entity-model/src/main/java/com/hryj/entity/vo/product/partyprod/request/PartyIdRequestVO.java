package com.hryj.entity.vo.product.partyprod.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: PartyIdRequestVO
 * @description: 门店或仓库ID请求VO
 * @create 2018/7/5 0005 10:36
 **/
@ApiModel(value = "门店或仓库ID请求VO", description = "门店或仓库ID请求VO")
@Data
public class PartyIdRequestVO extends RequestVO {

    @ApiModelProperty(value = "分门店或仓库ID", required = true)
    private Long party_id;
}
