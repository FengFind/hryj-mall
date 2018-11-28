package com.hryj.entity.vo.product.partyprod.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: PartySaleableProdPoolAppendRequestVO
 * @description:
 * @create 2018/6/27 0027 17:45
 **/
@ApiModel(value = "添加门店或仓库可销售商品请求VO", description = "添加门店或仓库可销售商品请求VO")
@Data
public class PartySaleableProdPoolAppendRequestVO extends RequestVO {

    @ApiModelProperty(value = "门店或仓库ID", required = true)
    private Long party_id;

    @ApiModelProperty(value = "当事组织类型 store门店，warehouse仓库")
    private String party_type;

    @ApiModelProperty(value = "添加到销售库的商品数据集合", required = true)
    private List<PartyProductItem> party_prod_list;

    @ApiModelProperty(value = "商品重复添加时的处理策略， 1返回错误，其他任意值直接忽略, 缺省1")
    private Integer when_prod_duplicate = 1;

    @ApiModelProperty(value = "商品下架时的处理策略， 1返回错误，其他任意值直接忽略, 缺省1")
    private Integer when_prod_down = 1;

}
