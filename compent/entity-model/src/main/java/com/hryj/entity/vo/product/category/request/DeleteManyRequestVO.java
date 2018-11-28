package com.hryj.entity.vo.product.category.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: DeleteManyRequestVO
 * @description: 删除多条数据请求VO
 * @create 2018/7/5 0005 11:44
 **/
@ApiModel(value = "删除多条数据请求VO", description = "删除多条数据请求VO")
@Data
public class DeleteManyRequestVO extends RequestVO {

    @ApiModelProperty(value = "属性ID集合，数据含义根据具体的接口代表不同的业务数据ID", required = true)
    private List<Long> id_list;
}
