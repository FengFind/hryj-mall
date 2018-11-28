package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.staff.dept.DeptShareConfig;
import com.hryj.entity.vo.staff.dept.request.DeptShareRequestVO;
import com.hryj.entity.vo.staff.dept.response.DeptShareListResponseVO;
import com.hryj.entity.vo.staff.dept.response.DeptStaffShareResPonseVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 组织节点分润配置(末级节点组织不参与分润) Mapper 接口
 *
 * @author daitingbo
 * @since 2018-07-02
 */
@Component
public interface DeptShareConfigMapper extends BaseMapper<DeptShareConfig> {
    /**
     * @author 代廷波
     * @description: 据门店或者仓库id + 组织id 查询分润员工(以门店或者仓库+组织的维度)
     * @param: null
     * @return
     * @create 2018/07/18 20:05
     **/
    DeptStaffShareResPonseVO getDeptStaffShare(DeptShareRequestVO vo);
    /**
     * @author 代廷波
     * @description: 根据门店或者仓库id 查询分润列表(以门店或者仓库维度)
     * @param: dept_id 门店或者仓库的id
     * @return java.util.List<com.hryj.entity.vo.staff.dept.response.DeptShareListResPonseVO>
     * @create 2018/07/18 20:57
     **/
    List<DeptShareListResponseVO> getDeptShareList(@Param("dept_id") Long dept_id);
}
