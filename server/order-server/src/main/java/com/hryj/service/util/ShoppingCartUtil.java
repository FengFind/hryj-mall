package com.hryj.service.util;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hryj.cache.CodeCache;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.cart.ShoppingCartRecord;
import com.hryj.mapper.ShoppingCartMapper;
import com.hryj.utils.UtilValidate;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @author 王光银
 * @className: ShoppingCartUtil
 * @description:
 * @create 2018/7/21 0021 17:34
 **/
@Slf4j
public class ShoppingCartUtil {

    public static final Integer USER_SHOPPING_CART_LIMIT = new Integer("100");

    public static final String USER_SHOPPING_CART_LIMIT_CONFIG_GROUP_KEY = "ShoppingProductLimit";
    public static final String USER_SHOPPING_CART_LIMIT_CONFIG_ITEM_KEY = "S01";


    //默认购物车
    public static final String SHOPPING_CART_TYPE_01 = "01";

    //代下单购物车
    public static final String SHOPPING_CART_TYPE_02 = "02";

    //促销购物车
    public static final String SHOPPING_CART_TYPE_03 = "03";

    //立即下单购物车
    public static final String SHOPPING_CART_TYPE_04 = "04";


    public static Integer getShoppingCartLimit() {
        //获取数量限制
        Integer limit = USER_SHOPPING_CART_LIMIT;
        try {
            limit = Integer.valueOf(CodeCache.getValueByKey(USER_SHOPPING_CART_LIMIT_CONFIG_GROUP_KEY, USER_SHOPPING_CART_LIMIT_CONFIG_ITEM_KEY));
        } catch (Exception e) {}
        return limit;
    }

    public static boolean userShoppingCartLimitOver(@NonNull Long user_id, @NonNull String cart_type, Long staff_id, @NonNull ShoppingCartMapper shoppingCartMapper) {
        //获取数量限制
        Integer limit = getShoppingCartLimit();
        Result<Integer> count_result = countUserShoppingCart(user_id, staff_id, cart_type, shoppingCartMapper);
        if (count_result.isFailed() || count_result.getData() == null) {
            return false;
        }
        return  count_result.getData().intValue() >= limit;
    }

    public static Result<Integer> countUserShoppingCart(@NonNull Long user_id, Long staff_id, @NonNull String cart_type, @NonNull ShoppingCartMapper shoppingCartMapper) {
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("user_id", user_id);
        wrapper.and().eq("cart_type", cart_type);
        if (staff_id != null && staff_id > 0L) {
            wrapper.and().eq("help_staff_id", staff_id);
        }
        try {
            Integer count = shoppingCartMapper.selectCount(wrapper);
            return new Result<>(CodeEnum.SUCCESS, count);
        } catch (Exception e) {
            log.error("查询用户购物车数量异常", e);
            return new Result<>(CodeEnum.FAIL_BUSINESS, "查询用户购物车数量异常");
        }
    }

    public static Result<ShoppingCartRecord> getExists(Long partyId,
                                                       Long productId,
                                                       Long activityId,
                                                       Map<String, Object> other_eq_condition,
                                                       ShoppingCartMapper shoppingCartMapper) {
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("party_id", partyId);
        wrapper.eq("product_id", productId);
        if (activityId != null && activityId > 0L) {
            wrapper.eq("activity_id", activityId);
        } else {
            wrapper.isNull("activity_id");
        }
        if (UtilValidate.isNotEmpty(other_eq_condition)) {
            for (Map.Entry<String, Object> entry : other_eq_condition.entrySet()) {
                wrapper.eq(entry.getKey(), entry.getValue());
            }
        }

        try {
            List<ShoppingCartRecord> records = shoppingCartMapper.selectList(wrapper);
            Result<ShoppingCartRecord> result = new Result<>(CodeEnum.SUCCESS);
            if (UtilValidate.isNotEmpty(records)) {
                result.setData(records.get(0));
            }
            return result;
        } catch (Exception e) {
            log.error("加载购物车商品数据异常", e);
            return new Result<>(CodeEnum.FAIL_BUSINESS, "加载购物车商品数据异常");
        }
    }

    public static boolean containsThisCartType(String cart_type) {
        if (UtilValidate.isEmpty(cart_type)) {
            return false;
        }
        if (SHOPPING_CART_TYPE_01.equals(cart_type)
                || SHOPPING_CART_TYPE_02.equals(cart_type)
                || SHOPPING_CART_TYPE_03.equals(cart_type)
                || SHOPPING_CART_TYPE_04.equals(cart_type)) {
            return true;
        }
        return false;
    }

    public static boolean needToCheckLimit(String cart_type) {
        return !SHOPPING_CART_TYPE_04.equals(cart_type);
    }
}
