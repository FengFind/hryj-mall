package com.hryj.constant;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 代廷波
 * @className: StaffConstants
 * @description:
 * @create 2018/7/2 0002-17:32
 **/
public class StaffConstants {
    /**
     * 分润比例常量 主要用于部门分润比例,  总的分润比例最大为100
     */
    public static final BigDecimal DEFAULT_MAX_SHARE_RATIO = new BigDecimal("100");

    /**
     * 默认BigDecimal类型0.00值,这里定义的只是一个0.00值,没有定义业务逻辑
     */
    public static final BigDecimal DEFAULT_BIG_ZERO_NO =new BigDecimal("0.00");

    /**
     * 分润校验提信息
     */
    public static Map<Integer, String> SHARE_ERROR_MSG = new HashMap<Integer, String>() {{
        put(1, "员工id为空");
        put(2, "分润比例为空");
        put(3, "节点部门id为空");
        put(4, "分润比例为无效,最多两位小数");
    }};

    /**
     * 高德地图kdy
     */
    public static final String AMAPKEY = "d78e944e3b53ef5c694918c4da3dd90b";
    /**
     * 高德地图周边收缩 url
     */
    public static final String AMAP_AROUND_URL = "https://restapi.amap.com/v3/place/around";
    /**
     * 高德地图行政搜索 url
     */
    public static final String AMAP_DISTRICT_URL = "https://restapi.amap.com/v3/config/district";

    /**
     * 正则表达式
     * 一到两位数字有效或者一到两位数字+一到两们小数
     * 最大99.99 最小0.00
     * 1.([1-9]{1}[0-9]{0,1}):验证1-99数字
     * 2.([1-9]{1}[0-9]{0,1}+(.[0-9]{1,2}):验证以不为0开始的两位小数
     * 3.(0{1}+(.[0-9]{1,2}):验证以0开始的两位小数
     */
    public static final String NUMBER_REGEX_TWO_DECIMAL = "([1-9]{1}[0-9]{0,1})||([1-9]{1}[0-9]{0,1}+(.[0-9]{1,2})||(0{1}+(.[0-9]{1,2})||(0{1})))";
    /**
     * 正则表达式
     * 整数位有效数字,位数不限+最多两位数小数
     */
    public static final String NUMBER_REGEX = "([1-9]{1}[0-9]{0,})||([1-9]{1}[0-9]{0,}+(.[0-9]{1,2})||(0{1}+(.[0-9]{1,2})|(0{1})))";
    /**
     * 时间正则表达式 00:00-23:30
     *
     */
    public static final String TIME_REGEX = "(20|21|22|23|([0]{1}[0-9]{1})|([1]{1}[0-9]{1})):(00|30)";
    /**
     * 手机正则
     */
    public static final String PHONE_REGEX = "[1][3-9]{1}[0-9]{9}";

    /**
     * @author 代廷波
     * @description:直辖市 对应的城市id
     * --重庆
     * SELECT * from sys_city_area where pid=500000
     * SELECT * from sys_city_area where pid=500100
     *
     * -- 北京
     * SELECT * from sys_city_area where pid=110000
     * SELECT * from sys_city_area where pid=110100
     * -- 上海
     * SELECT * from sys_city_area where pid=310000
     * SELECT * from sys_city_area where pid=310100
     *
     * -- 天津
     * SELECT * from sys_city_area where pid=120000
     * SELECT * from sys_city_area where pid=120100
     *
     * @param: null
     * @return
     * @create 2018/07/17 14:49
     **/
    public static Map<Long, Long> DIRECTLY_CITY_AREA = new HashMap<Long, Long>() {{
        put(110000L, 110100L);//北京
        put(120000L, 120100L);//天津
        put(310000L, 310100L);//上海
        put(500000L, 500100L);//重庆
    }};

    public static final String DEPT_LIST_KEY="dept_list_key";

    /**
     * 普通权限
     */
    public static final String  GENERAL_TYPE="generalType";
    /**
     * 员工推荐码生成时间
     */
    public static final int STAFF_REFERRAL_CODE_TIME = 60 * 1;//1分钟
    /**
     * 员工推荐码
     */
    public static final String  STAFF_REFERRAL_CODE = "staff_referral_code";

    /**
     * 员工导入的角色名
     */
    public static final String  STAFF_IMPORT_STORE_MANAGER = "店长";
    public static final String  STAFF_IMPORT_STORE_ASSISTANT = "店员";
    public static final String STAFF_ROLE_NAME_TYPE="(店长|店员)";
    /**
     * 性别
     */
    public static final String STAFF_SEX_TYPE = "(男|女)";
    /**
     * 学历
     */
    public static final String STAFF_EDUCATION = "(小学|初中|中专|高中|大专|本科|硕士|博士)";

    public static final String [] STAFF_EXCEL_EXPORT_TEMPLET = {
            "姓名",
            "身份证",
            "帐号"
    };
    /**
     * 批量导入员工文件类型
     */
    public static final String EXCEL_TYPE = "(xlsx|xls)";

    /**
     * 批量导入员工表头
     */
    public static final String EXCEL_HEAD_NAME = "staff_name,phone_num,sex,id_card,education,email,home_address,contact_name,contact_tel,role_name,";

    public static void main(String[] args) {
        for (int i = 0; i < StaffConstants.STAFF_EXCEL_EXPORT_TEMPLET.length; i++) {
            System.out.println(StaffConstants.STAFF_EXCEL_EXPORT_TEMPLET[i]);

        }
    }
}


