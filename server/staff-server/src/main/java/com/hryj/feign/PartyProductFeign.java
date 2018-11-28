package com.hryj.feign;

import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.product.partyprod.request.PartyProductStatisticsRequestVO;
import com.hryj.entity.vo.product.partyprod.response.PartyProductStatisticsItem;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author 王光银
 * @className: PartyProductFeign
 * @description:
 * @create 2018/7/11 0011 23:15
 **/
@FeignClient(name = "product-server")
public interface PartyProductFeign {

    @RequestMapping(value = "/partyProductMgr/partyProdSimpleStatistics", method = RequestMethod.POST)
    Result<ListResponseVO<PartyProductStatisticsItem>> partyProdSimpleStatistics(@RequestBody PartyProductStatisticsRequestVO partyProductStatisticsRequestVO);
}
