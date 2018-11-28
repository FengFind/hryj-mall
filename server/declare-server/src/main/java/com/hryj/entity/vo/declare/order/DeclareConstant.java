package com.hryj.entity.vo.declare.order;

/**
 * @author 白飞
 * @className: DeclareConstant
 * @description:
 * @create 2018/9/29 14:22
 **/
public class DeclareConstant {

    /** 支付编码 */
    public enum PayCode{
        /** 支付宝 */
        alipay,
        /** 微信 */
        wxpay
    }

    /** 物流编码 */
    public enum WaybillCode{
        /** 韵达 */
        yunda,
        /** EMS */
        ems
    }

    /** 拆单编码 */
    public enum SplitCode{
        /** 拆单 */
        T,
        /** 不拆单 */
        F
    }

    /** 拆单编码 */
    public enum BizTypeCode{
        /** 直邮 */
        I10,
        /** 保税 */
        I20
    }

    /** 申报状态 */
    public enum DeclareStatus {

        /**
         * 状态
         */
        Status_0(0, "待申报"),
        Status_1(1, "申报中"),
        Status_2(2, "运单、支付信息及订单不存在"),
        Status_43(43, "申报错误"),
        Status_44(44, "终止申报或申报失败"),
        Status_45(45, "运单信息不存在"),
        Status_46(46, "支付信息不存在"),
        Status_47(47, "订购人与支付人不匹配"),
        Status_48(100, "订购人超过年度限额"),
        Status_49(49, "订单不存在"),
        Status_50(-621041, "存在已申报清单，无法完成申报操作"),
        Status_51(-621001, "清单报文校验失败，商品错误:商品编未备案"),
        Status_52(0, "身份验证未通过，请核对身份信息"),
        Status_621001(-621001, "预录入编号(preNo)应由电子口岸生成"),
        Status_621041(-621041, "短时间内不能重复新增申报操作"),
        Status_621055(-621055, "已存在较新清单，请修改app_time为最新时间"),
        Status_621057(-621057, "改单失败,invtno不能为空，请重试"),
        Status_621043(-621043, "清单目前的状态不允许进行改单申报操作"),
        Status_621042(-621042, "清单不存在，无法完成改单申报操作"),
        Status_621999(-621999, "海关清单编号不允许修改"),
        Status_621053(-621053, "改单失败,preno和invono不能为空:[InvtNo字段为空]，请重试"),
        Status_621049(-621049, "验签失败"),
        Status_621031(-621053, "请确认表体gnum无重复"),
        Status_100(100, "清单中业务主键重复（订单号号+电商企业备案号）"),
        Status_120(120, "逻辑校验通过"),
        Status_800(800, "海关放行即海关备案通过"),
        Status_600(600, "挂起,地址不详"),
        Status_400(400, "海关已接受申报待货物运抵后处理"),
        Status_300(300, "待人工审核"),
        /**
         * 自定义,主要用于订单、运单、支付单
         */
        Status_805(805, "申报出现异常"),
        Status_806(806, "等待申报"),
        Status_807(807, "申报成功"),
        Status_808(808, "申报失败"),
        Status_809(809, "取消失败");

        private int index;

        private String name;

        DeclareStatus(int index, String name){
            this.index = index;
            this.name = name;
        }

        public int getIndex(){
            return this.index;
        }
        public  void setIndex(int index){
            this.index = index;
        }

        public String getName(){
            return this.name;
        }
        public void setName(String name){
            this.name = name;
        }

    }

    /**
     * 根据申报状态值获取名称
     *
     * @param index
     *         索引
     * @return
     */
    public static String getDeclareName(int index){
        for(DeclareStatus declareStatus : DeclareStatus.values()){
            if(declareStatus.getIndex() == index){
                return declareStatus.getName();
            }
        }
        return null;
    }

}
