package com.hryj.service.util;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.product.PartyProduct;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.product.common.mapping.ProductMappingItem;
import com.hryj.entity.vo.product.common.request.ProductValidateItem;
import com.hryj.entity.vo.product.common.request.ProductsValidateRequestVO;
import com.hryj.entity.vo.promotion.activity.request.ActivityIdProductIdRequestVO;
import com.hryj.entity.vo.promotion.activity.response.ActivityInProgressProductItemResponseVO;
import com.hryj.feign.PromotionFeignClient;
import com.hryj.mapper.PartyProductMapper;
import com.hryj.mapper.ProductMapper;
import com.hryj.utils.UtilMisc;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author 王光银
 * @className: ProductValidateUtil
 * @description:
 * @create 2018/7/20 0020 9:16
 **/
@Slf4j
public class ProductValidateUtil {

    public static Result<IdsHandler> productsValidateRequestParamsValidate(ProductsValidateRequestVO productsValidateRequestVO) {
        if (productsValidateRequestVO == null || UtilValidate.isEmpty(productsValidateRequestVO.getProd_summary_list())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "验证商品条目集合不能是空值");
        }
        if (productsValidateRequestVO.getMerge_validate_inventory() == null) {
            productsValidateRequestVO.setMerge_validate_inventory(true);
        }
        if (productsValidateRequestVO.getReturn_promotion_info() == null) {
            productsValidateRequestVO.setReturn_promotion_info(false);
        }
        if (productsValidateRequestVO.getReturn_tax_rate() == null) {
            productsValidateRequestVO.setReturn_tax_rate(false);
        }
        int i = 0;
        IdsHandler idsHandler = new IdsHandler();
        idsHandler.id_set_one = new HashSet<>(productsValidateRequestVO.getProd_summary_list().size());
        idsHandler.id_set_two = new HashSet<>(productsValidateRequestVO.getProd_summary_list().size());
        idsHandler.id_set_three = new HashSet<>(productsValidateRequestVO.getProd_summary_list().size());
        for (ProductValidateItem validateItem : productsValidateRequestVO.getProd_summary_list()) {
            if (validateItem.getParty_id() == null || validateItem.getParty_id() <= 0L) {
                return new Result(CodeEnum.FAIL_PARAMCHECK, "第[" + (i + 1) +"]个商品的当事组织ID不能是空值");
            }
            if (validateItem.getProduct_id() == null || validateItem.getProduct_id() <= 0L) {
                return new Result(CodeEnum.FAIL_PARAMCHECK, "第[" + (i + 1) + "]个商品的商品ID不能是空值");
            }
            if (validateItem.getRequired_min_inventory_quantity() == null || validateItem.getRequired_min_inventory_quantity().intValue() < 0) {
                validateItem.setRequired_min_inventory_quantity(0);
            }
            idsHandler.id_set_one.add(validateItem.getParty_id());
            idsHandler.id_set_two.add(validateItem.getProduct_id());
            if (validateItem.getActivity_id() != null && validateItem.getActivity_id().longValue() > 0L) {
                idsHandler.id_set_three.add(validateItem.getActivity_id());
            }
            i ++;
        }
        return new Result<>(CodeEnum.SUCCESS, idsHandler);
    }

    public static Result<Map<Long, ProductMappingItem>> loadValidateProducts(Set<Long> prod_id_set, ProductMapper productMapper) {
        try {
            List<ProductMappingItem> prod_list = productMapper.selectProductMappingList(prod_id_set);
            if (UtilValidate.isNotEmpty(prod_list)) {
                Map<Long, ProductMappingItem> map = new HashMap<>(prod_list.size());
                for (ProductMappingItem info : prod_list) {
                    map.put(info.getProduct_id(), info);
                }
                return new Result<>(CodeEnum.SUCCESS, map);
            }
            return new Result<>(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.error("加载商品数据失败", e);
            return new Result<>(CodeEnum.FAIL_BUSINESS, "加载商品数据失败:" + e.getMessage());
        }
    }

    public static Result<Map<Long, Map<Long, PartyProduct>>> loadValidatePartyProducts(List<ProductValidateItem> prod_summary_list, PartyProductMapper partyProductMapper) {
        Map<Long, Set<Long>> map = new HashMap<>(prod_summary_list.size());
        for (ProductValidateItem item : prod_summary_list) {
            Long party_id = item.getParty_id();
            if (map.containsKey(party_id)) {
                map.get(party_id).add(item.getProduct_id());
            } else {
                map.put(party_id, UtilMisc.toSet(item.getProduct_id()));
            }
        }

        Map<Long, Map<Long, PartyProduct>> big_map = new HashMap<>(map.size());

        Iterator<Long> it = map.keySet().iterator();
        while (it.hasNext()) {
            Long party_id = it.next();
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.eq("party_id", party_id).and().in("product_id", map.get(party_id));
            wrapper.setSqlSelect("introduction_date", "sales_end_date", "up_down_status", "inventory_quantity", "party_id", "product_id", "sale_price");
            try {
                List<PartyProduct> partyProductList = partyProductMapper.selectList(wrapper);
                if (UtilValidate.isNotEmpty(partyProductList)) {
                    Map<Long, PartyProduct> small_map = new HashMap<>(partyProductList.size());
                    for (PartyProduct partyProduct : partyProductList) {
                        small_map.put(partyProduct.getProduct_id(), partyProduct);
                    }
                    big_map.put(party_id, small_map);
                }
            } catch (Exception e) {
                log.error("加载当事组织的商品数据失败", e);
                return new Result<>(CodeEnum.FAIL_BUSINESS, "加载当事组织的商品数据失败");
            }
        }
        return new Result<>(CodeEnum.SUCCESS, big_map);
    }

    public static Result<Map<Long, Map<Long, ActivityInProgressProductItemResponseVO>>> loadActivityProdValidate(List<ProductValidateItem> prod_summary_list,
                                                                                                        PromotionFeignClient promotionFeignClient) {
        List<ActivityIdProductIdRequestVO> list = new ArrayList<>(prod_summary_list.size());

        for (ProductValidateItem item : prod_summary_list) {
            if (item.getActivity_id() != null && item.getActivity_id() > 0L) {
                ActivityIdProductIdRequestVO activityIdProductIdRequestVO = new ActivityIdProductIdRequestVO();
                activityIdProductIdRequestVO.setActivity_id(item.getActivity_id());
                activityIdProductIdRequestVO.setProduct_id(item.getProduct_id());
                list.add(activityIdProductIdRequestVO);
            }
        }

        if (UtilValidate.isEmpty(list)) {
            return new Result<>(CodeEnum.SUCCESS);
        }

        try {
            Result<ListResponseVO<ActivityInProgressProductItemResponseVO>> promotion_check_result = promotionFeignClient.checkPromotionProd(list);
            if (promotion_check_result.isFailed()
                    || promotion_check_result.getData() == null
                    || UtilValidate.isEmpty(promotion_check_result.getData().getRecords())) {
                log.error("商品验证公共接口-调用接口获取商品促销活动信息失败:" + promotion_check_result.getCode() + " : " + promotion_check_result.getMsg());
                return new Result<>(CodeEnum.FAIL_SERVER, "获取商品活动信息失败");
            }
            Map<Long, Map<Long, ActivityInProgressProductItemResponseVO>> return_map = new HashMap<>(promotion_check_result.getData().getRecords().size());
            for (ActivityInProgressProductItemResponseVO activity : promotion_check_result.getData().getRecords()) {
                if (activity.getActivity_id() == null || activity.getActivity_id() <= 0L) {
                    log.error("活动商品验证未返回活动ID");
                    continue;
                }
                if (activity.getProduct_id() == null || activity.getProduct_id() <= 0L) {
                    log.error("活动商品验证未返回商品ID");
                    continue;
                }
                if (return_map.containsKey(activity.getActivity_id())) {
                    return_map.get(activity.getActivity_id()).put(activity.getProduct_id(), activity);
                } else {
                    Map<Long, ActivityInProgressProductItemResponseVO> map = new HashMap<>(promotion_check_result.getData().getRecords().size());
                    map.put(activity.getProduct_id(), activity);
                    return_map.put(activity.getActivity_id(), map);
                }
            }
            return new Result<>(CodeEnum.SUCCESS, return_map);
        } catch (Exception e) {
            //调用接口异常时只记录日志，其他数据正常返回
            log.error("商品验证公共接口-调用接口获取商品促销活动信息异常", e);
            return new Result<>(CodeEnum.FAIL_SERVER, "获取商品活动信息失败");
        }
    }

    public static Result<List<ActivityInProgressProductItemResponseVO>> getProductActivityInfo(List<ActivityIdProductIdRequestVO> list, PromotionFeignClient promotionFeignClient) {
        try {
            Result<ListResponseVO<ActivityInProgressProductItemResponseVO>> promotion_check_result = promotionFeignClient.checkPromotionProd(list);
            if (promotion_check_result.isFailed()
                    || promotion_check_result.getData() == null
                    || UtilValidate.isEmpty(promotion_check_result.getData().getRecords())) {
                log.error("商品验证公共接口-调用接口获取商品促销活动信息失败:" + promotion_check_result.getCode() + " : " + promotion_check_result.getMsg());
                return new Result<>(CodeEnum.FAIL_SERVER, "获取商品活动信息失败");
            }
            return new Result<>(CodeEnum.SUCCESS, promotion_check_result.getData().getRecords());
        } catch (Exception e) {
            //调用接口异常时只记录日志，其他数据正常返回
            log.error("商品验证公共接口-调用接口获取商品促销活动信息异常", e);
            return new Result<>(CodeEnum.FAIL_SERVER, "获取商品活动信息失败");
        }
    }
}
