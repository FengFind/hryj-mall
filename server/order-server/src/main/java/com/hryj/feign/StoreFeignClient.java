package com.hryj.feign;

import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.staff.dept.request.DeptIdRequestVO;
import com.hryj.entity.vo.staff.dept.request.DeptIdsRequestVO;
import com.hryj.entity.vo.staff.dept.response.DeptIdByStoreOrWarehouseResponseVO;
import com.hryj.exception.GlobalException;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 罗秋涵
 * @className: ProductFeignClient
 * @description:
 * @create 2018/7/6 0006 17:50
 **/
@FeignClient(name = "staff-server")
public interface StoreFeignClient {

    /**
     * @author 罗秋涵
     * @description: 获取门店信息
     * @param: [productValidateRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.product.common.response.ProductValidateResponseVO>
     * @create 2018-07-12 20:43
     **/
    @RequestMapping("/app/dept/getAppDeptIdByStoreOrWarehouseDet")
    Result<DeptIdByStoreOrWarehouseResponseVO> getDeptIdByStoreOrWarehouseDet(@RequestBody DeptIdRequestVO vo) throws GlobalException ;

    /**
     * @author 罗秋涵
     * @description: app 根据组织ids获取门店或者仓库查询详情
     * @param: vo
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.dept.response.DeptIdByStoreOrWarehouseResponseVO>
     * @create 2018/07/12 21:56
     **/
    @PostMapping("/app/dept/getAppDeptIdsByStoreOrWarehouseDet")
    Result<ListResponseVO<DeptIdByStoreOrWarehouseResponseVO>> getAppDeptIdsByStoreOrWarehouseDet(@RequestBody DeptIdsRequestVO vo);



}
