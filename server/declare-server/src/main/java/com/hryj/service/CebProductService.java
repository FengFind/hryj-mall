package com.hryj.service;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.hryj.cache.CodeCache;
import com.hryj.entity.bo.declare.CebProduct;
import com.hryj.exception.BizException;
import com.hryj.mapper.CebProductMapper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author 白飞
 * @className: CebProductService
 * @description:
 * @create 2018/10/8 10:26
 **/
@Service
public class CebProductService extends ServiceImpl<CebProductMapper, CebProduct> {

    @Autowired
    private CebTaxRateService cebTaxRateService;

    /**
     * 根据电商备案编号和商品货号查询
     *
     * @param ebcCode
     *          电商备案编号
     * @param itemNo
     *          商品货号
     * @return 备案商品信息
     */
    public CebProduct findCebProduct(String ebcCode, String itemNo){
        if(StringUtils.isEmpty(ebcCode) || StringUtils.isEmpty(itemNo)){
            return null;
        }
        EntityWrapper<CebProduct> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("item_no",itemNo);
        entityWrapper.eq("ebc_code",ebcCode);
        return super.selectOne(entityWrapper);
    }

    /**
     * 根据电商备案编号和商品货号查询
     *
     * @param ebcCode
     *          电商备案编号
     * @param itemNo
     *          商品货号
     * @return 备案商品信息集合
     *
     */
    public List<CebProduct> findList(String ebcCode, String itemNo){
        if(StringUtils.isEmpty(ebcCode) || StringUtils.isEmpty(itemNo)){
            return null;
        }
        EntityWrapper<CebProduct> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("item_no",itemNo);
        entityWrapper.eq("ebc_code",ebcCode);
        return super.selectList(entityWrapper);
    }

    /**
     * 根据电商备案编号和商品货号查询
     *
     * @param ebcCode
     *          电商备案编号
     * @param itemNos
     *          商品货号集合
     * @return 备案商品信息集合
     *
     */
    public List<CebProduct> findList(String ebcCode, List<String> itemNos){
        if(StringUtils.isEmpty(ebcCode) || null == itemNos || itemNos.size() == 0){
            return null;
        }
        EntityWrapper<CebProduct> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("item_no", itemNos);
        entityWrapper.eq("ebc_code",ebcCode);
        return super.selectList(entityWrapper);
    }

    /**
     * 根据电商备案编号和商品货号查询
     *
     * @param ebcCode
     *          电商备案编号
     * @param itemNos
     *          商品货号集合
     * @return 备案商品信息Map集合
     *
     */
    public Map<String, CebProduct> findListToMap(String ebcCode, List<String> itemNos){
        if(StringUtils.isEmpty(ebcCode) || null == itemNos || itemNos.size() == 0){
            return null;
        }
        EntityWrapper<CebProduct> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("ebc_code",ebcCode);
        entityWrapper.in("item_no", itemNos);
        List<CebProduct> cebProducts = super.selectList(entityWrapper);
        if(null == cebProducts){
            return null;
        }
        Map<String, CebProduct> productMap = Maps.newHashMap();
        for(CebProduct cebProduct : cebProducts){
            productMap.put(cebProduct.getItemNo(), cebProduct);
        }
        return productMap;
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
     * @param gunit
     *          单位（片/张、毫升、克）
     * @param increment_tax
     *          增值税率
     * @param consume_tax
     *          消费税率
     * @return  单个商品综合税费
     */
    public BigDecimal calculateTax(BigDecimal shop_price, Integer quantity, BigDecimal net_content, String gunit, BigDecimal increment_tax, BigDecimal consume_tax){
        if(shop_price == null || quantity == null || increment_tax == null){
            throw  new  BizException("计算综合税参数错误");
        }
        if(shop_price.compareTo(new BigDecimal(0)) <= 0 || quantity <= 0 || increment_tax.compareTo(new BigDecimal(0)) <= 0){
            throw  new  BizException("计算综合税参数错误");
        }
        BigDecimal total_product_amount = NumberUtil.mul(shop_price, quantity);
        if(net_content != null && net_content.compareTo(new BigDecimal(0)) > 0 && !org.springframework.util.StringUtils.isEmpty(gunit)){
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
            if(!ArrayUtils.contains(units, gunit)){
                throw  new BizException("净含量单位错误");
            }
            //片或张-金额金额
            String sheet_amount = CodeCache.getValueByKey("NetContentUnitMaxAmountGroup", "SliceUnitMaxAmount");
            //克-金额金额
            String g_amount = CodeCache.getValueByKey("NetContentUnitMaxAmountGroup", "GUnitMaxAmount");
            //毫升-金额金额
            String ml_amount = CodeCache.getValueByKey("NetContentUnitMaxAmountGroup", "MLUnitMaxAmount");
            try{
                if(gunit.equals(sheet) && NumberUtil.div(shop_price, net_content).compareTo(new BigDecimal(sheet_amount)) >= 0){
                    BigDecimal tax = NumberUtil.mul(NumberUtil.div(NumberUtil.add(increment_tax, consume_tax), NumberUtil.sub(1, consume_tax)), 0.7);
                    return  NumberUtil.mul(total_product_amount, tax);
                }else if(gunit.equals(gram) && NumberUtil.div(shop_price, net_content).compareTo(new BigDecimal(g_amount)) >= 0){
                    BigDecimal tax = NumberUtil.mul(NumberUtil.div(NumberUtil.add(increment_tax, consume_tax), NumberUtil.sub(1, consume_tax)), 0.7);
                    return  NumberUtil.mul(total_product_amount, tax);
                }else if(gunit.equals(ml) && NumberUtil.div(shop_price, net_content).compareTo(new BigDecimal(ml_amount)) >= 0){
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
