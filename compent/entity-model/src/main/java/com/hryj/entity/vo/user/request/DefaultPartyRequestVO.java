package com.hryj.entity.vo.user.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 李道云
 * @className: DefaultPartyRequestVO
 * @description: 设置默认门店或仓库请求VO
 * @create 2018/8/15 15:45
 **/
@Data
@ApiModel(value="设置默认门店或仓库请求VO")
public class DefaultPartyRequestVO extends RequestVO {

    @ApiModelProperty(value = "默认门店或仓库id", required = true)
    private Long party_id;

    @ApiModelProperty(value = "部门类型", required = true)
    private String dept_type;

    @ApiModelProperty(value = "默认门店或仓库名称", required = true)
    private String party_name;

    @ApiModelProperty(value = "默认门店或仓库地址", required = true)
    private String party_address;

    @ApiModelProperty(value = "用户位置到门店的距离")
    private BigDecimal distance;
}
