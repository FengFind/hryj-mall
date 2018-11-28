package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.product.ProductCategory;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * @author 王光银
 * @className: ProductCategoryMapper
 * @description: 商品分类mapper
 * @create 2018/6/29 0029 9:05
 **/
@Component
public interface ProductCategoryMapper extends BaseMapper<ProductCategory> {

    /**
     * @author 王光银
     * @methodName: 根据条件查询最大的排序号
     * @methodDesc:
     * @description:
     * @param: 
     * @return 
     * @create 2018-07-11 14:46
     **/
    Integer selectMaxNumByCondition(@Param("parent_cate_id") Long parent_cate_id);
}
