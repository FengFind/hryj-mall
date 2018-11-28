package com.hryj.entity.vo.product.audit.response;

import com.hryj.entity.vo.product.response.ProdAttrItemResponseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: ProductBackupResponseVO
 * @description:
 * @create 2018/6/27 0027 12:03
 **/
@ApiModel(value = "商品审核数据VO", description = "商品审核数据VO")
@Data
public class ProductBackupResponseVO {

    @ApiModelProperty(value = "商品数据提交ID")
    private Long product_backup_id;

    @ApiModelProperty(value = "商品ID")
    private Long product_id;

    @ApiModelProperty(value = "商品类型ID")
    private String product_type_id;

    @ApiModelProperty(value = "商品类型名称")
    private String product_type_name;

    @ApiModelProperty(value = "备份数据的状态，true已生效，false未生效，这个字段表示新增或修改的商品快照数据的使用状态，不直接表示审核处理的结果，但是与审核处理结果是可以对应的，只有审核通过后数据才会生效")
    private Boolean data_status;

    @ApiModelProperty(value = "提交用户ID")
    private Long updated_by;

    @ApiModelProperty(value = "提交用户姓名")
    private String updated_user_name;

    @ApiModelProperty(value = "审核处理状态, true已经处理， false未处理，该字段标识一个商品的新增或商品的修改是否已经审核了，并不表示审核处理的结果")
    private Boolean process_status;

    @ApiModelProperty(value = "修改时间，格式: yyyy-MM-dd HH:mm:ss")
    private String updated_time;

    @ApiModelProperty(value = "修改商品前的数据备份,JSON格式的字符串，prod_data_after字段也是一样，但为方便比较使用，这两个JSON字段的数据中，字段数量保证一致，并且顺序也一致，顺序主要是用在属性比对，新增的情况除外，新增只有一个字段有值", notes = "JSON:{product_id:'',product_name:'',prod_cate_id:'', prod_cate_name:'', brand_name:'',made_where:'',shelf_life:'',specification:'',integral:'',comments:'',long_description:'',list_image_url:'',detail_image_url_one:'',detail_image_url_two:'',detail_image_url_three:'',detail_image_url_four:'',detail_image_url_five:'',cost_price:'',erp_code:'',introduction_date:'',sales_end_date:'',forbid_sale_flag:'',up_down_status:'',attr_list:[{attr_type:'',attr_name:'',attr_value:'',prod_cate_attr_id:'',prod_cate_attr_item_id:''},{attr_type:'',attr_name:'',attr_value:'',prod_cate_attr_id:'',prod_cate_attr_item_id:''}]}")
    private String prod_data_before;

    @ApiModelProperty(value = "修改商品后的数据备份")
    private String prod_data_after;

    @ApiModelProperty(value = "商品的历史审核记录")
    private List<ProductAuditResponseVO> audit_record_list;

    @ApiModelProperty(value = "分类属性集合")
    private List<ProdAttrItemResponseVO> category_attr_list;

    @ApiModelProperty(value = "自定义商品属性集合")
    private List<ProdAttrItemResponseVO> attr_list;
}
