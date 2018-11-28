package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.sys.Code;
import com.hryj.entity.vo.sys.response.CodeInfoVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 李道云
 * @className: CodeMapper
 * @description: 代码mapper
 * @create 2018/6/23 8:48
 **/
@Component
public interface CodeMapper extends BaseMapper<Code> {

    /**
     * 根据代码类别查询代码列表
     * @param code_type
     * @return
     */
    List<CodeInfoVO> findCodeListByType(@Param("code_type") String code_type);
}
