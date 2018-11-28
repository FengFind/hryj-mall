package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hryj.entity.bo.product.ProductInfo;
import com.hryj.entity.vo.product.common.mapping.ProductMappingItem;
import com.hryj.entity.vo.product.response.ProductTopSalesItemResponseVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author 王光银
 * @className: ProductMapper
 * @description:
 * @create 2018/6/30 0030 15:07
 **/
@Component
public interface ProductMapper extends BaseMapper<ProductInfo> {

    List<ProductInfo> pageFindProduct(Map<String, Object> params_map, Page page_condition);

    /**
     * @author 王光银
     * @methodName:  selectPartyTopSalesProduct
     * @methodDesc: 查询一个当事组织的的TOP10销量商品
     * @description:
     * @param: 
     * @return 
     * @create 2018-07-09 20:00
     **/
    List<ProductTopSalesItemResponseVO> selectPartyTopSalesProduct(Map<String, Object> params_map);

    /**
     * @author 王光银
     * @methodName: 查询商品销量TOP 10
     * @methodDesc:
     * @description:
     * @param: 
     * @return 
     * @create 2018-07-09 20:06
     **/
    List<ProductTopSalesItemResponseVO> selectTopSalesProduct(Map<String, Object> params_map);

    /**
     * @author 王光银
     * @methodName: updateProdCateName
     * @methodDesc: 修改商品分类名称
     * @description:
     * @param:
     * @return
     * @create 2018-07-25 19:53
     **/
    void updateProdCateName(Map<String, Object> params_map);


    /**
     * 查询商品与跨境的映射数据
     * @param prod_id_set
     * @return
     */
    List<ProductMappingItem> selectProductMappingList(@Param("prod_id_set") Collection<Long> prod_id_set);
}
