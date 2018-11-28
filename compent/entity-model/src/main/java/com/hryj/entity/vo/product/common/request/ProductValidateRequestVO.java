package com.hryj.entity.vo.product.common.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: ProductValidateRequestVO
 * @description: 商品验证请求VO
 * @create 2018/7/5 0005 10:34
 **/
@ApiModel(value = "商品验证请求VO", description = "商品验证请求VO")
@Data
public class ProductValidateRequestVO extends RequestVO {

    @ApiModelProperty(value = "商品ID", required = true)
    private Long product_id;

    @ApiModelProperty(value = "门店或仓库ID", required = true)
    private Long party_id;

    @ApiModelProperty(value = "活动ID")
    private Long activity_id;

    @ApiModelProperty(value = "用户ID， 门店端需要传此参数， 用户端请忽略此参数")
    private Long user_id;

}
