package com.hryj.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 李道云
 * @className: PageResponseVO
 * @description: 分页查询响应VO
 * @create 2018/6/29 15:08
 **/
@ApiModel(value = "分页查询响应VO")
public class PageResponseVO<T> implements Serializable {

    private static final long serialVersionUID = 3267841769765066754L;

    public PageResponseVO() {}

    public PageResponseVO(Long total_count, Long total_page) {
        this.total_count = total_count;
        this.total_page = total_page;
    }

    public PageResponseVO(Long total_count, Long total_page, List<T> data_list) {
        this.total_count = total_count;
        this.total_page = total_page;
        this.records = data_list;
    }

    @ApiModelProperty(value = "总记录数", required = true)
    private Long total_count = 0L;

    @ApiModelProperty(value = "总页数", required = true)
    private Long total_page = 0L;

    @ApiModelProperty(value = "查询结果集", required = true)
    private List<T> records = new ArrayList<>();


    public Long getTotal_count() {
        return total_count;
    }

    public void setTotal_count(Long total_count) {
        this.total_count = total_count;
    }

    public Long getTotal_page() {
        return total_page;
    }

    public void setTotal_page(Long total_page) {
        this.total_page = total_page;
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
