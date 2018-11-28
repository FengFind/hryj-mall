package com.hryj.entity.bo.sys;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 李道云
 * @className: CodeType
 * @description: 代码类型
 * @create 2018/6/23 8:46
 **/
@Data
@TableName("sys_code_type")
public class CodeType extends Model<CodeType> {

    @TableId(value="id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 代码类型
     */
    private String code_type;
    /**
     * 代码类型名称
     */
    private String code_type_name;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
