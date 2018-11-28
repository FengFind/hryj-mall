package com.hryj.entity.bo.product;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.hryj.entity.vo.delegator.GenericConverter;
import com.hryj.entity.vo.product.audit.response.ProductBackupResponseVO;
import com.hryj.utils.UtilValidate;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品修改数据快照备份表
 *
 * @author daitingbo
 * @since 2018-06-28
 */
@Data
@TableName("p_product_backup")
public class ProductBackup extends Model<ProductBackup> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 商品id
     */
    private Long product_id;
    /**
     * 修改商品前的所有数据的JSON格式字符串
     */
    private String prod_data_before;
    /**
     * 修改商品前的所有数据的JSON格式字符串
     */
    private String prod_data_after;
    /**
     * 数据状态:是否已覆盖,1-已覆盖,0-未覆盖
     */
    private Integer data_status;
    /**
     * 处理状态:1-已处理,0-未处理
     */
    private Integer handle_status;
    /**
     * 门店仓库id
     */
    private Long party_id;
    /**
     * 数据类型:01-商品中心库,02-仓库或者门店
     */
    private String data_type;
    /**
     * 变更人id
     */
    private Long updated_by;

    /**
     * 变更人姓名
     */
    private String updated_user;

    /**
     * 变更时间
     */
    private Date update_time;

    @Override
    protected Serializable pkVal() {
          return this.id;
    }

    public ProductBackupResponseVO convertTo(GenericConverter<String> prod_type_getter, GenericConverter<Brand> brand_getter, GenericConverter<ProductGeo> geo_getter) {
        String prod_type_id_key = "product_type_id";
        String brand_key = "brand";
        String made_where_key = "made_where";

        JSONObject before = null;
        JSONObject after = null;
        if (UtilValidate.isNotEmpty(this.prod_data_before)) {
            before = JSON.parseObject(this.prod_data_before);
        }
        if (UtilValidate.isNotEmpty(this.prod_data_after)) {
            after = JSON.parseObject(this.prod_data_after);
        }

        ProductBackupResponseVO vo_backup = new ProductBackupResponseVO();

        if (before != null) {
            Long brand_id = before.getLong(brand_key);
            Long made_where_id = before.getLong(made_where_key);
            if (brand_id != null && brand_id > 0L) {
                Brand brand = brand_getter.convert(brand_id);
                if (brand != null) {
                    before.put(brand_key, brand.convertToProdBrand());
                }
            }
            if (made_where_id != null && made_where_id > 0L) {
                ProductGeo geo = geo_getter.convert(made_where_id);
                if (geo != null) {
                    before.put(made_where_key, geo.convertToMadeWhere());
                }
            }

            if (before.containsKey(prod_type_id_key)) {
                String product_type_id = before.getString(prod_type_id_key);
                if (UtilValidate.isNotEmpty(product_type_id)) {
                    String prod_type_name = prod_type_getter.convert(product_type_id);
                    vo_backup.setProduct_type_id(product_type_id);
                    vo_backup.setProduct_type_name(prod_type_name);
                }
            }

            this.prod_data_before = before.toJSONString();
        }

        if (after != null) {
            Long brand_id = after.getLong(brand_key);
            Long made_where_id = after.getLong(made_where_key);
            if (brand_id != null && brand_id > 0L) {
                Brand brand = brand_getter.convert(brand_id);
                if (brand != null) {
                    after.put(brand_key, brand.convertToProdBrand());
                }
            }
            if (made_where_id != null && made_where_id > 0L) {
                ProductGeo geo = geo_getter.convert(made_where_id);
                if (geo != null) {
                    after.put(made_where_key, geo.convertToMadeWhere());
                }
            }

            this.prod_data_after = after.toJSONString();
        }

        vo_backup.setProduct_backup_id(this.getId());
        vo_backup.setProduct_id(this.getProduct_id());
        vo_backup.setUpdated_by(this.getUpdated_by());
        vo_backup.setUpdated_user_name(this.getUpdated_user());
        vo_backup.setUpdated_time(DateUtil.formatDateTime(this.getUpdate_time()));
        vo_backup.setProcess_status(this.getHandle_status() == null || this.getHandle_status().intValue() == 0 ? false : true);
        vo_backup.setData_status(this.getData_status() == null || this.getData_status().intValue() == 0 ? false : true);
        vo_backup.setProd_data_before(this.getProd_data_before());
        vo_backup.setProd_data_after(this.getProd_data_after());
        return vo_backup;
    }
}
