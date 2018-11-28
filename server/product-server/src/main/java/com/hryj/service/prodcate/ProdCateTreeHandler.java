package com.hryj.service.prodcate;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author 王光银
 * @className: ProdCateTreeHandler
 * @description:
 * @create 2018/8/15 0015 11:04
 **/
@Data
public class ProdCateTreeHandler {

    private List<ProdCateTreeItem> tree_list;

    private Map<Long, ProdCateTreeItem> map_data;

    private boolean has_no_data;
}
