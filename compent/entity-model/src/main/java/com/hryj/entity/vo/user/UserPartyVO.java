package com.hryj.entity.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author 李道云
 * @className: UserPartyVO
 * @description: 服务于用户的门店或者仓库
 * @create 2018/7/2 17:40
 **/
@Data
@ApiModel(value="服务于用户的门店或者仓库VO")
public class UserPartyVO implements Serializable {

    private static final long serialVersionUID = 2267841769765066754L;

    /**
     * 门店或仓库id
     */
    @ApiModelProperty(value = "门店或仓库id", required = true)
    private Long party_id;
    /**
     * 部门类型
     */
    @ApiModelProperty(value = "部门类型", required = true)
    private String dept_type;
    /**
     * 门店或仓库名称
     */
    @ApiModelProperty(value = "门店或仓库名称", required = true)
    private String party_name;
    /**
     * 门店或仓库地址
     */
    @ApiModelProperty(value = "门店或仓库地址", required = true)
    private String party_address;
    /**
     * 用户位置到门店的距离
     */
    @ApiModelProperty(value = "用户位置到门店的距离")
    private BigDecimal distance;
}
