package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.vo.profit.response.DataQueryResponseVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * @author 李道云
 * @className: DataQueryMapper
 * @description: 数据查询mapper
 * @create 2018/7/7 14:06
 **/
@Component
public interface DataQueryMapper extends BaseMapper {

    /**
     * @author 李道云
     * @methodName: findPersonalData
     * @methodDesc:  查询个人数据
     * @description:
     * @param: [staff_id, start_date, end_date]
     * @return com.hryj.entity.vo.profit.response.DataQueryResponseVO
     * @create 2018-07-07 14:33
     **/
    DataQueryResponseVO findPersonalData(@Param("staff_id") Long staff_id, @Param("start_date") String start_date, @Param("end_date") String end_date);

    /**
     * @author 李道云
     * @methodName: findStoreTeamData
     * @methodDesc:  查询门店团队数据
     * @description:
     * @param: [store_id, start_date, end_date]
     * @return com.hryj.entity.vo.profit.response.DataQueryResponseVO
     * @create 2018-07-07 14:33
     **/
    DataQueryResponseVO findStoreTeamData(@Param("store_id") Long store_id, @Param("start_date") String start_date, @Param("end_date") String end_date);

    /**
     * @author 李道云
     * @methodName: findDeptTeamData
     * @methodDesc:  查询部门团队数据
     * @description:
     * @param: [dept_path, start_date, end_date]
     * @return com.hryj.entity.vo.profit.response.DataQueryResponseVO
     * @create 2018-07-07 14:33
     **/
    DataQueryResponseVO findDeptTeamData(@Param("dept_path") String dept_path, @Param("start_date") String start_date, @Param("end_date") String end_date);

    /**
     * @author 李道云
     * @methodName: findWhTeamData
     * @methodDesc:  查询仓库团队数据
     * @description:
     * @param: [store_id, start_date, end_date]
     * @return com.hryj.entity.vo.profit.response.DataQueryResponseVO
     * @create 2018-07-07 14:33
     **/
    DataQueryResponseVO findWhTeamData(@Param("wh_id") Long wh_id, @Param("start_date") String start_date, @Param("end_date") String end_date);
}
