package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hryj.entity.bo.order.OrderInfo;
import com.hryj.entity.vo.order.DistributionInfoVO;
import com.hryj.entity.vo.order.ReturnOrderDetailsRequestVO;
import com.hryj.entity.vo.order.ReturnProductVO;
import com.hryj.entity.vo.order.ReturnVO;
import com.hryj.entity.vo.order.request.DistributionForStoreRequestVO;
import com.hryj.entity.vo.order.request.DistributionRequestVO;
import com.hryj.entity.vo.order.request.OrderListRequestVO;
import com.hryj.entity.vo.order.request.ReturnOrderListRequestVO;
import com.hryj.entity.vo.order.response.HistoricalOrderListResponseVO;
import com.hryj.entity.vo.order.response.ReturnOrderDetailsResponseVO;
import com.hryj.entity.vo.order.response.SelfPickResponseVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 罗秋涵
 * @className: OrderForStoreMapper
 * @description:
 * @create 2018/7/5 0005 21:47
 **/
@Component
public interface OrderForStoreMapper extends BaseMapper<OrderInfo> {

    
    /**
     * 查询各状态发货，取货记录(店长)
     * @param distributionForStoreRequestVO
     * @return
     */
    List<DistributionInfoVO> findDistributionListForBuinour(@Param("VO") DistributionForStoreRequestVO distributionForStoreRequestVO, @Param("dept_id") Long dept_id);

    /**
     * 查询各状态发货，取货记录(店员)
     * @param distributionForStoreRequestVO
     * @return
     */
    List<DistributionInfoVO> findDistributionListForStaff(@Param("VO")DistributionForStoreRequestVO distributionForStoreRequestVO, @Param("staff_id") Long staff_id);

    /**
     * 根据自提码查询自提信息
     * @param self_pick_code
     * @return
     */
    SelfPickResponseVO getSelfPickRecord(String self_pick_code);

    /**
     * 根据电话号码获取自提列表
     * @param phone_num
     * @return
     */
    List<SelfPickResponseVO> findUserSelfPickList(@Param("dept_id")Long dept_id,@Param("phone_num") String phone_num,@Param("keyValue") String keyValue);

    /**
     * 获取待分配退货列表
     * @param dept_id
     * @return
     */
    List<ReturnVO> findReturnListForUnallocated(@Param("return_status") String return_status,@Param("dept_id") Long dept_id,Page page);

    /**
     * 查询退货单类别
     * @param staff_id
     * @return
     */
    List<ReturnVO> findReturnListForStaff(@Param("VO") ReturnOrderListRequestVO returnOrderListRequestVO, @Param("staff_id") Long staff_id,Page page);

    /**
     * 查询门店中，已完成，超时的配送单列表
     * @param distributionForStoreRequestVO
     * @return
     */
    List<DistributionInfoVO> findDistributionListForBuinourByStatus(@Param("VO")DistributionForStoreRequestVO distributionForStoreRequestVO, @Param("dept_id")Long dept_id,Page page);

    /**
     * 获取配员工送单列表
     * @param distributionRequestVO
     * @return
     */
    List<DistributionInfoVO> findDistributionStaff(DistributionRequestVO distributionRequestVO,Page page);

    /**
     * 获取员工已完成配送单
     * @param distributionRequestVO
     * @return
     */
    List<DistributionInfoVO> findDistributionStaffForOver(@Param("VO") DistributionRequestVO distributionRequestVO,@Param("distribution_status") String distribution_status,Page page);

    /**
     * 查询已取货的退货点
     * @param dept_id
     * @return
     */
    List<ReturnVO> findReturnListForAllot(@Param("dept_id") Long dept_id,Page page);


    /**
     * 门店点端查询代下单订单列表
     * @param orderListRequestVO
     * @return
     */
    List<HistoricalOrderListResponseVO> findWaitPayOrderList(OrderListRequestVO orderListRequestVO);

    /**
     * 门店点端查询代下单订单列表
     * @param orderListRequestVO
     * @param page
     * @return
     */
    List<HistoricalOrderListResponseVO> findOrderListByStatus(OrderListRequestVO orderListRequestVO, Page page);

    /**
     * 查询创退货单
     * @param returnOrderListRequestVO
     * @param id
     * @return
     */
    List<ReturnVO> findReturnListForWarehouse(@Param("VO") ReturnOrderListRequestVO returnOrderListRequestVO, @Param("party_id") Long id,Page page);

    /**
     * 获取提货单详情
     * @param returnOrderDetailsRequestVO
     * @return
     */
    ReturnOrderDetailsResponseVO getReturnDetail(ReturnOrderDetailsRequestVO returnOrderDetailsRequestVO);

    /**
     * 获取退货单商品信息
     * @param returnOrderDetailsRequestVO
     * @return
     */
    List<ReturnProductVO> findReturnProductList(ReturnOrderDetailsRequestVO returnOrderDetailsRequestVO);
}
