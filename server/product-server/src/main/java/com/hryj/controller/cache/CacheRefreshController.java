package com.hryj.controller.cache;

import com.hryj.cache.ProductBrandCacheHandler;
import com.hryj.cache.ProductGeoCacheHandler;
import com.hryj.common.Result;
import com.hryj.exception.ServerException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 王光银
 * @className: CacheRefreshController
 * @description:
 * @create 2018/10/8 0008 10:03
 **/
@RestController
@RequestMapping("/cache/refresh")
public class CacheRefreshController {

    /**
     * 刷新商品品牌缓存
     * @return
     * @throws ServerException
     */
    @PostMapping("/refreshProductBrand")
    public Result refreshProductBrandCache() throws ServerException {
        ProductBrandCacheHandler.cacheInit();
        return Result.getDefaultSuccessResult();
    }

    /**
     * 刷新商品产地缓存
     * @return
     * @throws ServerException
     */
    @PostMapping("/refreshProductGeo")
    public Result refreshProductGeoCache() throws ServerException {
        ProductGeoCacheHandler.cacheInit();
        return Result.getDefaultSuccessResult();
    }
}
