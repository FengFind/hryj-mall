package com.hryj.entity.vo.sys.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: CodeListRequestVO
 * @description: 字典表列表查询请求VO
 * @create 2018/7/9 18:10
 **/
@Data
@ApiModel(value="字典表列表查询请求VO")
public class CodeListRequestVO extends RequestVO {

    @ApiModelProperty(value = "字典类别", required = true)
    private String code_type;
}
