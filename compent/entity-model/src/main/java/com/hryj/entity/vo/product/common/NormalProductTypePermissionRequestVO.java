package com.hryj.entity.vo.product.common;

import com.hryj.constant.CommonConstantPool;
import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.delegator.GenericConverter;
import com.hryj.utils.UtilValidate;

import java.util.List;

/**
 * @author 王光银
 * @className: NormalProductTypePermissionRequestVO
 * @description:
 * @create 2018/9/13 0013 16:42
 **/
public abstract class NormalProductTypePermissionRequestVO extends RequestVO implements ProductTypePermissionProcess {

    public abstract String getProduct_type_id();

    public abstract String setProduct_type_id(String product_type_id);

    @Override
    public List<String> getPermission_list(GenericConverter<List<String>> getter) {
        List<String> permission_list = getter.convert(this.getLogin_token());
        if (UtilValidate.isEmpty(permission_list)) {
            return null;
        }

        if (UtilValidate.isNotEmpty(this.getProduct_type_id())
                && !CommonConstantPool.STR_ALL.equals(this.getProduct_type_id())
                && !permission_list.contains(this.getProduct_type_id())) {
            return null;
        }

        if (UtilValidate.isEmpty(this.getProduct_type_id())) {
            this.setProduct_type_id(CommonConstantPool.STR_ALL);
        }

        return permission_list;
    }
}
