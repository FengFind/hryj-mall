package com.hryj.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.cache.LoginCache;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.product.PartyProduct;
import com.hryj.entity.bo.product.ProductInfo;
import com.hryj.entity.bo.product.recommend.PartyRecommendProduct;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.product.partyprod.mapping.PartyRecommendProductMappingRow;
import com.hryj.entity.vo.product.partyprod.request.IdRequestVO;
import com.hryj.entity.vo.product.partyprod.request.PartyIdRequestVO;
import com.hryj.entity.vo.promotion.recommend.request.CopyRecommendProdToOtherPartyRequestVO;
import com.hryj.entity.vo.promotion.recommend.request.PartyRecommendProdAppendRequestVO;
import com.hryj.entity.vo.promotion.recommend.request.PartyRecommendProdItem;
import com.hryj.entity.vo.promotion.recommend.response.PartyRecommendProductItemResponseVO;
import com.hryj.entity.vo.staff.user.StaffAdminLoginVO;
import com.hryj.exception.BizException;
import com.hryj.exception.ServerException;
import com.hryj.mapper.PartyProductMapper;
import com.hryj.mapper.PartyRecommendProductMapper;
import com.hryj.mapper.ProductMapper;
import com.hryj.service.util.ProductUtil;
import com.hryj.service.util.RedisCacheUtil;
import com.hryj.utils.UtilMisc;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author 王光银
 * @className: PartyRecommendProductService
 * @description:
 * @create 2018/7/9 0009 11:16
 **/
@Slf4j
@Service
public class PartyRecommendProductService extends ServiceImpl<PartyRecommendProductMapper, PartyRecommendProduct> {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private PartyProductMapper partyProductMapper;

    @Autowired
    private ProductService productService;

    /**
     * @author 王光银
     * @methodName: findPartyRecommendProductList
     * @methodDesc: 查询返回指定门店或仓库的推荐商品
     * @description: 每个门店的推荐商品最多为5个，该查询不需要进行分页, 返回数据按是否置顶以及更新时间，创建时间进行排序
     * @param: [partyIdRequestVO]
     * @return com.hryj.common.Result<java.util.List<com.hryj.entity.vo.promotion.recommend.response.PartyRecommendProductItemResponseVO>>
     * @create 2018-06-28 16:06
     **/
    public Result<ListResponseVO<PartyRecommendProductItemResponseVO>> findPartyRecommendProductList(
            PartyIdRequestVO partyIdRequestVO) throws ServerException {
        if (partyIdRequestVO == null || partyIdRequestVO.getParty_id() == null || partyIdRequestVO.getParty_id() <= 0L) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "当事组织(门店或仓库)ID不能是空值");
        }
        try {
            List<PartyRecommendProductMappingRow> list = super.baseMapper.selectPartyRecommendProduct(partyIdRequestVO.getParty_id());
            if (UtilValidate.isNotEmpty(list)) {
                List<PartyRecommendProductItemResponseVO> return_list = new ArrayList<>(list.size());
                for (PartyRecommendProductMappingRow item : list) {
                    return_list.add(item.convertTo(ProductUtil.PROD_BRAND_GETTER));
                }
                return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>(return_list));
            }
            return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>(new ArrayList<>(0)));
        } catch (Exception e) {
            log.error("查询推荐位商品失败", e);
            return new Result<>(CodeEnum.FAIL_BUSINESS, "查询推荐位商品失败");
        }
    }

    /**
     * @author 王光银
     * @methodName: saveAppendManyPartyRecommendProduct
     * @methodDesc: 批量保存门店或仓库的推荐位商品
     * @description:
     * @param: [partyRecommendProdAppendRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-27 21:19
     **/
    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result saveAppendManyPartyRecommendProduct(
            PartyRecommendProdAppendRequestVO requestVO) throws ServerException {
        if (requestVO == null || requestVO.getParty_id() == null || requestVO.getParty_id() <= 0L) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "门店或仓库ID不能是空值");
        }
        if (UtilValidate.isEmpty(requestVO.getRecommend_prod_list())) {
            //推荐位商品数据为空时，处理上认为用户删除了所有的推荐商品
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.eq("party_id", requestVO.getParty_id());
            super.baseMapper.delete(wrapper);
            //清除缓存
            RedisCacheUtil.PartyRecommendProdCacheUtil.cleanByPartyId(requestVO.getParty_id());
            return new Result(CodeEnum.SUCCESS);
        }

        StaffAdminLoginVO admin_login = LoginCache.getStaffAdminLoginVO(requestVO.getLogin_token());
        if (admin_login == null) {
            return new Result(CodeEnum.FAIL_SERVER, "获取当前操作用户信息失败, token=" + requestVO.getLogin_token());
        }

        //请求参数中的推荐位商品验证：商品ID非空验证， 重复添加验证， 推荐日期合理性验证
        Map<Long, PartyRecommendProdItem> params_map = new HashMap<>(requestVO.getRecommend_prod_list().size());
        for (PartyRecommendProdItem recommendProdItem : requestVO.getRecommend_prod_list()) {
            if (recommendProdItem.getProduct_id() == null || recommendProdItem.getProduct_id() <= 0L) {
                return new Result(CodeEnum.FAIL_PARAMCHECK, "推荐位商品ID存在为空或小于等于0的错误数据:" + recommendProdItem.getProduct_id());
            }
            if (params_map.containsKey(recommendProdItem.getProduct_id())) {
                if (requestVO.getWhen_prod_duplicate().intValue() == 1) {
                    return new Result(CodeEnum.FAIL_PARAMCHECK, "商品:[" + productService.getProductName(recommendProdItem.getProduct_id()) + "]的重复添加");
                }
                continue;
            }
            if (UtilValidate.isNotEmpty(recommendProdItem.getStart_date()) && UtilValidate.isNotEmpty(recommendProdItem.getEnd_date())) {
                try {
                    Calendar c1 = Calendar.getInstance();
                    c1.setTime(DateUtil.parseDateTime(recommendProdItem.getStart_date()));
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(DateUtil.parseDateTime(recommendProdItem.getEnd_date()));
                    if (c1.after(c2)) {
                        return new Result(CodeEnum.FAIL_PARAMCHECK, "推荐位商品生效开始日期[" + recommendProdItem.getStart_date() + "]必须在结束日期[" + recommendProdItem.getEnd_date() + "]之前");
                    }
                } catch (Exception e) {
                    log.error("批量保存门店或仓库的推荐位商品 - 推荐商品展示开始日期、结束日期格式错误(start:" + recommendProdItem.getStart_date() + ", end:" + recommendProdItem.getEnd_date() + "),必须为: yyyy-MM-dd HH:mm:ss", e);
                    return new Result(CodeEnum.FAIL_PARAMCHECK, "推荐商品的开始或结束日期格式错误(start:" + recommendProdItem.getStart_date() + ", end:" + recommendProdItem.getEnd_date() + "),必须为: yyyy-MM-dd HH:mm:ss");
                }
            }
            if (UtilValidate.isNotEmpty(recommendProdItem.getEnd_date())) {
                try {
                    Calendar c1 = Calendar.getInstance();
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(DateUtil.parseDateTime(recommendProdItem.getEnd_date()));
                    if (c1.after(c2)) {
                        return new Result(CodeEnum.FAIL_PARAMCHECK, "推荐位商品展示结束日期[" + recommendProdItem.getEnd_date() + "]必须在当前日期之后");
                    }
                } catch (Exception e) {
                    log.error("推荐商品的结束日期格式错误:" + recommendProdItem.getEnd_date() + "),必须为: yyyy-MM-dd HH:mm:ss", e);
                    return new Result(CodeEnum.FAIL_PARAMCHECK, "推荐商品的结束日期格式错误:" + recommendProdItem.getEnd_date() + "),必须为: yyyy-MM-dd HH:mm:ss");
                }
            }
            params_map.put(recommendProdItem.getProduct_id(), recommendProdItem);
        }

        //请求的推荐商品的数据库状态验证：从中心商品池验证是否全网下架
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.in("id", params_map.keySet());
        wrapper.setSqlSelect("id", "forbid_sale_flag");
        List<ProductInfo> center_prod_list = productMapper.selectList(wrapper);
        if (UtilValidate.isEmpty(center_prod_list)) {
            return new Result(CodeEnum.FAIL_BUSINESS, "商品中心已不存在这些商品");
        }
        Map<Long, ProductInfo> center_prod_map = new HashMap<>(center_prod_list.size());
        for (ProductInfo info : center_prod_list) {
            center_prod_map.put(info.getId(), info);
        }

        Iterator<Long> it = params_map.keySet().iterator();
        while (it.hasNext()) {
            Long prod_id = it.next();
            if (!center_prod_map.containsKey(prod_id)) {
                if (requestVO.getWhen_prod_duplicate().intValue() == 1) {
                    return new Result(CodeEnum.FAIL_BUSINESS, "商品中心不存在ID[" + prod_id + "]的商品");
                }
                it.remove();
                continue;
            }
            if (ProductUtil.UP_STATUS.equals(center_prod_map.get(prod_id).getForbid_sale_flag())) {
                if (requestVO.getWhen_prod_duplicate().intValue() == 1) {
                    return new Result(CodeEnum.FAIL_BUSINESS, "商品:[" + productService.getProductName(prod_id) + "]已全网禁售");
                }
                it.remove();
                continue;
            }
        }

        //请求的推荐位商品的进一步验证：验证请求中的商品是否在请求中的当事组织商品池中，以及商品是否下架，是否符合商品销售日期
        wrapper = new EntityWrapper();
        wrapper.in("product_id", params_map.keySet());
        wrapper.eq("party_id", requestVO.getParty_id());
        wrapper.setSqlSelect("up_down_status", "introduction_date", "product_id", "sales_end_date");
        List<PartyProduct> party_prod_list = partyProductMapper.selectList(wrapper);
        if (UtilValidate.isEmpty(party_prod_list)) {
            return new Result(CodeEnum.FAIL_BUSINESS, "推荐位商品不属于当前组织(门店或仓库)的销售商品池");
        }
        Map<Long, PartyProduct> party_prod_map = new HashMap<>(party_prod_list.size());
        for (PartyProduct party_prod : party_prod_list) {
            party_prod_map.put(party_prod.getProduct_id(), party_prod);
        }
        for (Long prod_id : params_map.keySet()) {
            if (!party_prod_map.containsKey(prod_id)) {
                return new Result(CodeEnum.FAIL_BUSINESS, "商品:[" + productService.getProductName(prod_id) + "]不属于当前组织(门店或仓库)的销售商品");
            }
            PartyProduct party_prod_item = party_prod_map.get(prod_id);
            if (!ProductUtil.UP_STATUS.equals(party_prod_item.getUp_down_status())) {
                return new Result(CodeEnum.FAIL_BUSINESS, "商品:[" + productService.getProductName(prod_id) + "]已被当前组织(门店或仓库)下架,不能添加到推荐商品中");
            }
            if (party_prod_item.getSales_end_date() != null) {
                ProductUtil.CheckResult check_result = ProductUtil.checkProductIntroductionAndEndDate(null, party_prod_item.getSales_end_date());
                if (!check_result.res) {
                    return new Result(CodeEnum.FAIL_BUSINESS, "商品:[" + productService.getProductName(prod_id) + "]" + check_result.msg + "，不能添加到推荐位商品");
                }
            }

            //验证推荐位展示的日期与商品在当前组织的销售日期是否匹配
            PartyRecommendProdItem item = params_map.get(prod_id);
            if (UtilValidate.isNotEmpty(item.getStart_date()) && party_prod_item.getIntroduction_date() != null) {
                Calendar c1 = Calendar.getInstance();
                c1.setTime(DateUtil.parseDateTime(item.getStart_date()));
                Calendar c2 = Calendar.getInstance();
                c2.setTime(party_prod_item.getIntroduction_date());
                if (c1.before(c2)) {
                    return new Result(CodeEnum.FAIL_BUSINESS, "ID为:[" + prod_id
                            + "]的商品推荐位展示开始日期[" + item.getStart_date() +"]不能在这个商品在当前组织的销售开始日期["
                            + DateUtil.formatDateTime(party_prod_item.getIntroduction_date()) + "]之前");
                }
            }

            if (UtilValidate.isNotEmpty(item.getEnd_date()) && party_prod_item.getSales_end_date() != null) {
                Calendar c1 = Calendar.getInstance();
                c1.setTime(DateUtil.parseDateTime(item.getEnd_date()));
                Calendar c2 = Calendar.getInstance();
                c2.setTime(party_prod_item.getSales_end_date());
                if (c1.after(c2)) {
                    return new Result(CodeEnum.FAIL_BUSINESS, "商品:[" + productService.getProductName(prod_id)
                            + "]推荐位展示结束日期[" + item.getEnd_date() +"]不能在这个商品在当前组织的销售结束日期["
                            + DateUtil.formatDateTime(party_prod_item.getSales_end_date()) + "]之后");
                }
            }
        }

        try {
            //删除当前门店已有推荐位商品
            wrapper = new EntityWrapper();
            wrapper.eq("party_id", requestVO.getParty_id());
            super.baseMapper.delete(wrapper);
        } catch (Exception e) {
            log.error("删除门店已有推荐位商品失败", e);
            throw new BizException("删除门店已有推荐位商品失败", e);
        }

        try {
            //重新生成推荐位商品数据
            Calendar current = Calendar.getInstance();
            if (UtilValidate.isNotEmpty(params_map)) {
                List<PartyRecommendProduct> partyRecommendProductList = new ArrayList<>(params_map.size());
                int sort = params_map.size();

                for (PartyRecommendProdItem item : requestVO.getRecommend_prod_list()) {
                    if (!params_map.containsKey(item.getProduct_id())) {
                        continue;
                    }

                    PartyProduct partyProduct = party_prod_map.get(item.getProduct_id());

                    PartyRecommendProduct partyRecommendProduct = new PartyRecommendProduct();
                    partyRecommendProductList.add(partyRecommendProduct);
                    partyRecommendProduct.setParty_id(requestVO.getParty_id());
                    partyRecommendProduct.setTop_flag(sort --);
                    partyRecommendProduct.setProduct_id(item.getProduct_id());
                    partyRecommendProduct.setCreate_time(current.getTime());
                    partyRecommendProduct.setUpdate_time(current.getTime());
                    current.add(Calendar.SECOND, -1);

                    if (item.getStart_date() == null) {
                        if (partyProduct.getIntroduction_date() == null) {
                            partyRecommendProduct.setStart_date(current.getTime());
                        } else {
                            partyRecommendProduct.setStart_date(partyProduct.getIntroduction_date());
                        }
                    } else {
                        if (partyProduct.getIntroduction_date() == null
                                || DateUtil.parseDateTime(item.getStart_date()).before(partyProduct.getIntroduction_date())) {
                            partyRecommendProduct.setStart_date(partyProduct.getIntroduction_date());
                        } else {
                            partyRecommendProduct.setStart_date(DateUtil.parseDateTime(item.getStart_date()));
                        }
                    }

                    if (item.getEnd_date() == null) {
                        partyRecommendProduct.setEnd_date(partyProduct.getSales_end_date());
                    } else if (partyProduct.getSales_end_date() == null
                            || DateUtil.parseDateTime(item.getEnd_date()).after(partyProduct.getSales_end_date())) {
                        partyRecommendProduct.setEnd_date(partyProduct.getSales_end_date());
                    } else {
                        partyRecommendProduct.setEnd_date(DateUtil.parseDateTime(item.getEnd_date()));
                    }

                    partyRecommendProduct.setOperator_id(admin_login.getStaff_id());
                }
                super.insertBatch(partyRecommendProductList);
            }
            return new Result(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.error("保存推荐位商品失败", e);
            throw new BizException("保存推荐位商品失败", e);
        } finally {
            //清除缓存中的数据
            RedisCacheUtil.PartyRecommendProdCacheUtil.cleanByPartyId(requestVO.getParty_id());
        }
    }

    /**
     * @author 王光银
     * @methodName: deleteOneFromPartyRecommendProduct
     * @methodDesc: 删除一个门店或仓库的推荐商品
     * @description: 如果指定的推荐商品不存在，直接返回成功
     * @param: [idRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-27 21:22
     **/
    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result deleteOneFromPartyRecommendProduct(IdRequestVO idRequestVO) throws ServerException {
        if (idRequestVO == null || idRequestVO.getId() == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "推荐位商品ID不能是空值");
        }
        Long party_id = null;
        try {
            PartyRecommendProduct item = super.selectById(idRequestVO.getId());
            if (item != null) {
                party_id = item.getParty_id();
                item.deleteById();
            }
            return new Result(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.error("删除推荐位商品失败", e);
            throw new BizException("删除推荐位商品失败", e);
        } finally {
            //清除缓存中的数据
            RedisCacheUtil.PartyRecommendProdCacheUtil.cleanByPartyId(party_id);
        }
    }

    /**
     * @author 王光银
     * @methodName: copyToOtherParty
     * @methodDesc: 复制一个门店或仓库的推荐商品到其他的门店或仓库
     * @description:  复制推荐商品时会排除掉复制目标门店或仓库没有销售的商品
     * @param: [copyRecommendProdToOtherPartyRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-27 21:35
     **/
    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result copyToOtherParty(CopyRecommendProdToOtherPartyRequestVO requestVO) throws ServerException {
        if (requestVO == null || requestVO.getParty_id_from() == null || requestVO.getParty_id_from() <= 0L) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "复制源当事组织(门店或仓库)ID不能是空值");
        }
        if (UtilValidate.isEmpty(requestVO.getParty_id_to())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "请选择复制目标门店或仓库");
        }
        for (Long party_id : requestVO.getParty_id_to()) {
            if (requestVO.getParty_id_from().equals(party_id)) {
                return new Result(CodeEnum.FAIL_PARAMCHECK, "不能从自己复制到自己");
            }
        }

        StaffAdminLoginVO admin_login = LoginCache.getStaffAdminLoginVO(requestVO.getLogin_token());
        if (admin_login == null) {
            return new Result(CodeEnum.FAIL_BUSINESS, "获取当前操作用户信息失败，token=" + requestVO.getLogin_token());
        }

        /**
         * 复制推荐商品逻辑：
         * 1、复制目标中没有销售的商品不复制
         * 2、复制目标已经下架的商品不复制
         * 3、已经全网下架的商品不复制
         * 4、复制源组织中配置的商品推荐开始日期和结束日期不复制， 开始日期默认为当前系统时间, 复制置顶状态
         */

        //加载复制源当事组织的所有  有效的推荐商品
        Date current = new Date();
        Map<String, Object> params_map = new HashMap<>(5);
        params_map.put("party_id_set", UtilMisc.toSet(requestVO.getParty_id_from()));
        params_map.put("forbid_sale_flag", ProductUtil.UP_STATUS);
        params_map.put("up_down_status", ProductUtil.UP_STATUS);
        params_map.put("end_date", current);
        List<PartyRecommendProduct> fromPartyRecommendProductList = super.baseMapper.selectAvailablePartyRecommendProd(params_map);
        if (UtilValidate.isEmpty(fromPartyRecommendProductList)) {
            return new Result(CodeEnum.FAIL_BUSINESS, "复制源当事组织没有有效的推荐位商品");
        }

        Map<Long, PartyRecommendProduct> fromPartyRecommendProductMap = new HashMap<>(fromPartyRecommendProductList.size());

        for (PartyRecommendProduct item : fromPartyRecommendProductList) {
            fromPartyRecommendProductMap.put(item.getProduct_id(), item);
        }

        //加载复制目标当事组织的这些商品(从当事组织的商品池中加载)
        Map<Long, Map<Long, PartyProduct>> target_party_prod_map = new HashMap<>(requestVO.getParty_id_to().size());
        for (Long party_id : requestVO.getParty_id_to()) {
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.eq("party_id", party_id);
            wrapper.in("product_id", fromPartyRecommendProductMap.keySet());
            wrapper.setSqlSelect("product_id", "introduction_date", "sales_end_date", "up_down_status");
            List<PartyProduct> partyProductList = partyProductMapper.selectList(wrapper);
            if (UtilValidate.isNotEmpty(partyProductList)) {
                Map<Long, PartyProduct> partyProductMap = new HashMap<>(partyProductList.size());
                for (PartyProduct partyProduct : partyProductList) {
                    //将已下架的过滤掉
                    if (!ProductUtil.UP_STATUS.equals(partyProduct.getUp_down_status())) {
                        continue;
                    }
                    //将未到销售开始时间  或者  销售已经结束的过滤掉
                    if (partyProduct.getIntroduction_date() != null && current.before(partyProduct.getIntroduction_date())) {
                        continue;
                    }
                    if (partyProduct.getSales_end_date() != null && current.after(partyProduct.getSales_end_date())) {
                        continue;
                    }
                    partyProductMap.put(partyProduct.getProduct_id(), partyProduct);
                }
                if (UtilValidate.isNotEmpty(partyProductMap)) {
                    target_party_prod_map.put(party_id, partyProductMap);
                }
            }
        }

        if (UtilValidate.isEmpty(target_party_prod_map)) {
            return new Result(CodeEnum.SUCCESS);
        }

        //将已经全网禁售的过滤掉
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("forbid_sale_flag", ProductUtil.UP_STATUS);
        wrapper.in("id", fromPartyRecommendProductMap.keySet());
        wrapper.setSqlSelect("id");
        List<ProductInfo> forbid_sale_prod_list = productMapper.selectList(wrapper);
        if (UtilValidate.isNotEmpty(forbid_sale_prod_list)) {
            for (ProductInfo info : forbid_sale_prod_list) {
                Iterator<Long> it = target_party_prod_map.keySet().iterator();
                while (it.hasNext()) {
                    Map<Long, PartyProduct> item_map = target_party_prod_map.get(it.next());
                    if (item_map.containsKey(info.getId())) {
                        item_map.remove(info.getId());
                    }
                }
            }
        }

        //加载这些复制目标门店的已有的有效的推荐位商品进一步验证（复制目标组织是否已经添加了这些商品， 复制的时候数量则不再做验证）
        params_map.put("party_id_set", target_party_prod_map.keySet());
        List<PartyRecommendProduct> target_party_exists_recommend_prod_list = super.baseMapper.selectAvailablePartyRecommendProd(params_map);
        if (UtilValidate.isNotEmpty(target_party_exists_recommend_prod_list)) {
            Map<Long, Set<Long>> target_party_exists_recommend_prod_map = new HashMap<>(target_party_exists_recommend_prod_list.size());
            for (PartyRecommendProduct recommendProduct : target_party_exists_recommend_prod_list) {
                if (target_party_exists_recommend_prod_map.containsKey(recommendProduct.getParty_id())) {
                    target_party_exists_recommend_prod_map.get(recommendProduct.getParty_id()).add(recommendProduct.getProduct_id());
                } else {
                    target_party_exists_recommend_prod_map.put(recommendProduct.getParty_id(), UtilMisc.toSet(recommendProduct.getProduct_id()));
                }
            }

            Iterator<Long> it = target_party_prod_map.keySet().iterator();
            while (it.hasNext()) {
                Long target_party_id = it.next();
                if (target_party_exists_recommend_prod_map.containsKey(target_party_id)) {
                    //将已经存在的推荐商品过滤掉
                    Iterator<Long> thisIt = target_party_prod_map.get(target_party_id).keySet().iterator();
                    while (thisIt.hasNext()) {
                        Long prod_id = thisIt.next();
                        if (target_party_exists_recommend_prod_map.get(target_party_id).contains(prod_id)) {
                            thisIt.remove();
                        }
                    }
                }
            }
        }

        List<PartyRecommendProduct> need_to_insert_list = new ArrayList<>();
        //剩下的当事组织商品是符合添加到推荐位的商品
        Iterator<Long> it = target_party_prod_map.keySet().iterator();
        while (it.hasNext()) {
            Long party_id = it.next();
            Map<Long, PartyProduct> item_map = target_party_prod_map.get(party_id);
            Iterator<Long> prod_it = item_map.keySet().iterator();
            while (prod_it.hasNext()) {
                Long prod_id = prod_it.next();
                PartyProduct item = item_map.get(prod_id);

                //生成推荐位商品数据
                PartyRecommendProduct partyRecommendProduct = new PartyRecommendProduct();
                need_to_insert_list.add(partyRecommendProduct);
                partyRecommendProduct.setParty_id(party_id);
                partyRecommendProduct.setProduct_id(prod_id);
                if (item.getIntroduction_date() != null && current.before(item.getIntroduction_date())) {
                    partyRecommendProduct.setStart_date(item.getIntroduction_date());
                } else {
                    partyRecommendProduct.setStart_date(current);
                }

                if (item.getSales_end_date() != null) {
                    partyRecommendProduct.setEnd_date(item.getSales_end_date());
                }

                partyRecommendProduct.setTop_flag(fromPartyRecommendProductMap.get(prod_id).getTop_flag());
                partyRecommendProduct.setOperator_id(admin_login.getStaff_id());
            }
        }

        try {
            if (UtilValidate.isNotEmpty(need_to_insert_list)) {
                super.insertBatch(need_to_insert_list);
            }
            return new Result(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.error("保存复制广告位商品失败", e);
            throw new BizException("保存复制广告位商品失败", e);
        } finally {
            //清除复制目标门店的缓存数据
            if (UtilValidate.isNotEmpty(requestVO.getParty_id_to())) {
                for (Long party_id : requestVO.getParty_id_to()) {
                    RedisCacheUtil.PartyRecommendProdCacheUtil.cleanByPartyId(party_id);
                }
            }
        }

    }

    /**
     * @author 王光银
     * @methodName: topPartyRecommendProduct
     * @methodDesc: 置顶广告位商品
     * @description: 置顶广告位时更新时间，有多个置顶商品按照更新时间排序，越早的排在前面
     * @param: [idRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-07-04 9:19
     **/
    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result topPartyRecommendProduct(IdRequestVO idRequestVO) throws ServerException {
        return topUnTopPartyRecommendProd(idRequestVO.getId(), ProductUtil.UP_STATUS);
    }

    /**
     * @author 王光银
     * @methodName: topPartyRecommendProduct
     * @methodDesc: 撤消置顶广告位商品
     * @description:
     * @param: [idRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-07-04 9:19
     **/
    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result untopPartyRecommendProduct(IdRequestVO idRequestVO) throws ServerException {
        return topUnTopPartyRecommendProd(idRequestVO.getId(), ProductUtil.DOWN_STATUS);
    }

    private Result topUnTopPartyRecommendProd(Long party_recommend_prod_id, Integer top_flag) throws ServerException {
        if (party_recommend_prod_id == null || party_recommend_prod_id <= 0L) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "推荐位商品ID不能是空值");
        }
        PartyRecommendProduct partyRecommendProduct = super.selectById(party_recommend_prod_id);
        if (partyRecommendProduct == null) {
            return new Result(CodeEnum.FAIL_BUSINESS, "ID为[" + party_recommend_prod_id + "]的推荐位商品不存在");
        }
        try {
            partyRecommendProduct.setTop_flag(top_flag);
            partyRecommendProduct.updateById();
            return new Result(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.error((ProductUtil.UP_STATUS.equals(top_flag) ? "置顶" : "撤消置顶") + "广告位商品失败", e);
            throw new ServerException((ProductUtil.UP_STATUS.equals(top_flag) ? "置顶" : "撤消置顶") + "广告位商品失败", e);
        } finally {
            //清除缓存
            RedisCacheUtil.PartyRecommendProdCacheUtil.cleanByPartyId(partyRecommendProduct.getParty_id());
        }
    }
}
