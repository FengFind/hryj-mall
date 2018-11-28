package com.hryj.service.app.v1_1;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.constant.CommonConstantPool;
import com.hryj.entity.bo.product.ProductCategory;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.product.category.request.app.AppProdCateSearchRequestVO;
import com.hryj.entity.vo.product.category.response.ProductCategoryItemResponseVO;
import com.hryj.exception.BizException;
import com.hryj.feign.UserFeignClient;
import com.hryj.mapper.ProductCategoryMapper;
import com.hryj.service.PartyProductUtilService;
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

/**
 * @author 王光银
 * @className: AppProductCategoryService
 * @description: APP端商品分类 v1.1 实现
 * @create 2018/7/9 0009 23:01
 **/
@Slf4j
@Service("v1.1-AppProductCategoryService")
public class AppProductCategoryService extends ServiceImpl<ProductCategoryMapper, ProductCategory> {

    @Autowired
    private ProductCategoryUtilService productCategoryUtilService;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private PartyProductUtilService partyProductUtilService;

    /**
     * @author 王光银
     * @methodName: findProdCate
     * @methodDesc:  APP端搜索商品分类
     * @description:
     * @param: [requestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.product.category.response.ProductCategoryItemResponseVO>>
     * @create 2018-08-15 14:57
     **/
    @SuppressWarnings("unchecked")
    public Result<ListResponseVO<ProductCategoryItemResponseVO>> findProdCate(AppProdCateSearchRequestVO requestVO) throws BizException {
        /**
         * 1、计算得到本次请求应该关联的门店
         * 2、加载得到所有的商品分类
         * 3、计算得到目标门店的在售商品基于分类的统计数据
         * 4、根据 2 3 步骤的结果 计算出应该返回的商品分类数据
         */

        Long target_party_id = requestVO.getParty_id();

        /**
         * 如果没有指定门店，则获取当前登陆用户的默认门店作为目标门店
         */
        if (target_party_id == null || target_party_id <= 0L) {
            //步骤1
            Result<Long> result = UserCoveredByPartyUtil.getCoveredUserUniqueParty(requestVO.getUser_id(), requestVO.getLogin_token(), userFeignClient, partyProductUtilService);
            if (result.isFailed()) {
                log.error("商品分类搜索接口 - 获取用户默认门店失败:" + result.getMsg());
                return new Result<>(CodeEnum.FAIL_BUSINESS, "您的周围暂无小店");
            }
            if (result.getData() == null || result.getData() <= 0L) {
                return new Result<>(CodeEnum.FAIL_BUSINESS, "您的周围暂无小店");
            }
            target_party_id = result.getData();
            requestVO.setParty_id(target_party_id);
        }

        //步骤2
        ProdCateTreeHandler cacheHandler = productCategoryUtilService.getProdCateTreeHandler();
        if (cacheHandler.isHas_no_data()) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "暂无分类");
        }

        /**
         * 步骤3
         * 此步骤的处理是 V1.2  与 V1.1接口上的唯一区别
         */
        String product_type_id = CommonConstantPool.STR_ALL.equals(requestVO.getProduct_type_id()) ? null : requestVO.getProduct_type_id();

        Map<Long, Integer> statisticsMap = UtilValidate.isEmpty(product_type_id)
                ? productCategoryUtilService.statisticsPartyProdByCate(target_party_id)
                : productCategoryUtilService.statisticsPartyProdByCate(target_party_id, product_type_id);

        if (UtilValidate.isEmpty(statisticsMap)) {
            return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>());
        }

        //步骤4
        for (Long cate_id : cacheHandler.getMap_data().keySet()) {
            if (statisticsMap.containsKey(cate_id)) {
                cacheHandler.getMap_data().get(cate_id).setProd_num(statisticsMap.get(cate_id));
            }
        }

        //将树形结构分类数据的商品数据进行节点的向上统计
        productCategoryUtilService.statisticsPartyCateProdNum(cacheHandler.getTree_list());

        //搜索目标分类下的子分类
        List<ProdCateTreeItem> son_list = productCategoryUtilService.searchSonList(requestVO.getCategory_id(), cacheHandler.getTree_list());
        if (UtilValidate.isEmpty(son_list)) {
            return new Result<>(CodeEnum.SUCCESS);
        }

        return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>(productCategoryUtilService.generateReturnVOList(son_list)));
    }


}
