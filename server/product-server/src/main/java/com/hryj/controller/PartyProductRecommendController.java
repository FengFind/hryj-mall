package com.hryj.controller;

import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.product.partyprod.request.IdRequestVO;
import com.hryj.entity.vo.product.partyprod.request.PartyIdRequestVO;
import com.hryj.entity.vo.promotion.recommend.request.CopyRecommendProdToOtherPartyRequestVO;
import com.hryj.entity.vo.promotion.recommend.request.PartyRecommendProdAppendRequestVO;
import com.hryj.entity.vo.promotion.recommend.response.PartyRecommendProductItemResponseVO;
import com.hryj.exception.ServerException;
import com.hryj.service.PartyRecommendProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 王光银
 * @className: RecommendController
 * @description: 门店或仓库的推荐商品接口，开放给后台管理
 * @create 2018/6/27 0027 20:49
 **/
@RestController
@RequestMapping("/partyProductRecommendMgr")
public class PartyProductRecommendController {

    @Autowired
    private PartyRecommendProductService partyRecommendProductService;

    /**
     * @author 王光银
     * @methodName: findPartyRecommendProductList
     * @methodDesc: 查询返回指定门店或仓库的推荐商品
     * @description: 该查询不需要进行分页, 返回数据按是否置顶以及更新时间，创建时间进行排序
     * @param: [partyIdRequestVO]
     * @return com.hryj.common.Result<java.util.List<com.hryj.entity.vo.promotion.recommend.response.PartyRecommendProductItemResponseVO>>
     * @create 2018-06-28 16:06
     **/
    @PostMapping("/findPartyRecommendProductList")
    public Result<ListResponseVO<PartyRecommendProductItemResponseVO>> findPartyRecommendProductList(
            @RequestBody PartyIdRequestVO partyIdRequestVO) throws ServerException {
        return partyRecommendProductService.findPartyRecommendProductList(partyIdRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: saveAppendManyPartyRecommendProduct
     * @methodDesc: 批量保存门店或仓库的推荐位商品
     * @description:
     * @param: [partyRecommendProdAppendRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-27 21:19
     **/
    @PostMapping("/saveAppendManyPartyRecommendProduct")
    public Result saveAppendManyPartyRecommendProduct(
            @RequestBody PartyRecommendProdAppendRequestVO partyRecommendProdAppendRequestVO) throws ServerException {
        return partyRecommendProductService.saveAppendManyPartyRecommendProduct(partyRecommendProdAppendRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: deleteOneFromPartyRecommendProduct
     * @methodDesc: 删除一个门店或仓库的推荐商品
     * @description: 如果指定的推荐商品不存在，直接返回成功
     * @param: [idRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-27 21:22
     **/
    @PostMapping("/deleteOneFromPartyRecommendProduct")
    public Result deleteOneFromPartyRecommendProduct(@RequestBody IdRequestVO idRequestVO) throws ServerException {
        return partyRecommendProductService.deleteOneFromPartyRecommendProduct(idRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: copyToOtherParty
     * @methodDesc: 复制一个门店或仓库的推荐商品到其他的门店或仓库
     * @description:  复制推荐商品时会排除掉复制目标门店或仓库没有销售的商品
     * @param: [copyRecommendProdToOtherPartyRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-27 21:35
     **/
    @PostMapping("/copyToOtherParty")
    public Result copyToOtherParty(@RequestBody CopyRecommendProdToOtherPartyRequestVO copyRecommendProdToOtherPartyRequestVO) throws ServerException {
        return partyRecommendProductService.copyToOtherParty(copyRecommendProdToOtherPartyRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: topPartyRecommendProduct
     * @methodDesc: 置顶广告位商品
     * @description: 置顶广告位时更新时间，有多个置顶商品按照更新时间排序，越早的排在前面
     * @param: [idRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-07-04 9:19
     **/
    @PostMapping("/topPartyRecommendProduct")
    public Result topPartyRecommendProduct(@RequestBody IdRequestVO idRequestVO) throws ServerException {
        return partyRecommendProductService.topPartyRecommendProduct(idRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: topPartyRecommendProduct
     * @methodDesc: 撤消置顶广告位商品
     * @description:
     * @param: [idRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-07-04 9:19
     **/
    @PostMapping("/untopPartyRecommendProduct")
    public Result untopPartyRecommendProduct(@RequestBody IdRequestVO idRequestVO) throws ServerException {
        return partyRecommendProductService.untopPartyRecommendProduct(idRequestVO);
    }
}
