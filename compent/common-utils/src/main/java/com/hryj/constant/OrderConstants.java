package com.hryj.constant;

import com.google.common.collect.Maps;

import java.util.*;

/**
 * @author BF
 * @className: OrderConstants
 * @description: 订单常量
 * @create 2018/9/12 16:05
 **/
public class OrderConstants {

    /**
     * 第三方订单状态
     */
    public  enum  ThirdOrderStatus{

        /** 预留 */
        reserve,
        /**待申报 */
        waitDeclare,
        /** 申报中 */
        declareing,
        /** 申报通过 */
        declareSuccess,
        /** 申报失败 */
        declareFail,
        /** 已取消 */
        cancel
    }

    /** 第三方订单状态状态描述 */
    public final static String[] THIRD_ORDER_STATUS = {"预留","待申报","海关申报中","海关申报通过","海关申报失败","订单已取消"};

    /** 医药订单 */
    public final static String DRUG_ORDER = "drug_order";
    /** 销售订单 */
    public final static String SALES_ORDER = "sales_order";
    /** 采购订单 */
    public final static String PURCHASE_ORDER = "purchase_order";

    /** 保健品订单 */
    public final static String HEALTH_ORDER = "health_order";

    /** 新零售一般订单 */
    public final static String NORMAL_ORDER = "normal_order";
    /** 新零售预售周期订单 */
    public final static String PRE_SALE_CYCLE = "pre_sale_cycle";
    /** 新零售预售订单 */
    public final static String PRE_SALE_ORDER = "pre_sale_order";
    /** 新零售订单 */
    public final static String NEW_RETAIL_ORDER = "new_retail_order";

    /** 跨境订单 */
    public final static String CROSS_BORDER_ORDER = "cross_border_order";
    /** 跨境保税订单 */
    public final static String CROSS_BORDER_BONDED_ORDER = "cross_border_bonded_order";
    /** 跨境直邮订单 */
    public final static String CROSS_BORDER_DIRECT_ORDER = "cross_border_direct_order";

    /** 跨境商品消费税 */
    public final static String EXCISE_TAX = "excise_tax";
    /** 跨境商品综合税 */
    public final static String CROSS_BORDER_PROD_TAX = "cross_border_prod_tax";
    /** 跨境商品增值税务 */
    public final static String VALUE_ADDED_TAX = "value_added_tax";

    /** 跨境商品 */
    public final  static  String CROSS_BORDER = "cross_border";
    /** 跨境保税商品 */
    public final  static  String BONDED = "bonded";

    /** 一般商品 */
    public final  static  String GENERAL = "general";
    /** 医药商品商品 */
    public final  static  String DRUG = "drug";
    /** 新零售商品 */
    public final  static  String NEW_RETAIL = "new_retail";

    /** 新零售订单集合  */
    public final static String[] RESALE_ORDER_TYPES = {NORMAL_ORDER, PRE_SALE_CYCLE, PRE_SALE_ORDER, NEW_RETAIL_ORDER};
    /** 跨境订单集合 */
    public final static String[] CROSS_BORDER_ORDER_TYPES = {BONDED, CROSS_BORDER, CROSS_BORDER_ORDER, CROSS_BORDER_BONDED_ORDER, CROSS_BORDER_DIRECT_ORDER};

    /** 新零售订单集合  */
    public final static List<String> RESALE_ORDER_TYPE_LIST = new  ArrayList<>(Arrays.asList(RESALE_ORDER_TYPES));
    /** 跨境订单集合 */
    public final static List<String> CROSS_BORDER_ORDER_TYPE_LIST = new  ArrayList<>(Arrays.asList(CROSS_BORDER_ORDER_TYPES));

    /** 跨境商品类型 对应 跨境订单类型 */
    public  final  static Map<String, String> CROSS_BORDER_ORDER_TYPE_MAP = getCrossBorderOrderTypeMap();

    /** 订单所有类型 */
    public  final  static Map<String, String> ORDER_TYPE_ALL_MAP = getOrderTypeAllMap();


    /**
     * 跨境商品为K，对应订单类型值
     * @return
     */
    private static Map<String, String> getCrossBorderOrderTypeMap(){
        Map<String, String> crossBorderMap = Maps.newHashMap();
        crossBorderMap.put(CROSS_BORDER, CROSS_BORDER_ORDER);
        crossBorderMap.put(BONDED, CROSS_BORDER_BONDED_ORDER);
        return  crossBorderMap;
    }

    /**
     * 商品为K，对应订单类型值
     * @return
     */
    private static Map<String, String> getOrderTypeAllMap(){
        Map<String, String> productMap = Maps.newHashMap();
        productMap.put(CROSS_BORDER, CROSS_BORDER_ORDER);
        productMap.put(BONDED, CROSS_BORDER_BONDED_ORDER);
        productMap.put(DRUG, DRUG_ORDER);
        productMap.put(NEW_RETAIL, NEW_RETAIL_ORDER);
        return  productMap;
    }



}


