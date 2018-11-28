package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.product.recommend.PartyRecommendProduct;
import com.hryj.entity.vo.product.partyprod.mapping.PartyRecommendProductMappingRow;
import com.hryj.entity.vo.product.response.app.AppProdListItemResponseVO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author 王光银
 * @className: PartyProductRecommendMapper
 * @description:
 * @create 2018/7/9 0009 11:17
 **/
@Component
public interface PartyRecommendProductMapper extends BaseMapper<PartyRecommendProduct> {

    /**
     * @author 王光银
     * @methodName: selectPartyRecommendProduct
     * @methodDesc: 查询一个当事组织（门店或仓库）的推荐位商品
     * @description:
     * @param:
     * @return
     * @create 2018-07-09 15:25
     **/
    List<PartyRecommendProductMappingRow> selectPartyRecommendProduct(Long party_id);


    /**
     * @author 王光银
     * @methodName: selectMangPartyRecommendProd
     * @methodDesc: 查询多个当事组织的推荐位商品
     * @description:
     * @param:
     * @return
     * @create 2018-07-10 14:19
     **/
    List<AppProdListItemResponseVO> selectManyPartyRecommendProd(Map<String, Object> params_map);

    /**
     * @author 王光银
     * @methodName: selectAvailablePartyRecommendProd
     * @methodDesc: 查询当事组织的有效的推荐位商品
     * @description:
     * @param:
     * @return
     * @create 2018-07-18 22:36
     **/
    List<PartyRecommendProduct> selectAvailablePartyRecommendProd(Map<String, Object> params_map);
}
