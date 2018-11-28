package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.payment.PaymentGroup;
import com.hryj.entity.bo.payment.PaymentGroupOrder;
import com.hryj.entity.vo.payment.PaymentGroupInfo;
import com.hryj.entity.vo.payment.PaymentGroupResponseVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 李道云
 * @className: PaymentGroupMapper
 * @description: 支付组mapper
 * @create 2018/7/10 18:14
 **/
@Component
public interface PaymentGroupMapper extends BaseMapper<PaymentGroup> {

    PaymentGroupInfo getPaymentGroupInfoByOrderId(@Param("order_id") Long order_id, @Param("pay_status") String pay_status);

    /**
     * 查询支付组明细
     * @param pay_group_sn
     * @return
     */
    List<PaymentGroupOrder> getRecordByGroupId(String pay_group_sn);

    /**
     * 根据编号查询支付组
     * @param pay_group_sn
     * @return
     */
    PaymentGroupResponseVO getPaymentGroupInfo(String pay_group_sn);
}
