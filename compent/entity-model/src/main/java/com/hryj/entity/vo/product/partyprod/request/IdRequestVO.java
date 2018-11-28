package com.hryj.entity.vo.product.partyprod.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: PartyIdRequestVO
 * @description: 单条数据请求VO
 * @create 2018/7/5 0005 10:36
 **/
@ApiModel(value = "单条数据请求VO", description = "单条数据请求VO")
@Data
public class IdRequestVO extends RequestVO {

    @ApiModelProperty(value = "ID，根据实际业务环境代表不同的业务数据ID", required = true)
    private Long id;
}
