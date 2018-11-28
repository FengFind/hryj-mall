package com.hryj.service.app.v1_0;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.common.BizCodeEnum;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.product.ProductCategory;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.product.category.request.app.AppProdCateSearchRequestVO;
import com.hryj.entity.vo.product.category.response.ProductCategoryItemResponseVO;
import com.hryj.exception.BizException;
import com.hryj.feign.PartyFeignClient;
import com.hryj.mapper.ProductCategoryMapper;
import com.hryj.service.prodcate.ProdCateTreeHandler;
import com.hryj.service.prodcate.ProdCateTreeItem;
import com.hryj.service.prodcate.ProductCategoryUtilService;
import com.hryj.service.util.UserCoveredByPartyUtil;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 王光银
 * @className: AppProductCategoryService
 * @description: APP端商品分类 v1.0 实现
 * @create 2018/7/9 0009 23:01
 **/
@Slf4j
@Service("v1.0-AppProductCategoryService")
public class AppProductCategoryService extends ServiceImpl<ProductCategoryMapper, ProductCategory> {

    @Autowired
    private ProductCategoryUtilService productCategoryUtilService;

    @Autowired
    private PartyFeignClient partyFeignClient;

    /**
     * @author 王光银
     * @methodName: findProdCate
     * @methodDesc:  APP端查询商品分类列表
     * @description:
     * @param: [appProdCateSearchRequestVO, covered_party_id_set]
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.product.category.response.ProductCategoryItemResponseVO>>
     * @create 2018-07-09 23:02
     **/
    @SuppressWarnings("unchecked")
    public Result<ListResponseVO<ProductCategoryItemResponseVO>> findProdCate(AppProdCateSearchRequestVO appProdCateSearchRequestVO, Set<Long> covered_party_id_set) throws BizException {
        //得到覆盖当前用户的门店和仓库
        if (UtilValidate.isEmpty(covered_party_id_set)) {
            Result<Set<Long>> covered_party_set = UserCoveredByPartyUtil.getUserCoveredParty(appProdCateSearchRequestVO.getLogin_token(), partyFeignClient);
            if (UtilValidate.isEmpty(covered_party_set.getData())) {
                Result result = new Result(CodeEnum.FAIL_BUSINESS, "没有覆盖用户的门店和仓库");
                result.setBiz_code(BizCodeEnum.NO_COVERED_PARTY.getCode());
                return result;
            }
            covered_party_id_set = covered_party_set.getData();
        }

        log.info("APP用户端商品分类搜索: 覆盖组织ID：【" + covered_party_id_set + "】, 用户:" + appProdCateSearchRequestVO.getLogin_token());

        //加载覆盖用户的门店和仓库的所有分类商品的统计
        Map<Long, Integer> statisticsMap = productCategoryUtilService.statisticsPartyProdByCate(covered_party_id_set);
        if (UtilValidate.isEmpty(statisticsMap)) {
            return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>());
        }

        ProdCateTreeHandler cacheHandler = productCategoryUtilService.getProdCateTreeHandler();
        if (cacheHandler == null
                || UtilValidate.isEmpty(cacheHandler.getTree_list())
                || UtilValidate.isEmpty(cacheHandler.getMap_data())) {
            return new Result<>(CodeEnum.SUCCESS, "没有数据");
        }

        //将统计的覆盖当前用户的门店和仓库的分类商品数据赋值到树形结构上, 将缓存的分类树结构进行JSON序列化和反序列化，以获得线程内的变量副本
        for (Long cate_id : cacheHandler.getMap_data().keySet()) {
            if (statisticsMap.containsKey(cate_id)) {
                cacheHandler.getMap_data().get(cate_id).setProd_num(statisticsMap.get(cate_id));
            }
        }

        //将树形结构分类数据的商品数据进行节点的向上统计
        productCategoryUtilService.statisticsPartyCateProdNum(cacheHandler.getTree_list());

        //搜索目标分类下的子分类
        List<ProdCateTreeItem> son_list = productCategoryUtilService.searchSonList(appProdCateSearchRequestVO.getCategory_id(), cacheHandler.getTree_list());
        if (UtilValidate.isEmpty(son_list)) {
            return new Result<>(CodeEnum.SUCCESS);
        }
        return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>(productCategoryUtilService.generateReturnVOList(son_list)));
    }

}
