package com.hryj.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import com.hryj.cache.CodeCache;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.constant.StaffConstants;
import com.hryj.entity.vo.amap.request.AmapAroundRequestVO;
import com.hryj.entity.vo.amap.request.AmapDistrictRequestVO;
import com.hryj.entity.vo.sys.response.CodeInfoVO;
import com.hryj.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 代廷波
 * @className: AmapController
 * @description:
 * @create 2018/7/13 0013-16:05
 **/
@RestController
@RequestMapping("/amap")
@Slf4j
public class AmapController {
    /**
     * @author 代廷波
     * @description: 周边查询
     * @param: vo
     * @return com.hryj.common.Result
     * @create 2018/07/19 10:59
     **/
    @PostMapping("/getAmapAround")
    public Result getAmapAround(@RequestBody AmapAroundRequestVO vo) throws GlobalException{


        Map<String, Object> map = new HashMap<>();
        map.put("key", StaffConstants.AMAPKEY);
        map.put("output", "JSON");

        //查询POI类型:当keywords和types均为空的时候，默认指定types为050000（餐饮服务）、070000（生活服务）、120000（商务住宅
        String type="";
        List<CodeInfoVO> list = CodeCache.getCodeList("LocationType");

        for (CodeInfoVO codeInfoVO : list) {
            type+=codeInfoVO.getCode_value()+"|";
        }
        map.put("types", type);

        //"中心点坐标-规则： 经度和纬度用\",\"分割，经度在前，纬度在后，经纬度小数点后不得超过6位"
        if (StrUtil.isNotEmpty(vo.getLocation())) {
            map.put("location", vo.getLocation());
        }

        //查询关键字-规则： 多个关键字用“|”分割"
        if (StrUtil.isNotEmpty(vo.getKeywords())) {
            map.put("keywords", vo.getKeywords());
        }

        //查询城市"
        if (StrUtil.isNotEmpty(vo.getCity())) {
            map.put("city", vo.getCity());
        }
        //查询半径 取值范围:0-50000。规则：大于50000按默认值，单位：米"
        if (StrUtil.isNotEmpty(vo.getRadius())) {
            map.put("radius", vo.getRadius());
        }else{
            map.put("radius", CodeCache.getValueByKey("AmapAroundRange","S01"));
        }
        //排序规则 规定返回结果的排序规则。按距离排序：distance；综合排序：weight"
        if (StrUtil.isNotEmpty(vo.getSortrule())) {
            map.put("sortrule", vo.getKeywords());
        }
        //每页记录数据 强烈建议不超过25，若超过25可能造成访问报错"
        if (StrUtil.isNotEmpty(vo.getOffset())) {
            map.put("offset", vo.getOffset());
        }
        //"当前页数最大翻页数100"
        if (StrUtil.isNotEmpty(vo.getPage())) {
            map.put("page", vo.getPage());
        }
        //返回结果控制 此项默认返回基本地址信息；取值为all返回地址信息、附近POI、道路以及道路交叉口信息"
        if (StrUtil.isNotEmpty(vo.getExtensions())) {
            map.put("extensions", vo.getExtensions());
        }
        //数字签名
        if (StrUtil.isNotEmpty(vo.getSig())) {
            map.put("sig", vo.getSig());
        }
        //回数据格式类型 可选值：JSON，XML
        if (StrUtil.isNotEmpty(vo.getOutput())) {
            map.put("output", vo.getOutput());
        }
        //回调函数callback值是用户定义的函数名称，此参数只在output=JSON时有效"
        if (StrUtil.isNotEmpty(vo.getCallback())) {
            map.put("callback", vo.getCallback());
        }

        log.info("地图周边查询请求参数map:{}",map.toString());

        String result = null;
        try {
            result = getAmapDet(StaffConstants.AMAP_AROUND_URL,map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(CodeEnum.FAIL_PARAMCHECK,"地图链接超时");
        }


        if(StrUtil.isNotEmpty(result)){
            Object obj=result;
            return new Result(CodeEnum.SUCCESS,obj);
        }else{
            return new Result(CodeEnum.FAIL_PARAMCHECK,"没有查询到相应数据");
        }

    }
    /**
     * @author 代廷波
     * @description: 行政搜索
     * @param: vo
     * @return com.hryj.common.Result
     * @create 2018/07/19 11:42
     **/

    @PostMapping("/getAmapAmapDistrict")
    public Result getAmapAmapDistrict(@RequestBody AmapDistrictRequestVO vo) throws GlobalException {


        Map<String, Object> map = new HashMap<>();
        map.put("key", StaffConstants.AMAPKEY);
        map.put("output", "JSON");


        //规则：只支持单个关键词语搜索关键词支持：行政区名称、citycode、adcode"
        if (StrUtil.isNotEmpty(vo.getKeywords())){
            map.put("keywords", vo.getKeywords());
        };

        //最外层返回数据个数,可选20")
        if (StrUtil.isNotEmpty(vo.getOffset())){
            map.put("offset", vo.getOffset());
        };

       //需要第几页数据,最外层的districts最多会返回20个数据，若超过限制，请用page请求下一页数据。例如page=2；page=3。默认page=1"
        if (StrUtil.isNotEmpty(vo.getPage())){
            map.put("page", vo.getPage());
        };
        //规则：设置显示下级行政区级数（行政区级别包括：国家、省/直辖市、市、区/县、乡镇/街道多级数据） 可选值：0、1、2、3等数字，并以此类推 0：不返回下级行政区1：返回下一级行政区；2：返回下两级行政区；3：返回下三级行政区"
        if (StrUtil.isNotEmpty(vo.getSubdistrict())){
            map.put("subdistrict", vo.getSubdistrict());
        };
        //项控制行政区信息中返回行政区边界坐标点； 可选值：base、all;base:不返回行政区边界坐标点；all:只返回当前查询district的边界值，不返回子节点的边界值；"
        if (StrUtil.isNotEmpty(vo.getExtensions())){
            map.put("extensions", vo.getExtensions());
        };

        //"根据区划过滤按照指定行政区划进行过滤，填入后则只返回该省/直辖市信息需填入adcode，为了保证数据的正确，强烈建议填入此参数"
        if (StrUtil.isNotEmpty(vo.getFilter())){
            map.put("filter", vo.getFilter());
        };
        //排序规则 规定返回结果的排序规则。按距离排序：distance；综合排序：weight"
        if (StrUtil.isNotEmpty(vo.getSortrule())){
            map.put("sortrule", vo.getSortrule());
        };
        //返回数据格式类型 可选值：JSON，XML")
        if (StrUtil.isNotEmpty(vo.getOutput())){
            map.put("output", vo.getOutput());
        };
        //回调函数callback值是用户定义的函数名称，此参数只在output=JSON时有效"
        if (StrUtil.isNotEmpty(vo.getCallback())){
            map.put("getCallback", vo.getCallback());
        };

        log.info("地图行政请求参数map:{}",map.toString());

        String result = null;
        try {
            result = getAmapDet(StaffConstants.AMAP_DISTRICT_URL,map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(CodeEnum.FAIL_PARAMCHECK,"地图链接超时");
        }

        if(StrUtil.isNotEmpty(result)){
            Object obj =result;
            return new Result(CodeEnum.SUCCESS,obj);
        }else{
            return new Result(CodeEnum.FAIL_PARAMCHECK,"没有查询到相应数据");
        }

    }

    public String getAmapDet(String url,Map<String, Object> map){
        String result = null;
        try {
             result = HttpRequest.post(url).form(map).execute().body();
            log.info("地图响应数据:{}",result);
        } catch (HttpException e) {
            e.printStackTrace();
            log.info("地图查询失败--->参数={}",map.toString());
            return result;
            //throw new HttpException(e);
        }
        return result;
    }
}
