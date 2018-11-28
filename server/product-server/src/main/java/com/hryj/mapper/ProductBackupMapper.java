package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hryj.entity.bo.product.ProductBackup;
import com.hryj.entity.vo.product.audit.mapping.ProductAuditPageMappingItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author 王光银
 * @className: ProductBackupMapper
 * @description:
 * @create 2018/7/2 0002 22:12
 **/
@Component
public interface ProductBackupMapper extends BaseMapper<ProductBackup> {

    /**
     * @author 王光银
     * @methodName: selectProdAuditPage
     * @methodDesc: 分页查询商品审核
     * @description:
     * @param:
     * @return
     * @create 2018-07-13 15:20
     **/
    List<ProductAuditPageMappingItem> selectProdAuditPage(Map<String, Object> params_map, Page page_condition);
}
