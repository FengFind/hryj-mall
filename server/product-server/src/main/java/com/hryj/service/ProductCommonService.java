package com.hryj.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.cache.CodeCache;
import com.hryj.cache.LoginCache;
import com.hryj.cache.ProductBrandCacheHandler;
import com.hryj.cache.ProductTypeCacheHandler;
import com.hryj.common.BizCodeEnum;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.constant.CommonConstantPool;
import com.hryj.constant.DataDictionaryGroup;
import com.hryj.entity.bo.product.Brand;
import com.hryj.entity.bo.product.PartyProduct;
import com.hryj.entity.bo.product.ProductInfo;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.product.common.mapping.ProductMappingItem;
import com.hryj.entity.vo.product.common.request.ProductValidateItem;
import com.hryj.entity.vo.product.common.request.ProductsValidateRequestVO;
import com.hryj.entity.vo.product.common.request.SearchHSCodeRequestVO;
import com.hryj.entity.vo.product.common.request.TopSaleRequestVO;
import com.hryj.entity.vo.product.common.response.*;
import com.hryj.entity.vo.product.response.ProductTopSalesItemResponseVO;
import com.hryj.entity.vo.promotion.activity.response.ActivityInProgressProductItemResponseVO;
import com.hryj.entity.vo.staff.dept.response.DeptGroupResponseVO;
import com.hryj.entity.vo.staff.user.StaffAppLoginVO;
import com.hryj.feign.PartyFeignClient;
import com.hryj.feign.PromotionFeignClient;
import com.hryj.mapper.*;
import com.hryj.service.util.IdsHandler;
import com.hryj.service.util.PartyUtil;
import com.hryj.service.util.ProductUtil;
import com.hryj.service.util.ProductValidateUtil;
import com.hryj.utils.UtilMisc;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author 王光银
 * @className: ProductCommonService
 * @description:
 * @create 2018/7/9 0009 16:28
 **/
@Slf4j
@Service
public class ProductCommonService extends ServiceImpl<ProductMapper, ProductInfo> {

    @Autowired
    private PartyProductMapper partyProductMapper;

    @Autowired
    private PromotionFeignClient promotionFeignClient;

    @Autowired
    private PartyFeignClient partyFeignClient;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private ProductGeoMapper productGeoMapper;

    @Autowired
    private TaxRateMapper taxRateMapper;


    /**
     * @author 王光银
     * @methodName: productsValidate
     * @methodDesc: 验证失败返回编码说明:  16100:party_id对应的组织不存在,  11030: product_id对应的商品不存在, 11000: 商品下架， 11020：库存不足, 11010:活动已结束
     * @description:
     * @param: [productsValidateRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.product.common.response.ProductsValidateResponseVO>
     * @create 2018-08-07 16:10
     **/
    public Result<ProductsValidateResponseVO> productsValidate(ProductsValidateRequestVO productsValidateRequestVO) {
        //请求参数验证
        Result<IdsHandler> params_check = ProductValidateUtil.productsValidateRequestParamsValidate(productsValidateRequestVO);
        if (params_check.isFailed()) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, params_check.getMsg());
        }

        //加载商品中心的这些所有商品
        Result<Map<Long, ProductMappingItem>> load_center_prod_result = ProductValidateUtil.loadValidateProducts(params_check.getData().id_set_two, super.baseMapper);

        //加载当事组织的这些商品
        Result<Map<Long, Map<Long, PartyProduct>>> party_prod_load_result = ProductValidateUtil.loadValidatePartyProducts(productsValidateRequestVO.getProd_summary_list(), partyProductMapper);

        //调用接口获取所有当事组织的状态数据
        Result<Map<Long, DeptGroupResponseVO>> party_validate_result = PartyUtil.getManyPartySimpleInfo(UtilMisc.toList(params_check.getData().id_set_one), partyFeignClient);

        //调用接口获取所有活动的状态数据
        Result<Map<Long, Map<Long, ActivityInProgressProductItemResponseVO>>> activity_prod_validate_result = null;
        if (UtilValidate.isNotEmpty(params_check.getData().id_set_three)) {
            activity_prod_validate_result = ProductValidateUtil.loadActivityProdValidate(productsValidateRequestVO.getProd_summary_list(), promotionFeignClient);
        }

        ProductsValidateResponseVO return_vo = new ProductsValidateResponseVO();
        return_vo.setFollow_value(productsValidateRequestVO.getFollow_value());

        int valid_count = 0;
        int invalid_count = 0;

        Date current = new Date();

        /**
         * 库存合并验证容器
         */
        Map<Integer, Integer> prod_need_inventory_summary_map = new HashMap<>(productsValidateRequestVO.getProd_summary_list().size());

        List<ProductValidateResponseItem> return_item_list = new ArrayList<>(productsValidateRequestVO.getProd_summary_list().size());

        return_vo.setProd_validate_result_list(return_item_list);

        for (ProductValidateItem validateItem : productsValidateRequestVO.getProd_summary_list()) {
            ProductValidateResponseItem return_item = new ProductValidateResponseItem();

            return_item_list.add(return_item);

            return_item.setFollow_value(validateItem.getFollow_value());
            return_item.setParty_id(validateItem.getParty_id());
            return_item.setProduct_id(validateItem.getProduct_id());
            return_item.setActivity_id(validateItem.getActivity_id());
            return_item.setRequired_min_inventory_quantity(validateItem.getRequired_min_inventory_quantity());

            //设置商品基本信息
            boolean center_prod_invalid = load_center_prod_result == null
                    || load_center_prod_result.isFailed()
                    || UtilValidate.isEmpty(load_center_prod_result.getData())
                    || !load_center_prod_result.getData().containsKey(validateItem.getProduct_id());
            if (!center_prod_invalid) {
                ProductMappingItem mappingItem = load_center_prod_result.getData().get(validateItem.getProduct_id());

                return_item.setProduct_type_id(mappingItem.getProduct_type_id());
                return_item.setProduct_type_name(ProductTypeCacheHandler.getProductTypeDescription(mappingItem.getProduct_type_id()));
                return_item.setProduct_name(mappingItem.getProduct_name());
                return_item.setProd_cate_id(mappingItem.getProd_cate_id());
                return_item.setProd_cate_path(mappingItem.getProd_cate_path());
                return_item.setList_image_url(mappingItem.getList_image_url());
                return_item.setSpecification(mappingItem.getSpecification());
                Brand brand = ProductBrandCacheHandler.getBrand(mappingItem.getBrand());
                if (brand != null) {
                    return_item.setBrand(brand.convertToProdBrand());
                    return_item.setBrand_name(brand.getBrand_name());
                }
                return_item.setCost_price(mappingItem.getCost_price());
                return_item.setTitle_mark_list(ProductTypeCacheHandler.getProductTypeTitleMark(mappingItem.getProduct_type_id()));
                if (productsValidateRequestVO.getReturn_tax_rate() && ProductTypeCacheHandler.isCrossBorder(mappingItem.getProduct_type_id())) {
                    return_item.setCrossBorderProductValidateResponseItem(mappingItem.getCrossBorderProductValidateResponseItem());
                    return_item.setChannel(mappingItem.getChannel());
                    return_item.setChannel_name(ProductUtil.CROSS_BORDER_PROD_CHANNEL_GETTER.convert(mappingItem.getChannel()));
                }
            }

            boolean is_valid = true;
            Integer validate_status_code = BizCodeEnum.DEFAULT.getCode();
            String other_comments = null;

            /**
             * 商品验证按照既定验证顺序依次往下验证，在任意一个节点上的验证未通过，则剩余验证将不再执行
             * 1、验证商品所属的当事组织（门店或仓库）的状态
             * 2、验证商品是否全网禁售
             * 3、验证商品的当事组织（门店或仓库）的状态： 上下架、库存、销售时间限制
             * 4、验证商品活动：活动是否开始，活动是否结束，活动是否停用
             */
            if (party_validate_result.isFailed()
                    || UtilValidate.isEmpty(party_validate_result.getData())
                    || !party_validate_result.getData().containsKey(validateItem.getParty_id())) {
                is_valid = false;
                validate_status_code = BizCodeEnum.PARTY_NOT_EXISTS.getCode();
                error("当事组织验证失败 | 当事组织不存在");
                other_comments = "小店不在服务区";
            } else {
                DeptGroupResponseVO party = party_validate_result.getData().get(validateItem.getParty_id());
                if (party.getDept_status() == null || !ProductUtil.UP_STATUS.equals(party.getDept_status())) {
                    is_valid = false;
                    validate_status_code = BizCodeEnum.PARTY_NOT_EXISTS.getCode();
                    error("当事组织已关闭");
                    other_comments = "小店不在服务区";
                }
            }

            //验证商品中心是否已经全网禁售
            if (is_valid) {
                if (center_prod_invalid) {
                    if (is_valid) {
                        is_valid = false;
                        validate_status_code = BizCodeEnum.PRODUCT_NOT_EXISTS.getCode();
                        error("商品中心验证失败 | 商品中心没有该商品");
                        other_comments = "商品不存在";
                    }
                } else {
                    ProductMappingItem productInfo = load_center_prod_result.getData().get(validateItem.getProduct_id());
                    if (ProductUtil.UP_STATUS.equals(productInfo.getForbid_sale_flag()) && is_valid) {
                        is_valid = false;
                        validate_status_code = BizCodeEnum.PRODUCT_HAS_DOWN.getCode();
                        other_comments = "商品已下架";
                    }
                }
            }

            //验证当事组织的这个商品
            Integer target_compare_inventory_quantity = 0;
            if (is_valid) {
                boolean party_invalid = party_prod_load_result.isFailed()
                        || UtilValidate.isEmpty(party_prod_load_result.getData())
                        || !party_prod_load_result.getData().containsKey(validateItem.getParty_id())
                        || !party_prod_load_result.getData().get(validateItem.getParty_id()).containsKey(validateItem.getProduct_id());
                if (party_invalid) {
                    if (is_valid) {
                        is_valid = false;
                        validate_status_code = BizCodeEnum.PRODUCT_HAS_DOWN.getCode();
                        error("当事组织商品验证失败 | 当事组织不存在 | 当事组织没有这个商品");
                        other_comments = "商品已下架";
                    }
                } else {
                    PartyProduct partyProduct = party_prod_load_result.getData().get(validateItem.getParty_id()).get(validateItem.getProduct_id());
                    if (partyProduct.getInventory_quantity() == null) {
                        partyProduct.setInventory_quantity(0);
                    }
                    if (!ProductUtil.UP_STATUS.equals(partyProduct.getUp_down_status()) && is_valid) {
                        is_valid = false;
                        validate_status_code = BizCodeEnum.PRODUCT_HAS_DOWN.getCode();
                        other_comments = "商品已下架";
                    } else if (partyProduct.getIntroduction_date() != null
                            && current.before(partyProduct.getIntroduction_date())
                            && is_valid) {
                        is_valid = false;
                        validate_status_code = BizCodeEnum.PRODUCT_HAS_DOWN.getCode();
                        other_comments = "销售未开始";
                    } else if (partyProduct.getSales_end_date() != null
                            && current.after(partyProduct.getSales_end_date())
                            && is_valid) {
                        is_valid = false;
                        validate_status_code = BizCodeEnum.PRODUCT_HAS_DOWN.getCode();
                        other_comments = "销售已结束";
                    } else if (is_valid) {
                        Integer summary_need_inventory = validateItem.getRequired_min_inventory_quantity();

                        if (productsValidateRequestVO.getMerge_validate_inventory()) {
                            //根据商品类型来进行库存的验证
                            ProductMappingItem mappingItem = load_center_prod_result.getData().get(validateItem.getProduct_id());
                            Integer hash_key;
                            if (ProductTypeCacheHandler.isCrossBorder(mappingItem.getProduct_type_id())) {
                                hash_key = validateItem.getProduct_id().hashCode();
                                target_compare_inventory_quantity = mappingItem.getInventory_quantity();
                            } else {
                                hash_key = validateItem.getParty_id().hashCode() * validateItem.getProduct_id().hashCode();
                                target_compare_inventory_quantity = partyProduct.getInventory_quantity();
                            }

                            if (prod_need_inventory_summary_map.containsKey(hash_key)) {
                                summary_need_inventory += prod_need_inventory_summary_map.get(hash_key);
                            }
                            prod_need_inventory_summary_map.put(hash_key, summary_need_inventory);
                        }

                        if (summary_need_inventory.compareTo(target_compare_inventory_quantity) > 0) {
                            is_valid = false;
                            validate_status_code = BizCodeEnum.PRODUCT_INVERTORY_OUT.getCode();
                            other_comments = "库存不足";
                        }
                    }

                    return_item.setInventory_quantity(target_compare_inventory_quantity);
                    return_item.setNormal_price(partyProduct.getSale_price());
                    return_item.setThis_moment_sale_price(partyProduct.getSale_price());
                }
            }

            //最后验证活动
            if (is_valid) {
                if (validateItem.getActivity_id() != null && validateItem.getActivity_id() > 0L) {
                    return_item.setActivity_id(validateItem.getActivity_id());

                    boolean activity_invalid = activity_prod_validate_result == null
                            || activity_prod_validate_result.isFailed()
                            || activity_prod_validate_result.getData() == null
                            || !activity_prod_validate_result.getData().containsKey(validateItem.getActivity_id())
                            || !activity_prod_validate_result.getData().get(validateItem.getActivity_id()).containsKey(validateItem.getProduct_id());

                    if (activity_invalid) {
                        if (is_valid) {
                            is_valid = false;
                            validate_status_code = BizCodeEnum.ACTIVITY_HAS_END.getCode();
                            error("活动商品验证失败 | 不存在该活动 | 活动不包含该商品");
                            other_comments = "活动已结束";
                        }
                    } else {
                        ActivityInProgressProductItemResponseVO activity = activity_prod_validate_result.getData().get(validateItem.getActivity_id()).get(validateItem.getProduct_id());
                        if (!ProductUtil.UP_STATUS.equals(activity.getActivity_status()) && is_valid) {
                            is_valid = false;
                            validate_status_code = BizCodeEnum.ACTIVITY_HAS_END.getCode();
                            error("活动已失效 | 活动已结束 | 活动未开始");
                            other_comments = "活动已结束";
                        } else if (activity.getStart_date() == null || activity.getStart_date().after(current)) {
                            if (is_valid) {
                                is_valid = false;
                                validate_status_code = BizCodeEnum.ACTIVITY_HAS_END.getCode();
                                other_comments = "活动未开始";
                            }
                        } else if (activity.getEnd_date() != null
                                && activity.getEnd_date().before(current)
                                && is_valid) {
                            is_valid = false;
                            validate_status_code = BizCodeEnum.ACTIVITY_HAS_END.getCode();
                            other_comments = "活动已结束";
                        } else {
                            return_item.setThis_moment_sale_price(activity.getPromotion_price());
                            if (productsValidateRequestVO.getReturn_promotion_info()) {
                                return_item.setPromotion_info(activity);
                            }
                        }
                    }
                }
            }

            if (is_valid) {
                valid_count ++;
            } else {
                invalid_count ++;
            }

            return_item.setIs_valid(is_valid);
            return_item.setValidate_status_code(validate_status_code);
            return_item.setOther_comments(other_comments);
            if (return_item.getNormal_price() == null) {
                return_item.setNormal_price(BigDecimal.ZERO);
            }
            if (return_item.getThis_moment_sale_price() == null) {
                return_item.setThis_moment_sale_price(BigDecimal.ZERO);
            }
            if (return_item.getInventory_quantity() == null) {
                return_item.setInventory_quantity(BigDecimal.ZERO.intValue());
            }
        }

        return_vo.setInvalid_prod_num(invalid_count);
        return_vo.setValid_prod_num(valid_count);

        return new Result<>(CodeEnum.SUCCESS, return_vo);
    }

    /**
     * @author 王光银
     * @methodName: findTopSalesProduct
     * @methodDesc: 查询TOP销量商品
     * @description: 该接口的统计范围是在统计时刻仍然在销售的商品，已经下架的商品不管销售多高不会返回，同时参数party_id有值返回指定门店或仓库的TOP销售商品，否则返回全国范围内的TOP销量商品，目前返回的商品数量为10个
     * @param: [partyIdRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.product.response.ProductTopSalesItemResponseVO>>
     * @create 2018-07-03 21:45
     **/
    @PostMapping("/findTopSalesProduct")
    public Result<ListResponseVO<ProductTopSalesItemResponseVO>> findTopSalesProduct(
            @RequestBody TopSaleRequestVO requestVO) {
        Map<String, Object> params_map = generateTopSalesCondition(requestVO.getFrom_date(), requestVO.getTo_date());
        params_map.put("up_down_status", ProductUtil.UP_STATUS);
        return loadTopSalesProd(requestVO, params_map);
    }

    /**
     * @author 王光银
     * @methodName: findTopSalesProduct
     * @methodDesc: 查询TOP销量商品
     * @description: 该接口的统计范围是只按照销量统计，不管商品是否下架，返回集合按照销量排序， 参数 party_id有值返回指定门店或仓库的TOP销售商品，否则返回全国范围内的TOP销量商品，目前返回的商品数量为10个
     * @param: [partyIdRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.product.response.ProductTopSalesItemResponseVO>>
     * @create 2018-07-03 21:45
     **/
    @PostMapping("/findTopSalesAndHisProduct")
    public Result<ListResponseVO<ProductTopSalesItemResponseVO>> findTopSalesAndHisProduct(
            @RequestBody TopSaleRequestVO requestVO) {
        Map<String, Object> params_map = generateTopSalesCondition(requestVO.getFrom_date(), requestVO.getTo_date());
        return loadTopSalesProd(requestVO, params_map);
    }

    private Map<String, Object> generateTopSalesCondition(String from_date, String to_date) {
        Map<String, Object> params_map = new HashMap<>(3);
        Calendar finished_date_begin = null;
        if (UtilValidate.isNotEmpty(from_date)) {
            try {
                Date fromDate = DateUtil.parseDate(DateUtil.formatDate(DateUtil.parseDate(from_date)));
                finished_date_begin = Calendar.getInstance();
                finished_date_begin.setTime(fromDate);
                finished_date_begin.set(Calendar.HOUR_OF_DAY, 0);
                finished_date_begin.set(Calendar.MINUTE, 0);
                finished_date_begin.set(Calendar.SECOND, 0);
            } catch (Exception e) {
                throw new RuntimeException("查询起始日期格式错误, 要求格式:yyyy-MM-dd, 实际参数值:" + from_date);
            }
        }
        Calendar finished_date_end = null;
        if (UtilValidate.isNotEmpty(to_date)) {
            try {
                Date toDate = DateUtil.parseDate(DateUtil.formatDate(DateUtil.parseDate(to_date)));
                finished_date_end = Calendar.getInstance();
                finished_date_end.setTime(toDate);
                finished_date_end.set(Calendar.HOUR_OF_DAY, 23);
                finished_date_end.set(Calendar.MINUTE, 59);
                finished_date_end.set(Calendar.SECOND, 59);
            } catch (Exception e) {
                throw new RuntimeException("查询起始日期格式错误, 要求格式:yyyy-MM-dd, 实际参数值:" + from_date);
            }
        }
        if (finished_date_begin != null) {
            params_map.put("date_time_begin", DateUtil.formatDateTime(finished_date_begin.getTime()));
        }
        if (finished_date_end != null) {
            params_map.put("date_time_end", DateUtil.formatDateTime(finished_date_end.getTime()));
        }
        params_map.put("limit_start", 0);
        params_map.put("limit_end", 10);
        return params_map;
    }

    private Result<ListResponseVO<ProductTopSalesItemResponseVO>> loadTopSalesProd(TopSaleRequestVO requestVO, Map<String, Object> params_map) {
        try {
            boolean need_to_invoke_api = true;
            if (requestVO.getParty_id() == null || requestVO.getParty_id() <= 0L) {
                StaffAppLoginVO appLoginVO = LoginCache.getStaffAppLoginVO(requestVO.getLogin_token());

                if (appLoginVO == null) {
                    return new Result<>(CodeEnum.FAIL_BUSINESS, "获取当前请求用户信息失败, token=" + requestVO.getLogin_token());
                }
                if (appLoginVO.getDeptGroup() == null || appLoginVO.getDeptGroup().getId() == null || appLoginVO.getDeptGroup().getId() <= 0L) {
                    return new Result<>(CodeEnum.FAIL_BUSINESS, "当前请求用户的所属组织获取失败, 用户ID:" + appLoginVO.getStaff_id() + ", 用户姓名:" + appLoginVO.getStaff_name());
                }
                requestVO.setParty_id(appLoginVO.getDeptGroup().getId());
                need_to_invoke_api = false;
            }
            Set<Long> party_id_set;
            if (need_to_invoke_api) {
                Result<Set<Long>> party_id_result = PartyUtil.getSubPartyGroup(requestVO.getParty_id(), null, partyFeignClient);
                if (party_id_result.isFailed()) {
                    return new Result<>(CodeEnum.FAIL_BUSINESS, party_id_result.getMsg());
                }
                if (UtilValidate.isEmpty(party_id_result.getData())) {
                    party_id_set = UtilMisc.toSet(requestVO.getParty_id());
                } else {
                    party_id_set = party_id_result.getData();
                }
            } else {
                party_id_set = UtilMisc.toSet(requestVO.getParty_id());
            }

            log.info("TOP销售查询 - 搜索的组织范围ID:" + StrUtil.format("{}", party_id_set));

            if (party_id_set.size() == 1) {
                params_map.put("party_id", party_id_set.iterator().next());
            } else {
                params_map.put("party_id_list", party_id_set);
            }

            String loadModelFromOrderRightNow = "01";
            String loadModelFromDayStatistics = "02";

            //获取数据加载方式
            String execute_load_model = loadModelFromOrderRightNow;
            try {
                String config_value = CodeCache.getValueByKey(DataDictionaryGroup.ProductTopSalesStatisticsModelGroup, CommonConstantPool.S_ZERO_ONE);
                boolean config_value_not_null = UtilValidate.isNotEmpty(config_value);
                boolean config_value_allow = loadModelFromOrderRightNow.equals(config_value) || loadModelFromDayStatistics.equals(config_value);
                if (config_value_not_null && config_value_allow) {
                    execute_load_model = config_value;
                }
            } catch (Exception e) {
                log.error("获取商品TOP销量数据数据查询处理配置失败, 采用默认处理策略: 实时查询数据", e);
            }

            String date_time_begin = "date_time_begin";
            String date_time_end = "date_time_end";
            int date_str_max_length = 10;

            /**
             * 从历史统计数据中获取数据可以提高执行效率，但只适用于截止日期在当前日期之前的情况下
             * 如果终止日期就在当前日期自然天，无论配置的是哪一种执行模式，都必须将数据加载类型切换到实时统计查询
             */
            if (params_map.containsKey(date_time_end)) {
                String end_str = DateUtil.formatDate(DateUtil.parseDateTime(params_map.get(date_time_end).toString()));
                String curr_str = DateUtil.formatDate(new Date());
                if (end_str.equals(curr_str)) {
                    execute_load_model = loadModelFromOrderRightNow;
                }
            }

            if (loadModelFromOrderRightNow.equals(execute_load_model)) {
                //从订单数据中实时查询统计
                List<ProductTopSalesItemResponseVO> list = partyProductMapper.findPartyTopSalesByOrderFinished(params_map);
                if (UtilValidate.isEmpty(list)) {
                    return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>());
                }
                for (ProductTopSalesItemResponseVO vo : list) {
                    vo.setBrand_name(ProductUtil.PROD_BRAND_GETTER.convert(vo.getBrand()).getBrand_name());
                }
                return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>(list));
            } else {
                //从每日统计数据中查询统计
                if (params_map.containsKey(date_time_begin)) {
                    String value = params_map.get(date_time_begin).toString();
                    if (value.length() > date_str_max_length) {
                        params_map.put(date_time_begin, value.substring(0, 10));
                    }
                }
                if (params_map.containsKey(date_time_end)) {
                    String value = params_map.get(date_time_end).toString();
                    if (value.length() > date_str_max_length) {
                        params_map.put(date_time_end, value.substring(0, 10));
                    }
                }
                List<ProductTopSalesItemResponseVO> list = partyProductMapper.findPartyTopSalesByStatisticsData(params_map);
                if (UtilValidate.isEmpty(list)) {
                    return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>());
                }
                for (ProductTopSalesItemResponseVO vo : list) {
                    vo.setBrand_name(ProductUtil.PROD_BRAND_GETTER.convert(vo.getBrand()).getBrand_name());
                }
                return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>(list));
            }
        } catch (Exception e) {
            log.error("加载当事组织TOP销量商品数据失败", e);
            return new Result<>(CodeEnum.FAIL_BUSINESS, "加载当事组织TOP销量商品数据失败");
        }
    }

    /**
     * @author 汪豪
     * @methodName: findProductBrandByName
     * @methodDesc:
     * @description: 多条品牌数据只返回前十条数据
     * @param: [searchProductBrandRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.product.common.response.ProductBrandResponseVO>>
     * @create 2018-09-11 9:09
     **/
    public Result<ListResponseVO<ProductBrandResponseVO>> getBrandList() {
        List<ProductBrandResponseVO> records = brandMapper.getBrandList();
        ListResponseVO result = new ListResponseVO();
        result.setRecords(records);
        return new Result<>(CodeEnum.SUCCESS,result);
    }

    /**
     * @author 汪豪
     * @methodName: findProductGeoByName
     * @methodDesc:
     * @description: 多条商品产地数据只返回前十条数据
     * @param: [searchProductGeoRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.product.common.response.ProductGeoResponseVO>>
     * @create 2018-09-11 9:13
     **/
    public Result<ListResponseVO<ProductGeoResponseVO>> getProductGeoList() {
        List<ProductGeoResponseVO> records = productGeoMapper.getProductGeoList();
        ListResponseVO result = new ListResponseVO();
        result.setRecords(records);
        return new Result<>(CodeEnum.SUCCESS,result);
    }

    /**
     * @author 汪豪
     * @methodName: findTaxRateDataByHSCode
     * @methodDesc:
     * @description: 多条商品HSCode数据只返回前十条数据
     * @param: [searchHSCodeRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.product.common.response.ProductTaxRateResponseVO>>
     * @create 2018-09-11 9:16
     **/
    public Result<ListResponseVO<ProductTaxRateResponseVO>> findTaxRateListByHSCode(SearchHSCodeRequestVO searchHSCodeRequestVO) {
        if(StrUtil.isEmpty(searchHSCodeRequestVO.getHs_code())){
            return new Result<>(CodeEnum.SUCCESS);
        }
        List<ProductTaxRateResponseVO> records = taxRateMapper.findTaxRateListByHSCode(searchHSCodeRequestVO);
        ListResponseVO result = new ListResponseVO();
        result.setRecords(records);
        return new Result<>(CodeEnum.SUCCESS,result);
    }

    private void error(String... msg) {
        Long thisCurr = System.nanoTime();
        log.error("prod-validate-" + thisCurr + "\t----------------------------------------------------------------------------------------------------------------------------------");
        for (String s : msg) {
            log.error(s);
        }
        log.error("prod-validate-" + thisCurr + "\t----------------------------------------------------------------------------------------------------------------------------------");
    }

}
