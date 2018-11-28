package com.hryj.cache.util;

import cn.hutool.core.util.NumberUtil;
import com.hryj.cache.CodeCache;
import com.hryj.exception.BizException;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * @author 白飞
 * @className: CalculateTaxUtil
 * @description: 计算税费
 * @create 2018/9/13 10:02
 **/
public final class CalculateTaxUtil {

    /**
     * 不需要实例化
     */
    private CalculateTaxUtil(){

    }

    /**
     * 计算单个商品计算综合税费
     *
     * @param shop_price
     *          商品单价
     * @param quantity
     *          数量
     * @param net_content
     *          净含量
     * @param unit
     *          单位（片/张、毫升、克）
     * @param increment_tax
     *          增值税率
     * @param consume_tax
     *          消费税率
     * @return  单个商品综合税费
     */
    public static BigDecimal calculateTax(BigDecimal shop_price, Integer quantity, BigDecimal net_content, String unit, BigDecimal increment_tax, BigDecimal consume_tax){
        if(shop_price == null || quantity == null || increment_tax == null){
            throw  new  BizException("计算综合税参数错误");
        }
        if(shop_price.compareTo(new BigDecimal(0)) <= 0 || quantity <= 0 || increment_tax.compareTo(new BigDecimal(0)) <= 0){
            throw  new  BizException("计算综合税参数错误");
        }
        BigDecimal total_product_amount = NumberUtil.mul(shop_price, quantity);
        if(net_content != null && net_content.compareTo(new BigDecimal(0)) > 0 && !StringUtils.isEmpty(unit)){
            if(consume_tax == null || consume_tax.compareTo(new BigDecimal(0)) <= 0){
                throw  new BizException("消费税率不能为空");
            }
            //片或张
            String sheet = CodeCache.getValueByKey("NetContentGroup", "S01");
            //克
            String gram = CodeCache.getValueByKey("NetContentGroup", "S02");
            //毫升
            String ml = CodeCache.getValueByKey("NetContentGroup", "S03");
            String[] units = {sheet, gram, ml};
            if(!ArrayUtils.contains(units, unit)){
                throw  new  BizException("净含量单位错误");
            }
            //片或张-金额金额
            String sheet_amount = CodeCache.getValueByKey("NetContentUnitMaxAmountGroup", "SliceUnitMaxAmount");
            //克-金额金额
            String g_amount = CodeCache.getValueByKey("NetContentUnitMaxAmountGroup", "GUnitMaxAmount");
            //毫升-金额金额
            String ml_amount = CodeCache.getValueByKey("NetContentUnitMaxAmountGroup", "MLUnitMaxAmount");
            try{
                if(unit.equals(sheet) && NumberUtil.div(shop_price, net_content).compareTo(new BigDecimal(sheet_amount)) >= 0){
                    BigDecimal tax = NumberUtil.mul(NumberUtil.div(NumberUtil.add(increment_tax, consume_tax), NumberUtil.sub(1, consume_tax)), 0.7);
                    return  NumberUtil.mul(total_product_amount, tax);
                }else if(unit.equals(gram) && NumberUtil.div(shop_price, net_content).compareTo(new BigDecimal(g_amount)) >= 0){
                    BigDecimal tax = NumberUtil.mul(NumberUtil.div(NumberUtil.add(increment_tax, consume_tax), NumberUtil.sub(1, consume_tax)), 0.7);
                    return  NumberUtil.mul(total_product_amount, tax);
                }else if(unit.equals(ml) && NumberUtil.div(shop_price, net_content).compareTo(new BigDecimal(ml_amount)) >= 0){
                    BigDecimal tax = NumberUtil.mul(NumberUtil.div(NumberUtil.add(increment_tax, consume_tax), NumberUtil.sub(1, consume_tax)), 0.7);
                    return  NumberUtil.mul(total_product_amount, tax);
                }
                consume_tax = BigDecimal.ZERO;
                BigDecimal tax = NumberUtil.mul(NumberUtil.div(NumberUtil.add(increment_tax, consume_tax), NumberUtil.sub(1, consume_tax)), 0.7);
                return NumberUtil.mul(total_product_amount, tax);
            }catch (BizException e){
                throw  new  BizException("净含量单位参数有误，不能计算综合税率");
            }
        }
        consume_tax = null == consume_tax || consume_tax.compareTo(new BigDecimal(0)) <= 0 ? BigDecimal.ZERO : consume_tax;
        BigDecimal tax = NumberUtil.mul(NumberUtil.div(NumberUtil.add(increment_tax, consume_tax), NumberUtil.sub(1, consume_tax)), 0.7);
        return  NumberUtil.mul(total_product_amount, tax);
    }
}
