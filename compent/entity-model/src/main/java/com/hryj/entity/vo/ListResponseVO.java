package com.hryj.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 李道云
 * @className: ListResponseVO
 * @description: 列表查询响应VO
 * @create 2018/7/2 16:27
 **/
@ApiModel(value = "列表查询响应VO")
public class ListResponseVO<T> implements Serializable {

    private static final long serialVersionUID = 4267841769765066754L;

    @ApiModelProperty(value = "查询结果集", required = true)
    private List<T> records = new ArrayList<>();

    public ListResponseVO() {

    }

    public ListResponseVO(List<T> records) {
        if(records != null){
            this.records = records;
        }
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        if(records != null){
            this.records = records;
        }
    }
}
