package com.hryj.service.util;

import com.hryj.cache.CodeCache;
import com.hryj.cache.ProductBrandCacheHandler;
import com.hryj.cache.ProductGeoCacheHandler;
import com.hryj.cache.ProductTypeCacheHandler;
import com.hryj.common.Result;
import com.hryj.common.SysCodeEnmu;
import com.hryj.constant.CommonConstantPool;
import com.hryj.constant.DataDictionaryGroup;
import com.hryj.entity.bo.product.Brand;
import com.hryj.entity.bo.product.ProductGeo;
import com.hryj.entity.vo.delegator.GenericConverter;
import com.hryj.entity.vo.product.response.app.AppProdAttrItemResponseVO;
import com.hryj.entity.vo.product.response.app.AppProdListItemResponseVO;
import com.hryj.entity.vo.product.response.app.AppProductInfoResponseVO;
import com.hryj.entity.vo.staff.dept.response.DeptGroupResponseVO;
import com.hryj.feign.PartyFeignClient;
import com.hryj.utils.UtilMisc;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author 王光银
 * @className: ProductUtil
 * @description: 商品相关的工具类
 * @create 2018/7/7 0007 14:52
 **/
@Slf4j
public class ProductUtil {

    public static final WeakHashLock<Long> COMMON_LOCK = new WeakHashLock<>();


    public static final String PROD_LONG_DESCRIPTION_TEMPLETE_CODE_TYPE = "ProdLongDescriptionTemplete";
    public static final String PROD_LONG_DESCRIPTION_TEMPLETE_CODE = "S01";

    public static final String PROD_ATTR_TYPE_CATE = "01";

    public static final String PROD_ATTR_TYPE_DEFINE = "02";

    protected static final Integer PROD_AUDIT_STATUS_AUDITED = 1;

    public static final Integer PROD_AUDIT_STATUS_UNAUDITED = 0;

    public static final Integer UP_STATUS = 1;

    public static final Integer DOWN_STATUS = 0;

    public static final String PARTY_TYPE_STORE = "store";

    public static final String PARTY_TYPE_WAREHOUSE = "warehouse";

    public static final Integer DATA_STATUS_COVERED = 1;
    public static final Integer DATA_STATUS_NOT_COVERED = 0;

    public static final Integer HANDLE_STATUS_HANDLED = 1;
    public static final Integer HANDLE_STATUS_NOT_HANDLED = 0;

    public static final String DATA_TYPE_CENTER = "01";
    public static final String DATA_TYPE_PARTY = "02";

    public static final String SUBMIT_TYPE_ADD = "01";
    public static final String SUBMIT_TYPE_MODIFY = "02";

    public static final String ENUM_ATTR_TYPE = "01";
    public static final String STRING_ATTR_TYPE = "02";

    public static final Calendar FIVE_HUNDRED_YEARS_LATER = Calendar.getInstance();

    public static final GenericConverter<String> PROD_TYPE_NAME_GETTER = ((Object value) -> (ProductTypeCacheHandler.getProductTypeDescription(value == null ? null : value.toString())));

    public static final GenericConverter<Brand> PROD_BRAND_GETTER = ((Object value) -> (ProductBrandCacheHandler.getBrand(value == null ? null : (Long) value)));

    public static final GenericConverter<ProductGeo> PROD_MADE_WHERE_GETTER = ((Object value) -> (ProductGeoCacheHandler.getProductGeo(value == null ? null : (Long) value)));

    public static final GenericConverter<String> PROD_LONG_DESC_GETTER = ((Object value) -> (value == null ? null : getProductLongDesc(value.toString())));

    public static final GenericConverter<Boolean> PROD_IS_CROSS_BORDER_GETTER = ((Object value) -> (value == null ? false : ProductTypeCacheHandler.isCrossBorder(value.toString())));

    public static final GenericConverter<String> CROSS_BORDER_PROD_CHANNEL_GETTER = ((Object value) -> (value == null ? null : CodeCache.getNameByKey(DataDictionaryGroup.CrossBorderProductDeliveryWarehouse, CommonConstantPool.S_ZERO_ONE)));

    static {
        FIVE_HUNDRED_YEARS_LATER.set(Calendar.YEAR, 2500);
        FIVE_HUNDRED_YEARS_LATER.set(Calendar.MONTH, 0);
        FIVE_HUNDRED_YEARS_LATER.set(Calendar.DAY_OF_MONTH, 1);
        FIVE_HUNDRED_YEARS_LATER.set(Calendar.HOUR_OF_DAY, 0);
        FIVE_HUNDRED_YEARS_LATER.set(Calendar.MINUTE, 0);
        FIVE_HUNDRED_YEARS_LATER.set(Calendar.SECOND, 0);
    }

    public static CheckResult checkProductIntroductionAndEndDate(Date introduction_date, Date sales_end_date) {
        return checkProductIntroductionAndEndDate(introduction_date, sales_end_date, Calendar.getInstance());
    }

    public static CheckResult checkProductIntroductionAndEndDate(Date introduction_date, Date sales_end_date, Calendar current) {
        if (introduction_date == null && sales_end_date == null) {
            return new CheckResult();
        }
        if (introduction_date != null && sales_end_date != null) {
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c1.setTime(introduction_date);
            c2.setTime(sales_end_date);
            return checkProductIntroductionAndEndDate(c1, c2, current);
        }
        if (introduction_date != null) {
            Calendar c1 = Calendar.getInstance();
            c1.setTime(introduction_date);
            return checkProductIntroductionAndEndDate(c1, null, current);
        }
        Calendar c2 = Calendar.getInstance();
        c2.setTime(sales_end_date);
        return checkProductIntroductionAndEndDate(null, c2, current);
    }

    public static CheckResult checkProductIntroductionAndEndDate(Calendar introduction_date, Calendar sales_end_date, Calendar current) {
        if (UtilValidate.isEmpty(introduction_date) && UtilValidate.isEmpty(sales_end_date)) {
            return new CheckResult();
        }
        if (introduction_date != null) {
            if (current.before(introduction_date)) {
                return new CheckResult(false, "销售开始日期未到[" + introduction_date + "]");
            }
        }
        if (sales_end_date != null) {
            if (current.after(sales_end_date)) {
                return new CheckResult(false, "销售已经结束[" + sales_end_date + "]");
            }
        }
        return new CheckResult(true);
    }


    public static class CheckResult {
        public CheckResult() {}
        public CheckResult(Boolean res) {
            this.res = res;
        }
        public CheckResult(Boolean res, String msg) {
            this.res = res;
            this.msg = msg;
        }
        public Boolean res = true;
        public String msg;
    }

    public static String getProductLongDesc(String long_description) {
        if (UtilValidate.isEmpty(long_description)) {
            return null;
        }
        String templete = null;
        try {
            templete = CodeCache.getValueByKey(PROD_LONG_DESCRIPTION_TEMPLETE_CODE_TYPE, PROD_LONG_DESCRIPTION_TEMPLETE_CODE);
        } catch (Exception e) {}
        if (UtilValidate.isEmpty(templete)) {
            templete = "<!DOCTYPE html><html><head><meta charset=\"utf-8\" /><meta name=\"viewport\" content=\"width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no\"><title></title><style>html,body{margin: 0;padding: 0;}img{vertical-align: top;}</style></head><body>\n" +
                    "${long_description}</body></html>";
        }
        return templete.replace("${long_description}", long_description);
    }

    public static void loadProdDistributionInfo(AppProductInfoResponseVO return_vo, PartyFeignClient partyFeignClient) {
        //(invoke-api)首先调用接口得到当事组织的信息.
        Result<Map<Long, DeptGroupResponseVO>> party_check_result = PartyUtil.getManyPartySimpleInfo(UtilMisc.toList(return_vo.getParty_id()), partyFeignClient);
        if (party_check_result.isFailed()
                || UtilValidate.isEmpty(party_check_result.getData())) {
            log.error("APP推荐端口加载 - 验证覆盖当前用户的当事组织失败" + party_check_result.getMsg());
        }
        if (party_check_result.isFailed()
                || UtilValidate.isEmpty(party_check_result.getData())) {
            return;
        }

        DeptGroupResponseVO party_vo = party_check_result.getData().values().iterator().next();
        if (SysCodeEnmu.DEPTETYPE_01.getCodeValue().equals(party_vo.getDept_type())) {
            String distribution_desc = CodeCache.getValueByKey("StoreDistributionDesc", "S01");
            String distribution_mark_image = CodeCache.getValueByKey("StoreDistributionDesc", "S02");
            return_vo.setDistribution_info(UtilValidate.isEmpty(distribution_desc) ? "缺少配置" : distribution_desc);
            return_vo.setDistribution_mark_image(UtilValidate.isEmpty(distribution_mark_image) ? "" : distribution_mark_image);
        } else if (SysCodeEnmu.DEPTETYPE_02.getCodeValue().equals(party_vo.getDept_type())) {
            String distribution_desc = CodeCache.getValueByKey("WarehouseDistributionDesc", "S01");
            return_vo.setDistribution_info(UtilValidate.isEmpty(distribution_desc) ? "缺少配置" : distribution_desc);
        } else {
            log.error("APP商品获取商品详细信息 - 加载商品当事组织信息以获取配送信息， 接口返回了不识别的当事组织类型: " + party_vo.getDept_type());
        }
    }

    public static void copyProdAttr(AppProductInfoResponseVO vo, List<Map<String, Object>> prod_attr_list) {
        List<AppProdAttrItemResponseVO> attr_list = new ArrayList<>(prod_attr_list.size());
        vo.setAttr_list(attr_list);
        for (Map<String, Object> map : prod_attr_list) {
            AppProdAttrItemResponseVO attr_item = new AppProdAttrItemResponseVO();
            attr_list.add(attr_item);
            attr_item.setAttr_name((String) map.get("attr_name"));
            attr_item.setAttr_value((String) map.get("attr_value"));
        }
    }

    public static List<AppProdListItemResponseVO> calculateAndGetPageProdData(TreeSet<PartyProdHandler> party_set, int need_how_many) {
        /**
         * 商品搜索分页数据的切割规则：始终数据的最后开始往前切割，切割数据量满足当前需要的数据量时立即返回。
         * 切割优先级与刚开始的数据加载优先级刚好相反, 从加载优先级最低的数据开始切割
         */

        List<AppProdListItemResponseVO> need_list = new ArrayList<>(need_how_many);

        List<PartyProdHandler> treeSetList = new ArrayList<>(party_set.size());
        for (PartyProdHandler partyProdHandler : party_set) {
            treeSetList.add(partyProdHandler);
        }

        //这是记录已经切割到的商品数量
        int has_get_num = 0;

        //首先从普通商品开始切割
        for (int i = treeSetList.size() - 1; i >= 0 ; i--) {
            if (has_get_num >= need_how_many) {
                break;
            }
            PartyProdHandler item = treeSetList.get(i);
            if (UtilValidate.isEmpty(item.normal_prod_list)) {
                continue;
            }

            if (item.normal_prod_list.size() <= (need_how_many - has_get_num)) {
                need_list.addAll(0, item.normal_prod_list);
                has_get_num += item.normal_prod_list.size();
            } else {
                int this_need = need_how_many - has_get_num;
                need_list.addAll(0, item.normal_prod_list.subList(item.normal_prod_list.size() - this_need, item.normal_prod_list.size()));
                has_get_num += this_need;
            }
        }

        //普通商品切割完的数量不够时，继续切割活动商品
        if (need_list.size() < need_how_many) {
            for (int i = treeSetList.size() - 1; i >= 0 ; i--) {
                if (has_get_num >= need_how_many) {
                    break;
                }
                PartyProdHandler item = treeSetList.get(i);
                if (UtilValidate.isEmpty(item.activity_prod_list)) {
                    continue;
                }

                if (item.activity_prod_list.size() <= (need_how_many - has_get_num)) {
                    need_list.addAll(0, item.activity_prod_list);
                    has_get_num += item.activity_prod_list.size();
                } else {
                    int this_need = need_how_many - has_get_num;
                    need_list.addAll(0, item.activity_prod_list.subList(item.activity_prod_list.size() - this_need, item.activity_prod_list.size()));
                    has_get_num += this_need;
                }
            }
        }

        return need_list;
    }
}
