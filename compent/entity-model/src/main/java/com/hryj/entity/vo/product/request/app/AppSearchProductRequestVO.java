package com.hryj.entity.vo.product.request.app;

import com.hryj.constant.CommonConstantPool;
import com.hryj.entity.vo.PageRequestVO;
import com.hryj.utils.UtilValidate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: AppSearchProductRequestVO
 * @description:
 * @create 2018/6/28 0028 20:19
 **/
@ApiModel(value = "APP端商品搜素请求VO", description = "APP端商品搜素请求VO")
@Data
public class AppSearchProductRequestVO extends PageRequestVO {

    public static void main(String[] args) {
        System.out.println(new AppSearchProductRequestVO().hashCode());
    }

    @ApiModelProperty(value = "门店ID，缺省使用用户的默认门店ID")
    private Long party_id;

    @ApiModelProperty(value = "商品分类ID")
    private Long category_id;

    @ApiModelProperty(value = "商品分类名称")
    private String category_name;

    @ApiModelProperty(value = "搜素关键字，目前只对商品的名称做前后模糊匹配")
    private String search_key;

    @ApiModelProperty(value = "用户ID， 门店端需要传此参数， 用户端请忽略此参数")
    private Long user_id;

    @ApiModelProperty(value = "商品类型ID， all全部, new_retail新零售商品， bonded跨境商品，缺省默认: all")
    private String product_type_id;

    @Override
    public int hashCode() {
        int party_id_hash = (party_id == null ? -1 : party_id.hashCode());
        int category_id_hash = (category_id == null ? -1 : category_id.hashCode());
        int category_name_hash = UtilValidate.isEmpty(category_name) ? -1 : category_name.hashCode();
        int search_key_hash = UtilValidate.isEmpty(search_key) ? -1 : search_key.hashCode();
        int page_num_hash = this.getPage_num() == null ? -1 : this.getPage_num().hashCode();
        int page_size_hash = this.getPage_size() == null ? -1 : this.getPage_size().hashCode();
        int product_type_id_hash = UtilValidate.isEmpty(this.product_type_id) ? CommonConstantPool.STR_ALL.hashCode() : this.product_type_id.hashCode();
        return party_id_hash + category_id_hash + category_name_hash + search_key_hash + page_num_hash + page_size_hash + product_type_id_hash;
    }

    public void checkPageNum(Integer min_page_num) {
        if (this.getPage_num() == null || this.getPage_num().intValue() < min_page_num.intValue()) {
            this.setPage_num(min_page_num);
        }
    }

    public void checkPageSize(Integer min_page_size, Integer max_page_size) {
        if (this.getPage_size() == null || this.getPage_size().intValue() < min_page_size.intValue()) {
            this.setPage_size(min_page_size);
            return;
        }

        if (this.getPage_size().intValue() > max_page_size.intValue()) {
            this.setPage_size(max_page_size);
        }
    }
}
