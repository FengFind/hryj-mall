package com.hryj.entity.bo.declare;

import com.baomidou.mybatisplus.annotations.TableId;
import lombok.Data;

/**
 * @author 白飞
 * @className: BaseEntity
 * @description:
 * @create 2018/9/26 10:06
 **/
@Data
public class BaseEntity {

    /** ID 主键*/
    @TableId(value = "id")
    private  Long id;


}
