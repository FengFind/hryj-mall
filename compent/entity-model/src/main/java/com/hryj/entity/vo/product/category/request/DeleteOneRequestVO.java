package com.hryj.entity.vo.product.category.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: DeleteOneRequestVO
 * @description:
 * @create 2018/7/5 0005 11:43
 **/
@ApiModel(value = "删除单条数据请求VO", description = "删除单条数据请求VO")
@Data
public class DeleteOneRequestVO extends RequestVO {

    @ApiModelProperty(value = "ID，根据具体接口的使用，代表不同业务数据的ID", required = true)
    private Long id;
}
