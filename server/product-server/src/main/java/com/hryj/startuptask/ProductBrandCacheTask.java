package com.hryj.startuptask;

import com.hryj.cache.ProductBrandCacheHandler;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * @author 王光银
 * @className: ProductTypeCacheTask
 * @description: 商品类型缓存任务
 * @create 2018/9/12 0012 10:07
 **/
@Component
public class ProductBrandCacheTask implements ApplicationRunner, Ordered {

    @Override
    public int getOrder() {
        return 4;
    }

    @Override
    public void run(ApplicationArguments applicationArguments) {
        ProductBrandCacheHandler.cacheInit();
    }
}
