package com.hryj.entity.vo.product.common;

import com.hryj.entity.vo.delegator.GenericConverter;

import java.util.List;

/**
 * @author 王光银
 * @className: ProductTypePermissionProcess
 * @description:
 * @create 2018/9/13 0013 16:45
 **/
public interface ProductTypePermissionProcess {

    List<String> getPermission_list(GenericConverter<List<String>> getter);

}
