package com.hryj.entity.vo.promotion.recommend.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

/**
 * @author 王光银
 * @className: CopyRecommendProdToOtherPartyRequestVO
 * @description:
 * @create 2018/6/27 0027 21:28
 **/
@ApiModel(value = "推荐商品复制请求VO", description = "推荐商品复制请求VO")
@Data
public class CopyRecommendProdToOtherPartyRequestVO extends RequestVO {

    @ApiModelProperty(value = "复制哪个门店或仓库的推荐商品数据，这就是那个门店或仓库的ID", required = true)
    private Long party_id_from;

    @ApiModelProperty(value = "复制给哪些门店或仓库，这就是那些门店或仓库的ID集合", required = true)
    private List<Long> party_id_to;
}
