package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hryj.entity.bo.product.PartyProduct;
import com.hryj.entity.bo.product.ProductInfo;
import com.hryj.entity.bo.promotion.ActivityInfo;
import com.hryj.entity.bo.promotion.ActivityScopeItem;
import com.hryj.entity.vo.inventory.mapping.InventoryLockMappingItem;
import com.hryj.entity.vo.product.partyprod.mapping.PartyProductMappingRow;
import com.hryj.entity.vo.product.partyprod.response.PartyProductStatisticsItem;
import com.hryj.entity.vo.product.response.ProductTopSalesItemResponseVO;
import com.hryj.entity.vo.product.response.app.AppProdListItemResponseVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author 王光银
 * @className: PartyProductMapper
 * @description:
 * @create 2018/7/4 0004 14:16
 **/
@Component
public interface PartyProductMapper extends BaseMapper<PartyProduct> {


    /**
     * @author 王光银
     * @methodName:  selectPartyIntersectionProduct
     * @methodDesc: 分页查询多个门店或仓库的交集商品
     * @description:
     * @param: [params_map, page_condition]
     * @return
     * @create 2018-07-06 18:56
     **/
    List<ProductInfo> selectPartyIntersectionProduct(Map<String, Object> params_map, Page page_condition);

    /**
     * @author 王光银
     * @methodName: selectPartyProduct
     * @methodDesc: 分页查询门店或仓库的商品
     * @description:
     * @param:
     * @return
     * @create 2018-07-09 9:19
     **/
    List<PartyProductMappingRow> selectPartyProduct(Map<String, Object> params_map, Page page_condition);


    /**
     * @author 王光银
     * @methodName: selectWarehouseSelectableProduct
     * @methodDesc: 分页查询仓库的可选择销售商品
     * @description:
     * @param:
     * @return
     * @create 2018-07-09 10:29
     **/
    List<PartyProductMappingRow> selectWarehouseSelectableProduct(Map<String, Object> params_map, Page page_condition);


    /**
     * @author 王光银
     * @methodName: selectStoreSelectableProduct
     * @methodDesc: 查询门店的可选择销售 商品
     * @description:
     * @param:
     * @return
     * @create 2018-07-09 10:30
     **/
    List<PartyProductMappingRow> selectStoreSelectableProduct(Map<String, Object> params_map, Page page_condition);

    /**
     * @author 王光银
     * @methodName: selectPartyProdCount
     * @methodDesc: 统计返回当事组织的商品数量
     * @description:
     * @param:
     * @return
     * @create 2018-07-11 23:01
     **/
    List<PartyProductStatisticsItem> selectPartyProdCount(Map<String, Object> params_map);

    /**
     * @author 王光银
     * @methodName: selectOnePartyActivityProd
     * @methodDesc: 查询一个当事组织的活动商品
     * @description:
     * @param:
     * @return
     * @create 2018-07-13 10:36
     **/
    List<AppProdListItemResponseVO> selectOnePartyActivityProd(Map<String, Object> params_map);

    /**
     * @author 王光银
     * @methodName: selectManyPartyProd
     * @methodDesc: 查询一组当事组织的活动商品
     * @description:
     * @param:
     * @return
     * @create 2018-07-13 10:36
     **/
    List<AppProdListItemResponseVO> selectManyPartyProd(Map<String, Object> params_map);

    /**
     * @author 王光银
     * @methodName: statisticsPartyProdGroupByCate
     * @methodDesc: 统计指定当事组织范围的商品，按分类进行统计
     * @description:
     * @param: 
     * @return 
     * @create 2018-07-17 9:23
     **/
    List<PartyProduct> statisticsPartyProdGroupByCate(Map<String, Object> params_map);

    /**
     * @author 王光银
     * @methodName:
     * @methodDesc:
     * @description:
     * @param:
     * @return
     * @create 2018-08-16 11:05
     **/
    List<PartyProduct> statisticsPartyProd(Map<String, Object> params_map);


    /**
     * @author 王光银
     * @methodName: updateStoreProdSalesPrice
     * @methodDesc: 修改门店商品的价格
     * @description:
     * @param:
     * @return
     * @create 2018-07-25 20:53
     **/
    void updateStoreProdSalesPrice(Map<String, Object> params_map);

    /**
     * @author 王光银
     * @methodName: findPartyTopSalesByOrderFinished
     * @methodDesc: 查询当事组织的已完成订单中的TOP销量商品(从订单数据中实际统计查询)
     * @description:
     * @param:
     * @return
     * @create 2018-08-02 15:06
     **/
    List<ProductTopSalesItemResponseVO> findPartyTopSalesByOrderFinished(Map<String, Object> params_map);

    /**
     * @author 王光银
     * @methodName: findPartyTopSalesByStatisticsData
     * @methodDesc: 查询当事组织的已完成订单中的TOP销量商品(从每日统计数据中查询)
     * @description:
     * @param:
     * @return
     * @create 2018-08-02 17:27
     **/
    List<ProductTopSalesItemResponseVO> findPartyTopSalesByStatisticsData(Map<String, Object> params_map);

    /**
     * @author 王光银
     * @methodName: pageFindPartyActivityAndNormalProduct
     * @methodDesc: 分布查询一个门店的活动商品与普通商品
     * @description: 结果是活动商品在前，普通商品在后，活动商品按照活动商品序号排序， 普通商品按照销量排序
     * @param:
     * @return
     * @create 2018-08-17 9:20
     **/
    List<AppProdListItemResponseVO> pageFindPartyActivityAndNormalProduct(Map<String, Object> params_map, Page page_condition);

    /**
     * @author 王光银
     * @methodName: inventoryLockQuery
     * @methodDesc: 商品库存锁定查询
     * @description:
     * @param:
     * @return
     * @create 2018-08-21 9:06
     **/
    List<InventoryLockMappingItem> inventoryLockQuery(@Param("params_list") List<Map<String, Object>> params_list);
    
    /**
     * @author 王光银
     * @methodName:
     * @methodDesc: TODO WGY 查询门店即将开始的最早的活动开始日期
     * @description:
     * @param:
     * @return 
     * @create 2018-08-29 10:53
     **/
    List<ActivityInfo> findPartyEarliestStartActivityDate(@Param("party_id") Long party_id, @Param("curr") Date curr);

    /**
     * TODO WGY 查询指定门店或所有门店的进行中的，即将开始的活动日期
     * @param party_id
     * @param curr
     * @return
     */
    List<ActivityScopeItem> findPartyActivityDate(@Param("party_id") Long party_id, @Param("curr") Date curr);

    /**
     * 查询门店商品的商品类型
     * @param party_product_id
     * @return
     */
    String findPartyProductType(@Param("party_product_id") Long party_product_id);
}
