package com.hryj.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.hryj.cache.CodeCache;
import com.hryj.cache.LoginCache;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.cart.ShoppingCartRecord;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.cart.InvalidCartProductVO;
import com.hryj.entity.vo.cart.ShoppingCartPoductVO;
import com.hryj.entity.vo.cart.ShoppingCartVO;
import com.hryj.entity.vo.cart.request.CartOperationRequestVO;
import com.hryj.entity.vo.cart.request.ShoppingCartForStoreRequestVO;
import com.hryj.entity.vo.cart.request.ShoppingCartItemAdjustRequestVO;
import com.hryj.entity.vo.cart.request.ShoppingCartRequestVO;
import com.hryj.entity.vo.cart.response.ShoppingCartResponseVO;
import com.hryj.entity.vo.product.common.request.ProductValidateItem;
import com.hryj.entity.vo.product.common.request.ProductsValidateRequestVO;
import com.hryj.entity.vo.product.common.response.ProductValidateResponseItem;
import com.hryj.entity.vo.product.common.response.ProductsValidateResponseVO;
import com.hryj.entity.vo.staff.dept.request.DeptIdsRequestVO;
import com.hryj.entity.vo.staff.dept.response.DeptIdByStoreOrWarehouseResponseVO;
import com.hryj.entity.vo.staff.user.StaffAppLoginVO;
import com.hryj.entity.vo.user.UserLoginVO;
import com.hryj.entity.vo.user.UserPartyVO;
import com.hryj.entity.vo.user.UserServiceRangeVO;
import com.hryj.exception.BizException;
import com.hryj.exception.ServerException;
import com.hryj.feign.ActivityFeignClient;
import com.hryj.feign.ProductFeignClient;
import com.hryj.feign.StoreFeignClient;
import com.hryj.feign.UserFeignClient;
import com.hryj.mapper.ShoppingCartMapper;
import com.hryj.service.util.ProductValidateUtil;
import com.hryj.service.util.ShoppingCartUtil;
import com.hryj.utils.CommonUtil;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author 罗秋涵
 * @className: ShoppingCartService
 * @description:
 * @create 2018/7/2 0002 10:08
 **/
@Slf4j
@Service
public class ShoppingCartService extends ServiceImpl<ShoppingCartMapper, ShoppingCartRecord> {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private ActivityFeignClient activityFeignClient;

    @Autowired
    private StoreFeignClient storeFeignClient;

    /**
     * @return com.hryj.common.Result
     * @author 罗秋涵
     * @description:加入购物车
     * @param: [shoppingCartRequestVO]
     * @create 2018-07-02 11:33
     **/
    @Transactional(rollbackFor = {RuntimeException.class, BizException.class, ServerException.class})
    public Result addShoppingCart(ShoppingCartRequestVO shoppingCartRequestVO, String cart_type) {
        List<Long> partyIdList =new ArrayList<>();
        //参数判断
        if (shoppingCartRequestVO == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "请求参数不能为空");
        }
        if (shoppingCartRequestVO.getParty_id() == null || shoppingCartRequestVO.getParty_id() <= 0L) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "门店仓库编号不能为空");
        }
        if (shoppingCartRequestVO.getProduct_id() == null || shoppingCartRequestVO.getProduct_id() <= 0L) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品编号不能为空");
        }
        if (shoppingCartRequestVO.getQuantity() == null || shoppingCartRequestVO.getQuantity() <= 0) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品数量必须大于0");
        }

        StaffAppLoginVO staffUser = null;
        UserLoginVO userLogin;
        //服务用户区域门店仓库
        if (shoppingCartRequestVO.getUser_id() != null && shoppingCartRequestVO.getUser_id() > 0L) {
            //门店端请求
            staffUser = LoginCache.getStaffAppLoginVO(shoppingCartRequestVO.getLogin_token());
            if (staffUser == null) {
                return new Result(CodeEnum.FAIL_BUSINESS, "获取门店端的登陆用户信息失败, token=" + shoppingCartRequestVO.getLogin_token());
            }
            log.info("门店端代下单，加入购物车：" + staffUser.getStaff_id());
            userLogin = new UserLoginVO();
            userLogin.setUser_id(shoppingCartRequestVO.getUser_id());
            partyIdList=this.getUserServiceStoreList(null,shoppingCartRequestVO.getUser_id());
        } else {
            //用户端请求
            userLogin = LoginCache.getUserLoginVO(shoppingCartRequestVO.getLogin_token());
            if (userLogin == null) {
                return new Result(CodeEnum.FAIL_BUSINESS, "获取登陆用户信息失败, token=" + shoppingCartRequestVO.getLogin_token());
            }
            shoppingCartRequestVO.setUser_id(userLogin.getUser_id());
            partyIdList=this.getUserServiceStoreList(shoppingCartRequestVO.getLogin_token(),shoppingCartRequestVO.getUser_id());
            log.info("用户端，加入购物车：" + userLogin.getUser_id());
        }
        //商品门店仓库是否在服务用户范围
        // start Modify 属于分享购买时不校验区域 by luoqh  2018-08-23
        Boolean verify=true;
        if(shoppingCartRequestVO.getShare_user_id()==null){
            if(partyIdList!=null&&partyIdList.size()>0){
                if(!partyIdList.contains(shoppingCartRequestVO.getParty_id())){
                    verify=false;
                }
            }
        }
        // end Modify 属于分享购买时不校验区域 by luoqh  2018-08-23
        if(!verify){
            return new Result(CodeEnum.FAIL_BUSINESS,"商品不在服务范围");
        }
        //验证购物车类型
        if (!ShoppingCartUtil.containsThisCartType(cart_type)) {
            log.info("购物车类型验证失败");
            return new Result(CodeEnum.FAIL_PARAMCHECK, "不能识别的购物车类型:" + cart_type);
        }

        //是否需要验证购物车的数量
        if (ShoppingCartUtil.needToCheckLimit(cart_type)
                && ShoppingCartUtil.userShoppingCartLimitOver(userLogin.getUser_id(),
                cart_type,
                (staffUser == null ? null : staffUser.getStaff_id()),
                shoppingCartMapper)) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "购物车已满");
        }

        //验证当前商品是否已存在于购物车中
        ShoppingCartRecord record = null;
        if (ShoppingCartUtil.needToCheckLimit(cart_type)) {
            Map<String, Object> conditionMap = new HashMap<>(2);
            conditionMap.put("cart_type", cart_type);
            conditionMap.put("user_id", userLogin.getUser_id());
            if (staffUser != null) {
                conditionMap.put("help_staff_id", staffUser.getStaff_id());
            }
            Result<ShoppingCartRecord> exists_result = ShoppingCartUtil.getExists(shoppingCartRequestVO.getParty_id(),
                    shoppingCartRequestVO.getProduct_id(),
                    shoppingCartRequestVO.getActivity_id(),
                    conditionMap,
                    shoppingCartMapper);
            if (exists_result.isSuccess()) {
                record = exists_result.getData();
            }
        }

        //调用商品验证接口进行验证
        String follow_value = String.valueOf(System.nanoTime());
        Integer quantity = record == null ? shoppingCartRequestVO.getQuantity() : record.getQuantity() + shoppingCartRequestVO.getQuantity();
        Result<ProductsValidateResponseVO> prod_validate_result = ProductValidateUtil.productsValidate(shoppingCartRequestVO.getParty_id(),
                shoppingCartRequestVO.getProduct_id(),
                shoppingCartRequestVO.getActivity_id(),
                quantity,
                follow_value,
                productFeignClient);
        log.info("商品校验结果：{}",JSON.toJSONString(prod_validate_result));
        if (prod_validate_result.isFailed()) {
            return prod_validate_result;
        }
        ProductsValidateResponseVO validateResponse = prod_validate_result.getData();
        if (!validateResponse.isValidatePassed()) {
            return new Result(CodeEnum.FAIL_BUSINESS, validateResponse.getItemByFlowValue(follow_value).getOther_comments());
        }
        ProductValidateResponseItem validateResponseItem = validateResponse.getItemByFlowValue(follow_value);
        if(validateResponseItem==null||validateResponseItem.getThis_moment_sale_price()==null){
            return new Result(CodeEnum.FAIL_BUSINESS,"商品数据异常");
        }

        //库存校验
        //立即购买库存校验
        if(CodeCache.getValueByKey("ShoppingCartType", "S04").equals(cart_type)){
            if(shoppingCartRequestVO.getQuantity()>validateResponseItem.getInventory_quantity()){
                return new Result(CodeEnum.FAIL_BUSINESS,"商品库存不足");
            }
        }else if(CodeCache.getValueByKey("ShoppingCartType", "S01").equals(cart_type)||CodeCache.getValueByKey("ShoppingCartType", "S02").equals(cart_type)){
            //获取用户同门店，用商品数量用于库存校验
            Integer total_quantity=shoppingCartMapper.getUserPartyProductNum(shoppingCartRequestVO,staffUser==null? null :staffUser.getStaff_id(),cart_type);
            if(total_quantity==null){
                total_quantity=0;
            }
            if(total_quantity>validateResponseItem.getInventory_quantity()){
                return new Result(CodeEnum.FAIL_BUSINESS,"商品库存不足");
            }
        }

        if (record != null) {
            //更新购物车条目数据和价格
            try {
                record.setQuantity(record.getQuantity() + shoppingCartRequestVO.getQuantity());
                record.setInto_cart_price(validateResponseItem.getThis_moment_sale_price());
                record.updateAllColumnById();
                return new Result(CodeEnum.SUCCESS);
            } catch (Exception e) {
                log.error("更新购物车商品失败", e);
                return new Result(CodeEnum.FAIL_BUSINESS, "更新购物车商品失败");
            }
        } else {
            //创建购物车记录
            try {
                ShoppingCartRecord newRecord = new ShoppingCartRecord();
                newRecord.setCart_type(cart_type);
                newRecord.setApp_key(shoppingCartRequestVO.getApp_key());
                newRecord.setUser_id(userLogin.getUser_id());
                newRecord.setHelp_staff_id(staffUser == null ? null : staffUser.getStaff_id());
                newRecord.setInto_cart_price(validateResponseItem.getThis_moment_sale_price());
                newRecord.setParty_id(shoppingCartRequestVO.getParty_id());
                newRecord.setProduct_id(shoppingCartRequestVO.getProduct_id());
                newRecord.setActivity_id(shoppingCartRequestVO.getActivity_id());
                // start add 分享购买信息 by luoqh  2018-08-23
                newRecord.setShare_user_id(shoppingCartRequestVO.getShare_user_id());
                newRecord.setShare_source(shoppingCartRequestVO.getShare_source());
                // start add 分享购买信息 by luoqh  2018-08-23
                if (shoppingCartRequestVO.getQuantity() != null) {
                    newRecord.setQuantity(shoppingCartRequestVO.getQuantity());
                } else {
                    newRecord.setQuantity(1);
                }

                newRecord.insert();
                return new Result(CodeEnum.SUCCESS, newRecord.getId());
            } catch (Exception e) {
                log.error("添加购物车商品失败", e);
                return new Result(CodeEnum.FAIL_BUSINESS, "添加购物车商品失败");
            }
        }
    }

    /**
     * @author 王光银
     * @methodName: updateShoppingCartProdQuantity
     * @methodDesc: 调整购物车商品的数量
     * @description: 商品的数量做覆盖操作
     * @param: [requestVO]
     * @return com.hryj.common.Result
     * @create 2018-07-21 20:51
     **/
    @Transactional(rollbackFor = {RuntimeException.class, BizException.class, ServerException.class})
    public Result adjustShoppingCartProdQuantity(ShoppingCartItemAdjustRequestVO requestVO) {
        if (requestVO == null || requestVO.getCart_record_id() == null || requestVO.getCart_record_id() <= 0L) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "购物车商品条目ID不能是空值");
        }
        if (requestVO.getQuantity() == null || requestVO.getQuantity() <= 0L) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品数量必须大于0");
        }
        try {
            ShoppingCartRecord record = shoppingCartMapper.selectById(requestVO.getCart_record_id());
            if (record == null) {
                return new Result(CodeEnum.FAIL_PARAMCHECK, "不存在ID:[" + requestVO.getCart_record_id() + "]的购物车商品条目");
            }
            /**
             * 验证商品
             */
            String follow_value = String.valueOf(System.nanoTime());
            Result<ProductsValidateResponseVO> validateResponseVOResult = ProductValidateUtil.productsValidate(record.getParty_id(),
                    record.getProduct_id(),
                    record.getActivity_id(),
                    requestVO.getQuantity(),
                    follow_value,
                    productFeignClient);
            if (validateResponseVOResult.isSuccess()) {
                ProductValidateResponseItem item = validateResponseVOResult.getData().getItemByFlowValue(follow_value);
                if (!item.getIs_valid()) {
                    return new Result(CodeEnum.FAIL_BUSINESS, item.getOther_comments());
                }
                record.setInto_cart_price(item.getThis_moment_sale_price());
            }
            record.setQuantity(requestVO.getQuantity());
            record.updateById();
            return new Result(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.error("购物车商品条目数量调整失败",e );
            return new Result(CodeEnum.FAIL_BUSINESS, "购物车商品条目数量调整失败");
        }
    }


    /**
     * @return com.hryj.common.Result
     * @author 罗秋涵
     * @description: 修改购物车数量
     * @param: [shoppingCartRequestVO]
     * @create 2018-07-17 16:32
     **/
    public Result updateShoppingCart(ShoppingCartRequestVO shoppingCartRequestVO,String cart_type) {
        Long help_staff_id = null;
        //参数判断
        if (shoppingCartRequestVO == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "请求参数不能为空");
        }
        if (shoppingCartRequestVO.getParty_id() == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "门店仓库编号不能为空");
        }
        if (shoppingCartRequestVO.getProduct_id() == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品编号不能为空");
        }
        if (shoppingCartRequestVO.getQuantity() == null || shoppingCartRequestVO.getQuantity() <= 0) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品数量必须大于0");
        }

        if (shoppingCartRequestVO.getUser_id() == null) {
            Long userId = orderService.getUserLoginUserId(shoppingCartRequestVO.getLogin_token());
            shoppingCartRequestVO.setUser_id(userId);
        } else {
            help_staff_id = orderService.getStaffAppLoginUserId(shoppingCartRequestVO.getLogin_token());
        }
        ShoppingCartRecord shoppingCartRecord = new ShoppingCartRecord();
        //查询商品是否存在购物车
        ShoppingCartRecord record = shoppingCartMapper.getShoppingCartRecord(shoppingCartRequestVO, help_staff_id);
        if (record == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "不存在的购物车记录");
        }
        /**
         * 验证商品
         */
        String follow_value = String.valueOf(System.nanoTime());
        Result<ProductsValidateResponseVO> validateResponseVOResult = ProductValidateUtil.productsValidate(shoppingCartRequestVO.getParty_id(),
                shoppingCartRequestVO.getProduct_id(),
                shoppingCartRequestVO.getActivity_id(),
                shoppingCartRequestVO.getQuantity(),
                follow_value,
                productFeignClient);
        if (validateResponseVOResult.isSuccess() && validateResponseVOResult.getData() != null) {

            if (!validateResponseVOResult.getData().isValidatePassed() && UtilValidate.isNotEmpty(validateResponseVOResult.getData().getInvalid())) {
                return new Result(CodeEnum.FAIL_BUSINESS, validateResponseVOResult.getData().getInvalid().get(0).getOther_comments());
            }

            //获取用户同门店，用商品数量用于库存校验
            Integer total_quantity = shoppingCartMapper.getUserPartyProductNum(shoppingCartRequestVO, help_staff_id, cart_type);
            if (total_quantity != null) {
                //总数量=购物车里商品总数量-本次修改购物车记录原数量+本次修改数量
                total_quantity = total_quantity-record.getQuantity()+shoppingCartRequestVO.getQuantity();
            }else{
                total_quantity = 0;
            }
            if (total_quantity > validateResponseVOResult.getData().getItemByFlowValue(follow_value).getInventory_quantity()) {
                return new Result(CodeEnum.FAIL_BUSINESS, "商品库存不足");
            }

            shoppingCartRecord.setInto_cart_price(validateResponseVOResult.getData().getItemByFlowValue(follow_value).getThis_moment_sale_price());
        }

        shoppingCartRecord.setId(record.getId());
        //设置数量
        shoppingCartRecord.setQuantity(shoppingCartRequestVO.getQuantity());
        //修改购物车
        shoppingCartMapper.updateById(shoppingCartRecord);

        return new Result(CodeEnum.SUCCESS);
    }


    /**
     * @return com.hryj.entity.bo.cart.ShoppingCartRecord
     * @author 罗秋涵
     * @description: 根据用户ID, 商品ID, 门店ID查询购物车记录
     * @param: [shoppingCartRequestVO]
     * @create 2018-07-02 13:50
     **/
    public ShoppingCartRecord getShoppingCartRecord(ShoppingCartRequestVO shoppingCartRequestVO) {
        Long help_staff_id = null;
        if (shoppingCartRequestVO.getUser_id() == null) {
            Long userId = orderService.getUserLoginUserId(shoppingCartRequestVO.getLogin_token());
            shoppingCartRequestVO.setUser_id(userId);
        } else {
            help_staff_id = orderService.getStaffAppLoginUserId(shoppingCartRequestVO.getLogin_token());
        }
        ShoppingCartRecord record = shoppingCartMapper.getShoppingCartRecord(shoppingCartRequestVO, help_staff_id);
        return record;

    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.cart.response.ShoppingCartResponseVO>
     * @author 罗秋涵
     * @description: 查询购物车。 1.1版本修改商品校验时返回商品信息，活动信息。
     * @param: [requestVO]
     * @create 2018-07-02 19:20
     * @version 1.1
     **/

    public Result<ShoppingCartResponseVO> findShoppingCartList(String login_token, Long user_id) {
        Long help_staff_id = null;
        List<Long> partyIdList = new ArrayList<>();
        if (user_id == null) {
            UserLoginVO userLoginVO = orderService.getUserLoginUser(login_token);
            user_id = userLoginVO.getUser_id();
            partyIdList = this.getUserServiceStoreList(login_token, user_id);
        } else {
            help_staff_id = orderService.getStaffAppLoginUserId(login_token);
            partyIdList = this.getUserServiceStoreList(null, user_id);
        }
        log.info(" 查询购物车:user_id:{} help_staff_id:{}", user_id, help_staff_id);
        if (user_id == null) {
            return new Result(CodeEnum.FAIL_BUSINESS, "无效用户");
        }
        ShoppingCartResponseVO response = new ShoppingCartResponseVO();
        //获取用户购物车条目列表
        List<ShoppingCartRecord> cartList = shoppingCartMapper.findPartyList(user_id, help_staff_id);
        //有效列表
        List<ShoppingCartVO> validCartList = new ArrayList<>();
        //有效的商品列表
        List<ShoppingCartPoductVO> validProduct = new ArrayList<>();
        //无效列表
        List<InvalidCartProductVO> invalidCartList = new ArrayList<>();
        //商品校验
        //商品校验请求
        ProductsValidateRequestVO productsValidateRequestVO = new ProductsValidateRequestVO();
        //设置返回商品活动信息
        productsValidateRequestVO.setReturn_promotion_info(true);
        List<ProductValidateItem> prod_summary_list = new ArrayList<>();
        if (cartList != null && cartList.size() > 0) {
            for (int i = 0; i < cartList.size(); i++) {
                ShoppingCartRecord cartRecord = cartList.get(i);
                    ProductValidateItem productValidate = new ProductValidateItem();
                    productValidate.setParty_id(cartRecord.getParty_id());
                    productValidate.setProduct_id(cartRecord.getProduct_id());
                    productValidate.setActivity_id(cartRecord.getActivity_id());
                    productValidate.setRequired_min_inventory_quantity(cartRecord.getQuantity());
                    productValidate.setFollow_value(cartRecord.getId().toString());
                    prod_summary_list.add(productValidate);
                    productsValidateRequestVO.setProd_summary_list(prod_summary_list);
            }
            Result<ProductsValidateResponseVO> result = productFeignClient.productsValidate(productsValidateRequestVO);
            log.info("商品校验结果：{}",JSON.toJSONString(result));
            if (result.isSuccess() && result.getData() != null && result.getData().getProd_validate_result_list() != null) {
                List<ProductValidateResponseItem> responseItems = result.getData().getProd_validate_result_list();
                //循环遍历验证结果
                for (ProductValidateResponseItem item : responseItems) {
                    if (item.getIs_valid() &&partyIdList!=null&& partyIdList.contains(item.getParty_id())) {
                        //门店是否在服务用户范围
                        ShoppingCartPoductVO shoppingCartPoductVO = new ShoppingCartPoductVO();
                        shoppingCartPoductVO.setCart_record_id(new Long(item.getFollow_value()));
                        shoppingCartPoductVO.setProduct_id(item.getProduct_id());
                        shoppingCartPoductVO.setProduct_name(item.getProduct_name());
                        shoppingCartPoductVO.setList_image_url(item.getList_image_url());
                        shoppingCartPoductVO.setInto_cart_price(null);
                        shoppingCartPoductVO.setSale_price(String.valueOf(item.getNormal_price()));
                        shoppingCartPoductVO.setQuantity(item.getRequired_min_inventory_quantity());
                        shoppingCartPoductVO.setInventory_quantity(item.getInventory_quantity());
                        shoppingCartPoductVO.setParty_id(item.getParty_id());
                        //v1.2 luoqh add 添加 商品title标记，商品类型，商品类型名称 ***************start
                        shoppingCartPoductVO.setTitle_mark(item.getTitle_mark_list()==null ? null : item.getTitle_mark_list().get(0));
                        shoppingCartPoductVO.setProduct_type_id(item.getProduct_type_id());
                        shoppingCartPoductVO.setProduct_type_name(item.getProduct_type_name());
                        //v1.2 luoqh add 添加 商品title标记，商品类型，商品类型名称 ***************end
                        //活动信息
                        if (item.getActivity_id() != null && item.getPromotion_info() != null) {
                            shoppingCartPoductVO.setActivity_id(item.getActivity_id());
                            shoppingCartPoductVO.setActivity_name(item.getPromotion_info().getActivity_name());
                            shoppingCartPoductVO.setActivity_type(item.getPromotion_info().getActivity_type());
                            shoppingCartPoductVO.setActivity_mark_image(item.getPromotion_info().getActivity_mark_image());
                            shoppingCartPoductVO.setActivity_price(String.valueOf(item.getThis_moment_sale_price()));
                        }
                        validProduct.add(shoppingCartPoductVO);
                    } else {
                        log.info("失效商品编号：{},失效理由：{}", item.getProduct_id(), item.getOther_comments()==null ? "门店不在服务用户区域":item.getOther_comments());
                        InvalidCartProductVO invalidCart = new InvalidCartProductVO();
                        invalidCart.setCart_record_id(new Long(item.getFollow_value()));
                        invalidCart.setProduct_id(item.getProduct_id());
                        invalidCart.setProduct_name(item.getProduct_name());
                        invalidCart.setList_image_url(item.getList_image_url());
                        invalidCart.setSale_price(String.valueOf(item.getNormal_price()));
                        invalidCart.setActivity_price(item.getActivity_id() == null ? null : String.valueOf(item.getThis_moment_sale_price()));
                        invalidCartList.add(invalidCart);
                    }
                }
            } else {
                return new Result(CodeEnum.FAIL_BUSINESS, "商品校验失败");
            }
        }
        if (validProduct != null && validProduct.size() > 0) {
            Map<Long, List<ShoppingCartPoductVO>> cartPoductMap = new LinkedHashMap<>();
            //门店集合用于查询门店信息
            List<Long> partyList = new ArrayList<>();
            //根据门店分组
            for (int i = 0; i < validProduct.size(); i++) {
                if (cartPoductMap.get(validProduct.get(i).getParty_id()) == null) {
                    //添加门店
                    partyList.add(validProduct.get(i).getParty_id());
                    List<ShoppingCartPoductVO> CartPoductList = new ArrayList<>();
                    CartPoductList.add(validProduct.get(i));
                    cartPoductMap.put(validProduct.get(i).getParty_id(), CartPoductList);
                } else {
                    cartPoductMap.get(validProduct.get(i).getParty_id()).add(validProduct.get(i));
                }
            }
            //获取门店信息
            DeptIdsRequestVO deptIdsRequestVO = new DeptIdsRequestVO();
            deptIdsRequestVO.setDept_ids(partyList);
            Result<ListResponseVO<DeptIdByStoreOrWarehouseResponseVO>> result = storeFeignClient.getAppDeptIdsByStoreOrWarehouseDet(deptIdsRequestVO);
            List<DeptIdByStoreOrWarehouseResponseVO> deptList = new ArrayList<>();
            if (result.isSuccess() && result.getData().getRecords() != null) {
                deptList = result.getData().getRecords();
            } else {
                return new Result(CodeEnum.FAIL_BUSINESS, "门店信息查询异常");
            }
            //循环遍历分组
            for (Long key : cartPoductMap.keySet()) {
                ShoppingCartVO shoppingCartVO = new ShoppingCartVO();
                shoppingCartVO.setParty_id(key);
                for (DeptIdByStoreOrWarehouseResponseVO deptInfo : deptList) {
                    if (deptInfo.getDept_id().equals(key)) {
                        shoppingCartVO.setHelp_staff_id(help_staff_id);
                        shoppingCartVO.setParty_name(deptInfo.getDept_name());
                        shoppingCartVO.setDept_type(deptInfo.getDept_type());
                        break;
                    }
                }
                shoppingCartVO.setUser_id(user_id);
                shoppingCartVO.setCartPorductList(cartPoductMap.get(key));
                validCartList.add(shoppingCartVO);
            }
        }
        response.setCartList(validCartList);
        response.setInvalidCartList(invalidCartList);

        return new Result(CodeEnum.SUCCESS, response);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.cart.response.ShoppingCartResponseVO>
     * @author 罗秋涵
     * @description: 删除购物车记录并返回最新列表
     * @param: [requestVO, cartItemIds, helpObjId]
     * @create 2018-07-04 15:23
     **/
    public Result<ShoppingCartResponseVO> deleteShoppingCartItem(CartOperationRequestVO cartoPerationRequestVO) {
        //将逗号隔开的字符串转list
        List<String> idList = CommonUtil.stringToList(cartoPerationRequestVO.getCartItemIds());
        //删除操作
        shoppingCartMapper.deleteShoppingCartItem(idList);
        return findShoppingCartList(cartoPerationRequestVO.getLogin_token(), cartoPerationRequestVO.getUser_id());
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.cart.response.ShoppingCartResponseVO>
     * @author 罗秋涵
     * @description: 清空无效商品
     * @param: [cartoPerationRequestVO]
     * @create 2018-07-07 17:22
     **/
    public Result<ShoppingCartResponseVO> clearInvalidProduct(CartOperationRequestVO cartoPerationRequestVO) {
        //获取用户信息
        Long help_staff_id = null;
        Long user_id = null;
        //服务用户区域门店仓库
        List<Long> partyIdList =new ArrayList<>();;
        if (cartoPerationRequestVO.getUser_id() != null) {
            help_staff_id = orderService.getStaffAppLoginUserId(cartoPerationRequestVO.getLogin_token());
            user_id=cartoPerationRequestVO.getUser_id();
            partyIdList=this.getUserServiceStoreList(null,cartoPerationRequestVO.getUser_id());
        } else {
             UserLoginVO userLoginVO= orderService.getUserLoginUser(cartoPerationRequestVO.getLogin_token());
             user_id=userLoginVO.getUser_id();
            partyIdList=this.getUserServiceStoreList(cartoPerationRequestVO.getLogin_token(),cartoPerationRequestVO.getUser_id());
        }
        List<String> idList = new ArrayList<>();
        //查询购物车列表
        List<ShoppingCartRecord> recordList = shoppingCartMapper.findCartRecordList(user_id, help_staff_id);
        if(recordList==null||recordList.size()<1){
            return new Result(CodeEnum.FAIL_BUSINESS,"无无效记录");
        }
        //商品校验请求
        ProductsValidateRequestVO productsValidateRequestVO = new ProductsValidateRequestVO();
        List<ProductValidateItem> prod_summary_list = new ArrayList<>();
        //循环组装校验请求数据
        for (ShoppingCartRecord tmp : recordList) {
            //商品门店仓库是否在服务用户范围
            Boolean verify=false;
            if(partyIdList!=null&&partyIdList.size()>0){
                if(partyIdList.contains(tmp.getParty_id())){
                    verify=true;
                }
            }
            if(!verify){
                idList.add(String.valueOf(tmp.getId()));
                continue;
            }
            ProductValidateItem productValidate = new ProductValidateItem();
            productValidate.setParty_id(tmp.getParty_id());
            productValidate.setProduct_id(tmp.getProduct_id());
            productValidate.setActivity_id(tmp.getActivity_id());
            productValidate.setRequired_min_inventory_quantity(tmp.getQuantity());
            productValidate.setFollow_value(tmp.getId().toString());
            prod_summary_list.add(productValidate);
        }
        if(prod_summary_list!=null&&prod_summary_list.size()>0){
            productsValidateRequestVO.setProd_summary_list(prod_summary_list);
            //发起校验
            Result<ProductsValidateResponseVO> result = productFeignClient.productsValidate(productsValidateRequestVO);
            //获得校验结果
            List<ProductValidateResponseItem> responseItems = result.getData().getProd_validate_result_list();
            //遍历校验结果
            for (ProductValidateResponseItem item : responseItems) {
                if (!item.getIs_valid()) {
                    idList.add(item.getFollow_value());
                }
            }
        }
        if(idList!=null&&idList.size()>0){
            //删除失效购物车记录
            shoppingCartMapper.deleteShoppingCartItem(idList);
        }

        return findShoppingCartList(cartoPerationRequestVO.getLogin_token(), cartoPerationRequestVO.getUser_id());

    }

    /**
     * @return java.util.List<com.hryj.entity.bo.cart.ShoppingCartRecord>
     * @author 罗秋涵
     * @description: 根据购物车Id获取购物车记录
     * @param: [idsList]
     * @create 2018-07-05 15:43
     **/
    public List<ShoppingCartRecord> findShoppingCartRecordList(List<String> idsList) {
        return shoppingCartMapper.findShoppingCartRecordList(idsList);
    }

    /**
     * @return java.util.List<com.hryj.entity.bo.cart.ShoppingCartRecord>
     * @author 白飞
     * @description: 根据购物车Id获取购物车记录
     * @param: [ids]
     * @create 2018-08-23 13:36
     **/
    public List<ShoppingCartRecord> findByIds(List<Long> ids) {
        if(null == ids || ids.size() == 0){
            return null;
        }
        List<String> idsList = Lists.newArrayList();
        for(Long id : ids){
            idsList.add(id + "");
        }
        return shoppingCartMapper.findShoppingCartRecordList(idsList);
    }

    /**
     * @return com.hryj.common.Result<java.lang.Integer>
     * @author 罗秋涵
     * @description: 查询用户购物车记录总数
     * @param: [shoppingCartForStoreRequestVO]
     * @create 2018-07-17 11:22
     **/
    public Result<Integer> getCartProductNum(ShoppingCartForStoreRequestVO shoppingCartForStoreRequestVO) {
        if (shoppingCartForStoreRequestVO == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "参数不能为空");
        }
        Long help_staff_id = null;
        if (shoppingCartForStoreRequestVO.getUser_id() == null) {
            shoppingCartForStoreRequestVO.setUser_id(orderService.getUserLoginUserId(shoppingCartForStoreRequestVO.getLogin_token()));
        } else {
            help_staff_id = orderService.getStaffAppLoginUserId(shoppingCartForStoreRequestVO.getLogin_token());
        }
        Integer num = shoppingCartMapper.getCartProductNum(shoppingCartForStoreRequestVO.getUser_id(), help_staff_id);

        return new Result(CodeEnum.SUCCESS, num);
    }

    /**
     * @author 罗秋涵
     * @description: 获取服务用户门店列表
     * @param: [token, user_id]
     * @return java.util.List<java.lang.Long>
     * @create 2018-08-08 21:56
     **/
    public List<Long> getUserServiceStoreList(String token,Long user_id){
        List<Long> partyIdList=new ArrayList<>();
        //服务用户区域门店仓库
        UserServiceRangeVO userServiceRangeVO=new UserServiceRangeVO();
        //根据token获取服务用户的门店
        if(StrUtil.isNotEmpty(token)){
            UserLoginVO userLoginVO= LoginCache.getUserLoginVO(token);
            if(userLoginVO!=null){
                userServiceRangeVO.setStoreList(userLoginVO.getStoreList());
                userServiceRangeVO.setWarehouse(userLoginVO.getWarehouse());
            }
        }else{
            //根据用户编号获取服务用户区域的门店集合和仓库
            Result<UserServiceRangeVO> result=userFeignClient.getUserServiceRangeByUserId(user_id);
            if(result.isSuccess()&&result.getData()!=null){
                userServiceRangeVO=result.getData();
            }
        }
        if(userServiceRangeVO!=null&&userServiceRangeVO.getStoreList()!=null&&userServiceRangeVO.getStoreList().size()>0){
            for(UserPartyVO userPartyVO:userServiceRangeVO.getStoreList()){
                partyIdList.add(userPartyVO.getParty_id());
            }
        }
        if(userServiceRangeVO.getWarehouse()!=null){
            partyIdList.add(userServiceRangeVO.getWarehouse().getParty_id());
        }
        return partyIdList;
    }

    /**
     * 获取购物车商品详细
     * @param cartRecord
     * @return
     */
    public ShoppingCartPoductVO findCartProductInfo(ShoppingCartRecord cartRecord){
        return this.shoppingCartMapper.findCartProductInfo(cartRecord);
    }
}
