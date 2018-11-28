package com.hryj.controller;

import com.hryj.common.Result;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.product.audit.request.ProductBackupRequestVO;
import com.hryj.entity.vo.product.audit.request.ProductHandledResultSubmitRequestVO;
import com.hryj.entity.vo.product.audit.request.SearchProductAuditRequestVO;
import com.hryj.entity.vo.product.audit.response.ProductAuditPageItemResponseVO;
import com.hryj.entity.vo.product.audit.response.ProductBackupResponseVO;
import com.hryj.exception.ServerException;
import com.hryj.service.ProductBackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 王光银
 * @className: ProductAuditController
 * @description: 商品审核管理接口，开放给后台系统
 * @create 2018/6/26 0026 17:18
 **/
@RestController
@RequestMapping("/productAuditMgr")
public class ProductAuditController {

    @Autowired
    private ProductBackupService productBackupService;

    /**
     * @author 王光银
     * @methodName: searchProductAuditByPage
     * @methodDesc: 分页查询商品审核管理数据
     * @description:
     * @param: [productAuditSearchRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.product.audit.response.ProductAuditPageItemResponseVO>>
     * @create 2018-06-30 9:16
     **/
    @PostMapping("/findProductAuditByPage")
    public Result<PageResponseVO<ProductAuditPageItemResponseVO>> searchProductAuditByPage(
            @RequestBody SearchProductAuditRequestVO productAuditSearchRequestVO) throws ServerException {
        return productBackupService.searchProductAuditByPage(productAuditSearchRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: getOneProductBackupData
     * @methodDesc: 查询返回一个商品的快照数据
     * @description:
     * @param: [productBackupRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.product.audit.response.ProductBackupResponseVO>
     * @create 2018-06-27 20:25
     **/
    @PostMapping("/getOneProductAuditData")
    public Result<ProductBackupResponseVO> getOneProductBackupData(
            @RequestBody ProductBackupRequestVO productBackupRequestVO) throws ServerException {
        return productBackupService.getOneProductBackupData(productBackupRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: submitProductAuditResult
     * @methodDesc: 提交商品审核处理结果
     * @description: 审核通过时，修改后的商品数据备份会覆盖到商品库中
     * @param: [productHandledResultSubmitRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-27 20:25
     **/
    @PostMapping("/submitProductAuditResult")
    public Result submitProductAuditResult(
            @RequestBody ProductHandledResultSubmitRequestVO productHandledResultSubmitRequestVO) throws ServerException {
        return productBackupService.submitProductAuditResult(productHandledResultSubmitRequestVO);
    }
}
