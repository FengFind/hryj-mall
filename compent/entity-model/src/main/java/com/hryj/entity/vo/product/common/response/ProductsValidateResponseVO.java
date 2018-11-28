package com.hryj.entity.vo.product.common.response;

import com.hryj.utils.UtilValidate;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 王光银
 * @className: ProductsValidateResponseVO
 * @description:
 * @create 2018/7/19 0019 21:54
 **/
@Data
public class ProductsValidateResponseVO {

    private static final String DEFAULT_FAILED_MSG = "商品已下架";

    @ApiModelProperty(value = "该参数取决于验证请求，如果验证请求中传了该参数，原样返回")
    private String follow_value;

    @ApiModelProperty(value = "商品验证结果条目集合")
    private List<ProductValidateResponseItem> prod_validate_result_list;

    @ApiModelProperty(value = "有效商品数量")
    private Integer valid_prod_num;

    @ApiModelProperty(value = "无效商品数量")
    private Integer invalid_prod_num;

    /**
     * @author 王光银
     * @methodName: isValidatePassed
     * @methodDesc: 返回验证是否通过， 只有提交验证的商品全部都通过验证时才返回 true，其他情况返回false
     * @description:
     * @param:
     * @return
     * @create 2018-08-07 16:05
     **/
    public Boolean isValidatePassed() {
        if (UtilValidate.isEmpty(prod_validate_result_list)) {
            return false;
        }
        return prod_validate_result_list.size() == valid_prod_num.intValue();
    }

    /**
     * @author 王光银
     * @methodName: getItem
     * @methodDesc: 根据 party_id, product_id, activity_id 返回对应商品的验证结果
     * @description:
     * @param:
     * @return
     * @create 2018-08-07 16:04
     **/
    public ProductValidateResponseItem getItem(Long party_id, Long product_id, Long activity_id) {
        if (UtilValidate.isEmpty(this.prod_validate_result_list)) {
            return null;
        }
        ProductValidateResponseItem tmp = new ProductValidateResponseItem(party_id, product_id, activity_id);
        for (ProductValidateResponseItem item : prod_validate_result_list) {
            if (tmp.equals(item)) {
                return item;
            }
        }
        return null;
    }

    /**
     * @author 王光银
     * @methodName: getInvalid
     * @methodDesc: 返回验证结果中无效的商品验证结果集合
     * @description:
     * @param:
     * @return
     * @create 2018-08-07 16:03
     **/
    public List<ProductValidateResponseItem> getInvalid() {
        if (invalid_prod_num == null || invalid_prod_num.intValue() <= 0) {
            return null;
        }
        List<ProductValidateResponseItem> tmpList = new ArrayList<>(invalid_prod_num);
        for (ProductValidateResponseItem item : prod_validate_result_list) {
            if (!item.getIs_valid()) {
                tmpList.add(item);
            }
        }
        return tmpList;
    }

    /**
     * @author 王光银
     * @methodName:  getItemByFlowValue
     * @methodDesc: 根据调用接口时传的  flow_value 值返回对应的商品验证结果条目
     * @description:
     * @param:
     * @return
     * @create 2018-08-07 16:02
     **/
    public ProductValidateResponseItem getItemByFlowValue(String flow_value) {
        if (UtilValidate.isEmpty(this.prod_validate_result_list) || UtilValidate.isEmpty(flow_value)) {
            return null;
        }
        for (ProductValidateResponseItem item : prod_validate_result_list) {
            if (flow_value.equals(item.getFollow_value())) {
                return item;
            }
        }
        return null;
    }

    /**
     * 返回所有未通过验证商品的失败信息
     * @return
     */
    public String getFailedMsg() {
        List<ProductValidateResponseItem> failed_item_list = getInvalid();
        if (UtilValidate.isEmpty(failed_item_list)) {
            return null;
        }
        StringBuilder failed_msg = new StringBuilder();
        for (ProductValidateResponseItem item : failed_item_list) {
            failed_msg.append(item.getOther_comments()).append(" | ");
        }
        return failed_msg.length() > 0 ? failed_msg.delete(failed_msg.length() - 3, failed_msg.length()).toString() : DEFAULT_FAILED_MSG;
    }

    /**
     * 返回第一个未通过验证的商品的失败原因
     * @return
     */
    public String getFirstFailedMsg() {
        List<ProductValidateResponseItem> failed_item_list = getInvalid();
        if (UtilValidate.isEmpty(failed_item_list)) {
            return null;
        }
        String error_msg = failed_item_list.get(0).getOther_comments();
        return UtilValidate.isEmpty(error_msg) ? DEFAULT_FAILED_MSG : error_msg;
    }
}
