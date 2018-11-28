package com.hryj.entity.bo.sys;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 李道云
 * @className: Code
 * @description: 代码表
 * @create 2018/6/23 8:40
 **/
@Data
@TableName("sys_code")
public class Code extends Model<Code> {

    @TableId(value="id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 代码类型
     */
    private String code_type;
    /**
     * 代码key
     */
    private String code_key;
    /**
     * 代码值
     */
    private String code_value;
    /**
     * 代码名称
     */
    private String code_name;
    /**
     * 代码状态:1-有效,0-无效
     */
    private boolean code_status;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
