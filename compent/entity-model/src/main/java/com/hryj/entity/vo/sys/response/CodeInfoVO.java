package com.hryj.entity.vo.sys.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 李道云
 * @className: CodeInfoVO
 * @description:
 * @create 2018/7/9 18:12
 **/
@Data
@ApiModel(value="字典表信息VO")
public class CodeInfoVO implements Serializable {

    @ApiModelProperty(value = "code_key", required = true)
    private String code_key;

    @ApiModelProperty(value = "code_value", required = true)
    private String code_value;

    @ApiModelProperty(value = "code_name", required = true)
    private String code_name;
}
