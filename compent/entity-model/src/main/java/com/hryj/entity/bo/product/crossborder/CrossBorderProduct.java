package com.hryj.entity.bo.product.crossborder;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.hryj.entity.bo.product.ProductGeo;
import com.hryj.entity.vo.delegator.GenericConverter;
import com.hryj.entity.vo.product.crossborder.response.CrossBorderProductResponseVO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 王光银
 * @className: ProductTypeRole
 * @description: 商品类型数据权限
 * @create 2018/9/10 0010 16:23
 **/
@Data
@TableName("p_cross_border")
public class CrossBorderProduct extends Model<CrossBorderProduct> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;

    /**
     * 发货仓库
     */
    private String channel;

    /**
     *  第三方SKU ID
     */
    private String third_sku_id;

    /**
     * 净含量值
     */
    private BigDecimal unit_1;

    /**
     *  净含量单位
     */
    private String unit_2;

    /**
     * HSCODE
     */
    private String hs_code;

    /**
     *  库存
     */
    private Integer inventory_quantity;

    /**
     *  报关价
     */
    private BigDecimal declare_price;

    /**
     * 启运地ID
     */
    private Long shipment_from;

    /**
     * 创建时间
     */
    private Date create_time;

    /**
     * 更新时间
     */
    private Date update_time;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    public CrossBorderProductResponseVO convertTo(GenericConverter<String> channel_getter, GenericConverter<ProductGeo> geo_getter) {
        CrossBorderProductResponseVO vo = new CrossBorderProductResponseVO();
        vo.setChannel(this.getChannel());
        vo.setChannel_name(channel_getter.convert(this.channel));
        vo.setDeclare_price(this.getDeclare_price() == null ? "0.00" : this.getDeclare_price().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        vo.setHs_code(this.getHs_code());
        vo.setInventory_quantity(this.getInventory_quantity());
        vo.setThird_sku_id(this.getThird_sku_id());
        vo.setUnit_1(this.getUnit_1());
        vo.setUnit_2(this.getUnit_2());
        ProductGeo geo = geo_getter.convert(this.shipment_from);
        if (geo != null) {
            vo.setShipment_from(geo.convertToMadeWhere());
        }
        return vo;
    }
}
