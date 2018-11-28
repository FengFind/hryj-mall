package com.hryj.service.prodcate;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.cache.RedisLock;
import com.hryj.cache.RedisService;
import com.hryj.entity.bo.product.PartyProduct;
import com.hryj.entity.bo.product.ProductCategory;
import com.hryj.entity.vo.product.category.response.ProductCategoryItemResponseVO;
import com.hryj.mapper.PartyProductMapper;
import com.hryj.mapper.ProductCategoryMapper;
import com.hryj.service.util.ProductCategoryUtil;
import com.hryj.service.util.ProductSearchConfigCacheUtil;
import com.hryj.utils.UtilMisc;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author 王光银
 * @className: ProductCategoryUtilService
 * @description:
 * @create 2018/8/15 0015 11:02
 **/
@Service
@Slf4j
public class ProductCategoryUtilService extends ServiceImpl<ProductCategoryMapper, ProductCategory> {

    /**
     * 只有当前线程从数据库加载了数据才能从该变量中获取到数据， 从当前线程变更获取数据以减少对缓存服务的访问和序列化，提高一定的响应
     */
    private static final ThreadLocal<ProdCateTreeHandler> PROD_CATE_TREE_CACHE_LOCAL = new ThreadLocal<>();

    @Autowired
    private PartyProductMapper partyProductMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisLock redisLock;


    public Set<Long> getLastProdCate(Long cate_id, boolean include_parent) {
        if (cate_id == null || cate_id <= 0L) {
            return null;
        }
        ProdCateTreeHandler cacheHandler = getProdCateTreeHandler();

        if (cacheHandler.getMap_data().containsKey(cate_id)) {
            return findLastNode(cacheHandler.getMap_data().get(cate_id), include_parent);
        }
        return null;
    }

    private Set<Long> findLastNode(ProdCateTreeItem tree_item, boolean include_self) {
        if (UtilValidate.isEmpty(tree_item.getSon_list())) {
            return UtilMisc.toSet(tree_item.getCategory_id());
        }
        Set<Long> set = new HashSet<>(tree_item.getSon_list().size());
        if (include_self) {
            set.add(tree_item.getCategory_id());
        }
        for (ProdCateTreeItem item : tree_item.getSon_list()) {
            if (UtilValidate.isEmpty(item.getSon_list())) {
                set.add(item.getCategory_id());
            } else {
                set.addAll(findLastNode(item, include_self));
            }
        }
        return set;
    }

    public ProdCateTreeHandler getProdCateTreeHandler() {
        //加载商品分类数据
        initProdCateTree();

        //从线程本地变量中获取，如果当前线程执行了分类数据的加载，线程变量中必定会有值
        ProdCateTreeHandler treeHandler = PROD_CATE_TREE_CACHE_LOCAL.get();
        if (treeHandler != null) {
            return treeHandler;
        }

        //从缓存中获取商品分类数据
        ProdCateTreeHandler prodCateTreeHandler = ProductCategoryUtil.getProdCateTreeHandlerFromCache();
        if (prodCateTreeHandler == null) {
            return getProdCateTreeHandler();
        }
        return prodCateTreeHandler;
    }

    public int statisticsPartyCateProdNum(List<ProdCateTreeItem> tree_list) {
        int all_prod_num = 0;
        for (ProdCateTreeItem treeItem : tree_list) {
            int this_node_num = treeItem.getProd_num();
            if (UtilValidate.isNotEmpty(treeItem.getSon_list())) {
                this_node_num += statisticsPartyCateProdNum(treeItem.getSon_list());
            }
            treeItem.setProd_num(this_node_num);
            all_prod_num += this_node_num;
        }
        return all_prod_num;
    }

    /**
     * 批量统计门店或仓库的基于商品分类的在售商品数据
     * @param party_id_set
     * @return
     */
    public Map<Long, Integer> statisticsPartyProdByCate(Set<Long> party_id_set, String...product_type_id) {

        if (UtilValidate.isEmpty(party_id_set)) {
            return new HashMap<>(0);
        }
        Map<String, Object> params_map = new HashMap<>(6);
        if (party_id_set.size() == 1) {
            params_map.put("party_id", party_id_set.iterator().next());
        } else {
            params_map.put("party_id_set", party_id_set);
        }

        if (ArrayUtil.isNotEmpty(product_type_id)) {
            if (product_type_id.length > 1) {
                params_map.put("product_type_id_list", CollUtil.toList(product_type_id));
            } else {
                params_map.put("product_type_id", product_type_id[0]);
            }
        }

        /**
         * 获取统一管理配置的商品搜索基础条件（上下架，禁售，开始销售时间，终止销售时间，库存检查等）
         */
        Map<String, Object> prod_search_config_map = ProductSearchConfigCacheUtil.generateSearchCondition();
        if (UtilValidate.isNotEmpty(prod_search_config_map)) {
            params_map.putAll(prod_search_config_map);
        }

        List<PartyProduct> partyProductList = partyProductMapper.statisticsPartyProdGroupByCate(params_map);
        Map<Long, Integer> cate_num_map = new HashMap<>(UtilValidate.isEmpty(partyProductList) ? 0 : partyProductList.size());
        if (UtilValidate.isNotEmpty(partyProductList)) {
            for (PartyProduct partyProduct : partyProductList) {
                cate_num_map.put(partyProduct.getProduct_id(), partyProduct.getInventory_quantity().intValue());
            }
        }
        return cate_num_map;
    }

    /**
     * TODO WGY ：统计 门店下商品按照商品类型后，各分类下的商品数据，此处应该对数据做缓存，暂时未实现
     * @param party_id
     * @param product_type_id
     * @return
     */
    public Map<Long, Integer> statisticsPartyProdByCate(Long party_id, String...product_type_id) {
        if (party_id == null || party_id <= 0L) {
            return new HashMap<>(0);
        }
        Map<String, Object> params_map = UtilMisc.toMap("party_id", party_id);

        if (ArrayUtil.isNotEmpty(product_type_id)) {
            if (product_type_id.length > 1) {
                params_map.put("product_type_id_list", CollUtil.toList(product_type_id));
            } else {
                params_map.put("product_type_id", product_type_id[0]);
            }
        }

        /**
         * 获取统一管理配置的商品搜索基础条件（上下架，禁售，开始销售时间，终止销售时间，库存检查等）
         */
        Map<String, Object> prod_search_config_map = ProductSearchConfigCacheUtil.generateSearchCondition();
        if (UtilValidate.isNotEmpty(prod_search_config_map)) {
            params_map.putAll(prod_search_config_map);
        }

        List<PartyProduct> partyProductList = partyProductMapper.statisticsPartyProdGroupByCate(params_map);

        Map<Long, Integer> cate_num_map = new HashMap<>(UtilValidate.isEmpty(partyProductList) ? 0 : partyProductList.size());

        if (UtilValidate.isNotEmpty(partyProductList)) {
            for (PartyProduct partyProduct : partyProductList) {
                cate_num_map.put(partyProduct.getProduct_id(), partyProduct.getInventory_quantity().intValue());
            }
        }
        return cate_num_map;
    }

    public List<ProductCategoryItemResponseVO> generateReturnVOList(List<ProdCateTreeItem> data_list) {
        List<ProductCategoryItemResponseVO> return_vo_list = new ArrayList<>(data_list.size());

        for (ProdCateTreeItem treeItem : data_list) {
            if (treeItem.getProd_num() > 0) {
                return_vo_list.add(treeItem.convertTo());
            }
        }
        return return_vo_list;
    }

    public List<ProdCateTreeItem> searchSonList(Long parent_id, List<ProdCateTreeItem> cate_data_tree_list) {
        if (UtilValidate.isEmpty(cate_data_tree_list)) {
            return null;
        }
        if (!UtilValidate.idIsValid(parent_id)) {
            //没有搜索目标分类时，返回最顶级分类
            List<ProdCateTreeItem> top_cate_list = new ArrayList<>(cate_data_tree_list.size());
            for (ProdCateTreeItem treeItem : cate_data_tree_list) {
                if (treeItem.getProd_num() > 0) {
                    top_cate_list.add(treeItem);
                }
            }
            return top_cate_list;
        }
        for (ProdCateTreeItem treeItem : cate_data_tree_list) {
            if (treeItem.getCategory_id().equals(parent_id)) {
                //如果搜索的分类下没有子分类，返回这个搜索目标分类
                if (UtilValidate.isEmpty(treeItem.getSon_list())) {
                    if (treeItem.getProd_num() > 0) {
                        return UtilMisc.toList(treeItem);
                    }
                }

                List<ProdCateTreeItem> to_return_list = new ArrayList<>(treeItem.getSon_list().size());
                for (ProdCateTreeItem sonItem : treeItem.getSon_list()) {
                    if (sonItem.getProd_num() > 0) {
                        to_return_list.add(sonItem);
                    }
                }

                //如果子分类中没有商品，但是父级分类中有商品，返回父级分类
                if (UtilValidate.isEmpty(to_return_list) && treeItem.getProd_num() > 0) {
                    to_return_list.add(treeItem);
                }
                return to_return_list;
            }
        }

        /// 如果一级分类中没有搜索目标分类，则对分类树进行递归搜索
        for (ProdCateTreeItem treeItem : cate_data_tree_list) {
            if (UtilValidate.isNotEmpty(treeItem.getSon_list())) {
                return searchSonList(parent_id, treeItem.getSon_list());
            }
        }
        //递归搜索仍然没有结果，返回空
        return null;
    }

    private void initProdCateTree() {
        //只有需要重新加载时才执行数据库加载操作
        if (ProductCategoryUtil.needToReload()) {
            try {
                //从数据加载时加上锁，保证加载过程只会执行一次
                boolean lockResult = redisLock.lock(ProductCategoryUtil.PROD_CATE_CACHE_GROUP_NAME, ProductCategoryUtil.PROD_CATE_CACHE_KEY, 5);
                while (!lockResult) {
                    try {
                        Thread.sleep(50);
                    } catch (Exception e) {}
                    lockResult = redisLock.lock(ProductCategoryUtil.PROD_CATE_CACHE_GROUP_NAME, ProductCategoryUtil.PROD_CATE_CACHE_KEY, 5);
                }

                if (!ProductCategoryUtil.needToReload()) {
                    return;
                }

                //加载商品分类处理为树形结构数据
                loadProdCateTree();

                ProductCategoryUtil.notifyNotNeedToReload();
            } catch (Exception e) {
                log.error("加载商品分类数据失败", e);
            } finally {
                redisLock.unLock(ProductCategoryUtil.PROD_CATE_CACHE_GROUP_NAME, ProductCategoryUtil.PROD_CATE_CACHE_KEY);
            }
        }
    }

    private void loadProdCateTree() {
        ProdCateTreeHandler treeHandler = new ProdCateTreeHandler();

        //加载所有商品分类数据，转换成满足树形结构的数据对象
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.setSqlSelect("id", "category_name", "category_pid", "category_url");
        wrapper.orderBy("sort_num", false);
        @SuppressWarnings("unchecked")
        List<ProductCategory> prod_cate_list = super.selectList(wrapper);
        if (UtilValidate.isEmpty(prod_cate_list)) {
            treeHandler.setHas_no_data(true);
        }
        List<ProdCateTreeItem> treeDataList = new LinkedList<>();

        Map<Long, ProdCateTreeItem> map = new LinkedHashMap<>();

        if (UtilValidate.isNotEmpty(prod_cate_list)) {
            for (ProductCategory category : prod_cate_list) {
                ProdCateTreeItem item = new ProdCateTreeItem();
                treeDataList.add(item);

                item.setCategory_id(category.getId());
                item.setCategory_name(category.getCategory_name());
                item.setImage_url(category.getCategory_url());
                item.setParent_id(category.getCategory_pid());
                map.put(item.getCategory_id(), item);
            }

            //处理为树形结构数据结构
            Iterator<ProdCateTreeItem> it = treeDataList.iterator();
            while (it.hasNext()) {
                ProdCateTreeItem item = it.next();
                if (item.getParent_id() != null && item.getParent_id() > 0L) {
                    ProdCateTreeItem parent = map.get(item.getParent_id());
                    if (parent == null) {
                        continue;
                    }
                    if (UtilValidate.isNotEmpty(parent.getSon_list())) {
                        parent.getSon_list().add(item);
                    } else {
                        parent.setSon_list(UtilMisc.toList(item));
                    }
                    it.remove();
                }
            }

            treeHandler.setTree_list(treeDataList);
            treeHandler.setMap_data(map);
            treeHandler.setHas_no_data(false);
        }

        PROD_CATE_TREE_CACHE_LOCAL.set(treeHandler);
        ProductCategoryUtil.cacheProdCateData(treeHandler);
    }
}
