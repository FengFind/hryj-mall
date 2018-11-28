package com.hryj.synchronizationtask;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hryj.common.ThirdOrderStatusEnum;
import com.hryj.entity.bo.product.crossborder.CrossBorderProduct;
import com.hryj.mapper.CrossBorderProductMapper;
import com.hryj.permission.PermissionManageHandler;
import com.hryj.service.inventory.cache.InventoryCacheUtil;
import com.hryj.service.inventory.cache.ProductInventorySummary;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.util.List;

/**
 * @author 汪豪
 * @className: SyncCrossBorderProductInventoryTask
 * @description:
 * @create 2018/9/19 0019 14:54
 **/
@Slf4j
@Component
public class SyncCrossBorderProductInventoryTask {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CrossBorderProductMapper crossBorderProductMapper;

    public void syncCrossBorderProductInventory() {
        Long start = System.currentTimeMillis();
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.setSqlSelect("id", "third_sku_id");
        List<CrossBorderProduct> crossBorderProducts = crossBorderProductMapper.selectList(wrapper);
        if (CollectionUtil.isNotEmpty(crossBorderProducts)) {
            for (CrossBorderProduct crossBorderProduct : crossBorderProducts) {
                if (StrUtil.isNotEmpty(crossBorderProduct.getThird_sku_id())) {
                    String params = createParams(crossBorderProduct.getThird_sku_id());
                    if (StrUtil.isNotEmpty(params)) {
                        String data = restTemplate.postForObject(ThirdOrderStatusEnum.URL,
                                params, String.class);
                        log.info("返回结果：{}" + Base64.decodeStr(data));
                        JSONObject jsonData = JSONObject.parseObject(Base64.decodeStr(data));
                        JSONObject headJson = (JSONObject) jsonData.get("head");
                        //接口调用成功更新跨境商品库存
                        if ("success".equals(headJson.get("result_code").toString())) {
                            JSONObject bodyJson = (JSONObject) jsonData.get("body");
                            JSONObject detailJson = (JSONObject) bodyJson.get("detail");
                            if (detailJson != null) {
                                String goods_storage = detailJson.get("goods_storage").toString();
                                if (StrUtil.isNotEmpty(goods_storage)) {
                                    CrossBorderProduct updateCBProduct = new CrossBorderProduct();
                                    updateCBProduct.setId(crossBorderProduct.getId());
                                    updateCBProduct.setInventory_quantity(Integer.parseInt(goods_storage));
                                    crossBorderProductMapper.updateById(updateCBProduct);

                                    //刷新商品库存缓存
                                    InventoryCacheUtil.addCache(new ProductInventorySummary(updateCBProduct.getId(), null, PermissionManageHandler.PermissionSupport.PRODUCT_TYPE_BONDED.getPermission_id(), updateCBProduct.getInventory_quantity()));
                                }
                            }
                        }
                    }
                }
            }
        }
        Long end = System.currentTimeMillis();
        log.info("同步跨境商品库存花费时间为："+(end-start)+"ms");
    }

    public String createParams(String third_sku_id) {
        try {
            String xmlbegin = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<xml>\n";
            String gcbegin = "<gc>\n";
            String head = "<head>\n" +
                    "        <action>" + ThirdOrderStatusEnum.SELECT_GOODS + "</action>\n" +
                    "        <return_type>json</return_type>\n" +
                    "        <partner_id>" + ThirdOrderStatusEnum.PARTNER_ID + "</partner_id>\n" +
                    "</head>\n";
            String body = "<body>\n" +
                    "        <goods>\n" +
                    "        <serial>" + third_sku_id + "</serial>\n" +
                    "        </goods>\n" +
                    "</body>\n";
            String gcend = "</gc>";
            String xmlend = "</xml>";
            String appkey = "<appkey>" + ThirdOrderStatusEnum.APPKEY + "</appkey>";
            String gcStr = gcbegin + head + body + gcend;
            String sign = "<sign>" + DigestUtils.md5Hex(gcStr + appkey) + "</sign>\n";
            String params = URLEncoder.encode(Base64.encode(xmlbegin + gcStr + sign + xmlend), "UTF-8");
            log.info("签名参数：{}", gcStr + appkey);
            log.info("加密参数：{}", xmlbegin + gcStr + sign + xmlend);
            log.info("加密结果：{}", params);
            return params;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
