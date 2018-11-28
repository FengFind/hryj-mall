package com.hryj.entity.vo.user.request;

import com.hryj.entity.vo.PageRequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: UserListRequestVO
 * @description: 用户列表查询请求VO
 * @create 2018/6/27 22:02
 **/
@Data
@ApiModel(value="用户列表查询请求VO")
public class UserListRequestVO extends PageRequestVO {

    @ApiModelProperty(value = "手机号码")
    private String phone_num;

    @ApiModelProperty(value = "用户姓名")
    private String user_name;

    @ApiModelProperty(value = "开始日期", notes = "日期格式:yyyy-MM-dd HH:mm:ss")
    private String start_date;

    @ApiModelProperty(value = "截止日期", notes = "日期格式:yyyy-MM-dd HH:mm:ss")
    private String end_date;
}
