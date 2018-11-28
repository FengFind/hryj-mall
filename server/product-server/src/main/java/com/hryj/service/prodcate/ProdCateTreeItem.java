package com.hryj.service.prodcate;

import com.hryj.entity.vo.product.category.response.ProductCategoryItemResponseVO;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: ProdCateTreeItem
 * @description:
 * @create 2018/8/15 0015 11:05
 **/
@Data
public class ProdCateTreeItem {

    private Long category_id;

    private String category_name;

    private String image_url;

    private Long parent_id;

    private Integer prod_num = 0;

    private List<ProdCateTreeItem> son_list;

    public ProductCategoryItemResponseVO convertTo() {
        ProductCategoryItemResponseVO vo = new ProductCategoryItemResponseVO();
        vo.setCategory_id(this.category_id);
        vo.setImage_url(this.image_url);
        vo.setCategory_name(this.category_name);
        return vo;
    }
}
