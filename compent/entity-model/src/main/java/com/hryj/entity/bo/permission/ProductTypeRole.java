package com.hryj.entity.bo.permission;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 王光银
 * @className: ProductTypeRole
 * @description:
 * @create 2018/9/10 0010 9:13
 **/
@Data
@TableName("p_product_type_role")
public class ProductTypeRole extends Model<ProductTypeRole> {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;

    private Long staff_id;

    private Long role_id;

    private String product_type_id;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
