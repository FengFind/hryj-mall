package com.hryj.feign;

import com.hryj.common.Result;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author 王光银
 * @className: ProductFeignClient
 * @description: 商品品牌、产地缓存刷新
 * @create 2018-10-08 10:16
 **/
@FeignClient(name = "product-server")
public interface ProductFeignClient {

    /**
     * 刷新商品品牌缓存
     * @return
     */
    @RequestMapping(value = "/cache/refresh/refreshProductBrand", method = RequestMethod.POST)
    Result refreshProductBrandCache();

    /**
     * 刷新商品产地缓存
     * @return
     */
    @RequestMapping(value = "/cache/refresh/refreshProductGeo", method = RequestMethod.POST)
    Result refreshProductGeoCache();

}
