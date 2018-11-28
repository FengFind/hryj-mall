package com.hryj.service.prodcate;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.cache.LoginCache;
import com.hryj.cache.RedisService;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.product.ProductAttribute;
import com.hryj.entity.bo.product.ProductCategory;
import com.hryj.entity.bo.product.ProductCategoryAttr;
import com.hryj.entity.bo.product.ProductCategoryAttrItem;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.product.category.request.*;
import com.hryj.entity.vo.product.category.response.ProdCateAttrEnumItemResponseVO;
import com.hryj.entity.vo.product.category.response.ProdCateAttrResponseVO;
import com.hryj.entity.vo.product.category.response.ProdCateTreeResponseItemVO;
import com.hryj.entity.vo.product.category.response.ProductCategoryResponseVO;
import com.hryj.entity.vo.product.partyprod.request.IdRequestVO;
import com.hryj.entity.vo.staff.user.StaffAdminLoginVO;
import com.hryj.exception.BizException;
import com.hryj.exception.ServerException;
import com.hryj.mapper.*;
import com.hryj.service.util.ProductCategoryUtil;
import com.hryj.service.util.ProductUtil;
import com.hryj.utils.UtilMisc;
import com.hryj.utils.UtilValidate;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

/**
 * @author 王光银
 * @className: ProductCategoryService
 * @description:
 * @create 2018/6/25 0025 20:10
 **/
@Slf4j
@Service
public class ProductCategoryService extends ServiceImpl<ProductCategoryMapper, ProductCategory> {

    @Autowired
    private ProductCategoryAttrMapper categoryAttrMapper;

    @Autowired
    private ProductCategoryAttrItemMapper attrItemMapper;

    @Autowired
    private ProductAttributeMapper prodAttrMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private RedisService redisService;

    /**
     * @author 王光银
     * @methodName: getOneProductCategory
     * @methodDesc: 根据分类ID返回一个商品分类数据，包含属性数据
     * @description: 返回对象中的 sub_list 节点属性永远不会有值
     * @param: [prodCateIdRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.product.category.response.ProductCategoryResponseVO>
     * @create 2018-07-12 10:12
     **/
    public Result<ProductCategoryResponseVO> getOneProductCategory(@RequestBody ProdCateIdRequestVO prodCateIdRequestVO) throws ServerException {
        if (prodCateIdRequestVO.getProduct_category_id() == null || prodCateIdRequestVO.getProduct_category_id() <= 0L) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "分类ID不能是空值");
        }
        try {
            ProductCategory cate = super.selectById(prodCateIdRequestVO.getProduct_category_id());
            if (cate == null) {
                return new Result<>(CodeEnum.FAIL_BUSINESS, "不存在ID:[" + prodCateIdRequestVO.getProduct_category_id() + "]的商品分类数据");
            }
            ProductCategoryResponseVO responseVO = new ProductCategoryResponseVO();
            responseVO.setProduct_category_id(cate.getId());
            responseVO.setCategory_name(cate.getCategory_name());
            responseVO.setCategory_pid(cate.getCategory_pid());
            responseVO.setSort_num(cate.getSort_num());
            responseVO.setCategory_url(cate.getCategory_url());
            if (cate.getCategory_pid() != null && cate.getCategory_pid() > 0L) {
                ProductCategory parent = super.selectById(cate.getCategory_pid());
                if (parent != null) {
                    responseVO.setParent_category_name(parent.getCategory_name());
                }
            }

            //加载属性数据
            ProductCategoryAttrFindRequestVO attrFindRequestVO = new ProductCategoryAttrFindRequestVO();
            attrFindRequestVO.setProduct_category_id(cate.getId());
            Result<ListResponseVO<ProdCateAttrResponseVO>> attrResult = findProdCategoryAttr(attrFindRequestVO);
            if (attrResult.isSuccess() && UtilValidate.isNotEmpty(attrResult.getData().getRecords())) {
                responseVO.setAttr_list(attrResult.getData().getRecords());
            }
            return new Result<>(CodeEnum.SUCCESS, responseVO);
        } catch (Exception e) {
            log.error("根据分类ID返回一个商品分类数据，包含属性数据 -- 加载分类数据失败", e);
            return new Result<>(CodeEnum.FAIL_SERVER, "加载分类数据失败");
        }
    }

    @PostMapping("/findProductCategory")
    public Result<ListResponseVO<ProdCateTreeResponseItemVO>> findProdCateTree(
            @RequestBody ProductCategoryFindRequestVO productCategoryFindRequestVO) throws BizException {

        try {
            Result<ListResponseVO<ProductCategoryResponseVO>> result = findProductCategory(productCategoryFindRequestVO);
            if (result.isFailed()) {
                return new Result<>(CodeEnum.FAIL_SERVER, result.getMsg());
            }
            if (result.getData() == null || UtilValidate.isEmpty(result.getData().getRecords())) {
                return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>());
            }
            List<ProductCategoryResponseVO> list = result.getData().getRecords();
            List<ProdCateTreeResponseItemVO> tree_node_list = new ArrayList<>(list.size());

            for (ProductCategoryResponseVO vo: list) {
                tree_node_list.add(treeNodeCopy(vo));
            }

            return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>(tree_node_list));
        } catch (Exception e) {
            log.error("加载分类数据树结构失败", e);
            throw new BizException("加载分类数据树结构失败", e);
        }
    }

    private ProdCateTreeResponseItemVO treeNodeCopy(@NonNull ProductCategoryResponseVO vo) {
        ProdCateTreeResponseItemVO node = new ProdCateTreeResponseItemVO();
        node.setValue(vo.getProduct_category_id().toString());
        node.setTitle(vo.getCategory_name());
        if (UtilValidate.isNotEmpty(vo.getSub_list())) {
            node.setChildren(new ArrayList<>(vo.getSub_list().size()));
            for (ProductCategoryResponseVO sub_item : vo.getSub_list()) {
                node.getChildren().add(treeNodeCopy(sub_item));
            }
        }
        return node;
    }

    public Result<ListResponseVO<ProductCategoryResponseVO>> findProductCategory(ProductCategoryFindRequestVO productCategoryFindRequestVO) throws BizException {
        try {
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.setSqlSelect("id", "category_name", "category_pid", "sort_num");
            wrapper.orderBy("sort_num", false);
            if (UtilValidate.isNotEmpty(productCategoryFindRequestVO.getCategory_name())) {
                wrapper.like("category_name", productCategoryFindRequestVO.getCategory_name().trim());
            }
            List<ProductCategory> category_list = super.selectList(wrapper);
            if (UtilValidate.isEmpty(category_list)) {
                return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>());
            }

            Map<Long, List<ProductCategoryResponseVO>> sub_map = new HashMap<>(category_list.size() / 2);

            //组织树形结构
            List<ProductCategoryResponseVO> category_vo_list = new ArrayList<>(category_list.size());
            for (ProductCategory category : category_list) {
                ProductCategoryResponseVO vo = new ProductCategoryResponseVO();
                category_vo_list.add(vo);
                vo.setProduct_category_id(category.getId());
                vo.setCategory_name(category.getCategory_name());
                vo.setCategory_pid(category.getCategory_pid());
                vo.setSort_num(category.getSort_num() == null ? 0 : category.getSort_num());
                if (vo.getCategory_pid() != null && vo.getCategory_pid() > 0) {
                    if (sub_map.containsKey(vo.getCategory_pid())) {
                        sub_map.get(vo.getCategory_pid()).add(vo);
                    } else {
                        List<ProductCategoryResponseVO> sub_vo_list = new ArrayList<>(category_list.size());
                        sub_vo_list.add(vo);
                        sub_map.put(vo.getCategory_pid(), sub_vo_list);
                    }
                }
            }

            Iterator<ProductCategoryResponseVO> it = category_vo_list.iterator();
            while (it.hasNext()) {
                ProductCategoryResponseVO vo = it.next();
                if (sub_map.containsKey(vo.getProduct_category_id())) {
                    vo.setSub_list(sub_map.get(vo.getProduct_category_id()));
                }
                if (vo.getCategory_pid() != null && vo.getCategory_pid() > 0) {
                    it.remove();
                }
            }

            Result<ListResponseVO<ProductCategoryResponseVO>> result = new Result<>(CodeEnum.SUCCESS);
            result.setData(new ListResponseVO<>(category_vo_list));

            //TODO 为true时返回属性数据

            return result;
        } catch (Exception e) {
            log.error("加载分类数据树结构失败", e);
            throw new BizException("加载分类数据树结构失败", e);
        }
    }

    public Result<ListResponseVO<ProdCateAttrResponseVO>> findProdCategoryAttr(ProductCategoryAttrFindRequestVO productCategoryAttrFindRequestVO) throws BizException {
        if (productCategoryAttrFindRequestVO == null
                || productCategoryAttrFindRequestVO.getProduct_category_id() == null
                || productCategoryAttrFindRequestVO.getProduct_category_id() <= 0) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品分类ID不能是空值");
        }
        if (UtilValidate.isNotEmpty(productCategoryAttrFindRequestVO.getAttr_type())
                && !ProductUtil.ENUM_ATTR_TYPE.equals(productCategoryAttrFindRequestVO.getAttr_type())
                && !ProductUtil.STRING_ATTR_TYPE.equals(productCategoryAttrFindRequestVO.getAttr_type())) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "无法识别的属性类型:" + productCategoryAttrFindRequestVO.getAttr_type());
        }
        if (productCategoryAttrFindRequestVO.getReturn_parent_attr() == null) {
            productCategoryAttrFindRequestVO.setReturn_parent_attr(false);
        }

        try {
            //加载分类对象
            ProductCategory category = super.selectById(productCategoryAttrFindRequestVO.getProduct_category_id());
            if (category == null) {
                return new Result<>(CodeEnum.FAIL_BUSINESS, "不存在ID为:[" + productCategoryAttrFindRequestVO.getProduct_category_id() + "]的商品分类");
            }
            Set<Long> id_set = new HashSet<>(3);
            id_set.add(category.getId());

            //加载父级分类
            if (productCategoryAttrFindRequestVO.getReturn_parent_attr()) {
                while (category.getCategory_pid() != null && category.getCategory_pid() > 0L) {
                    category = selectById(category.getCategory_pid());
                    if (category == null) {
                        break;
                    }
                    id_set.add(category.getId());
                }
            }


            EntityWrapper wrapper = new EntityWrapper();
            wrapper.in("prod_cate_id", id_set);
            wrapper.orderBy("prod_cate_id", true);
            if (ProductUtil.ENUM_ATTR_TYPE.equals(productCategoryAttrFindRequestVO.getAttr_type()) || ProductUtil.STRING_ATTR_TYPE.equals(productCategoryAttrFindRequestVO.getAttr_type())) {
                wrapper.and().eq("attr_type", productCategoryAttrFindRequestVO.getAttr_type());
            }
            List<ProductCategoryAttr> attr_list = categoryAttrMapper.selectList(wrapper);
            if (UtilValidate.isNotEmpty(attr_list)) {
                List<ProdCateAttrResponseVO> vo_list = new ArrayList<>(attr_list.size());
                Set<Long> enum_attr_id_set = new HashSet<>(attr_list.size());
                for (ProductCategoryAttr attr : attr_list) {
                    ProdCateAttrResponseVO vo = new ProdCateAttrResponseVO();
                    vo_list.add(vo);
                    vo.setAttr_name(attr.getAttr_name());
                    vo.setAttr_type(attr.getAttr_type());
                    vo.setProd_cate_attr_id(attr.getId());
                    vo.setProd_attr_id(System.nanoTime());

                    if (ProductUtil.ENUM_ATTR_TYPE.equals(attr.getAttr_type())) {
                        enum_attr_id_set.add(attr.getId());
                    }
                }

                if (UtilValidate.isNotEmpty(enum_attr_id_set)) {
                    EntityWrapper item_wrapper = new EntityWrapper();
                    item_wrapper.in("prod_cate_attr_id", enum_attr_id_set);
                    List<ProductCategoryAttrItem> attr_item_list = attrItemMapper.selectList(item_wrapper);
                    if (UtilValidate.isNotEmpty(attr_item_list)) {

                        Map<Long, List<ProdCateAttrEnumItemResponseVO>> attr_item_map = new HashMap<>(attr_item_list.size());

                        for (ProductCategoryAttrItem attr_item : attr_item_list) {
                            ProdCateAttrEnumItemResponseVO item_vo = new ProdCateAttrEnumItemResponseVO();
                            item_vo.setAttr_enum_item_id(attr_item.getId());
                            item_vo.setAttr_value(attr_item.getAttr_value());
                            if (attr_item_map.containsKey(attr_item.getProd_cate_attr_id())) {
                                attr_item_map.get(attr_item.getProd_cate_attr_id()).add(item_vo);
                            } else {
                                attr_item_map.put(attr_item.getProd_cate_attr_id(), UtilMisc.toList(item_vo));
                            }
                        }

                        for (ProdCateAttrResponseVO vo : vo_list) {
                            vo.setAttr_item_list(attr_item_map.get(vo.getProd_cate_attr_id()));
                        }
                    }
                }

                Result<ListResponseVO<ProdCateAttrResponseVO>> result = new Result<>(CodeEnum.SUCCESS);
                result.setData(new ListResponseVO(vo_list));
                return result;
            } else {
                return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>());
            }
        } catch (Exception e) {
            log.error("加载分类属性数据失败", e);
            throw new BizException("加载分类属性数据失败", e);
        }
    }

    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result saveCreateProductCategory(ProductCategoryRequestVO product_category) throws BizException, ServerException {
        Result result = saveProductCategoryForBase(product_category);
        if (result.isFailed()) {
            return result;
        }
        if (UtilValidate.isNotEmpty(product_category.getAttr_list())) {
            ProdCateAttrHandleResquestVO prodCateAttrHandleResquestVO = new ProdCateAttrHandleResquestVO();
            prodCateAttrHandleResquestVO.setProduct_category_id(product_category.getCategory_pid());
            prodCateAttrHandleResquestVO.setMany_attr(product_category.getAttr_list());
            Result saveAttrResult = saveManyAttr(prodCateAttrHandleResquestVO);
            if (saveAttrResult.isFailed()) {
                throw new BizException(saveAttrResult.getMsg());
            }
        }
        return new Result(CodeEnum.SUCCESS);
    }

    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result saveProductCategoryForBase(ProductCategoryRequestVO product_category) throws BizException, ServerException {
        if (product_category == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品分类不能是空值");
        }
        if (UtilValidate.isEmpty(product_category.getCategory_name())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "分类名称不能是空值");
        }
        if (UtilValidate.isEmpty(product_category.getCategory_url())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "分类图片URL不能是空值");
        }
        StaffAdminLoginVO admin_login = LoginCache.getStaffAdminLoginVO(product_category.getLogin_token());
        if (admin_login == null) {
            return new Result(CodeEnum.FAIL_SERVER, "获取当前请求用户信息失败: token=" + product_category.getLogin_token());
        }

        //验证商品分类名称是否重名
        if (ProductCategoryUtil.prodCateNameExists(null, product_category.getCategory_name(), null, super.baseMapper)) {
            return new Result(CodeEnum.FAIL_BUSINESS, "分类名称:[" + product_category.getCategory_name() + "]已存在");
        }

        //如果有上级分类，验证上级分类下是否有商品
        if (product_category.getCategory_pid() != null && product_category.getCategory_pid() > 0L && categoryHasProduct(product_category.getCategory_pid())) {
            return new Result(CodeEnum.FAIL_BUSINESS, "父级分类下存在商品,不能在该分类下建立子分类");
        }

        //根据分类的节点情况得到一个合适的排序号
        Integer sort_num;
        try {
            sort_num = calculateAndReturnCateSortNum(product_category.getCategory_pid());
        } catch (Exception e) {
            log.error("自动计算商品分类的排序号失败", e);
            return new Result(CodeEnum.FAIL_BUSINESS, "自动计算商品分类的排序号失败");
        }

        try {
            ProductCategory category = new ProductCategory();
            category.setCategory_name(product_category.getCategory_name().trim());
            category.setCategory_url(product_category.getCategory_url());
            category.setCategory_pid(product_category.getCategory_pid());
            category.setSort_num(sort_num);
            category.setOperator_id(admin_login.getStaff_id());
            category.setCreate_time(new Date());
            category.setUpdate_time(new Date());
            super.insert(category);
            product_category.setCategory_pid(category.getId());

            //商品分类变化后向缓存发出数据需要重新加载的通知
            try {
                redisService.put2(ProductCategoryUtil.PRODUCT_CATEGORY_CHANGED_FLAG, ProductCategoryUtil.NEED_RELOAD, ProductCategoryUtil.NEED_RELOAD_VALUE, null);
            } catch (Exception e) {
                log.error("保存新增商品分类-调用缓存服务发出缓存数据失效通知失败", e);
            }

            return new Result(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.error("新增商品分类处理失败", e);
            throw new BizException("新增商品分类处理失败", e);
        }
    }

    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result saveOneAttr(ProdCateAttrHandleResquestVO one_attr) throws BizException {
        if (one_attr == null || one_attr.getProduct_category_id() == null || one_attr.getProduct_category_id() <= 0) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品分类ID不能是空值");
        }
        if (one_attr.getOne_attr() == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品属性不能是空值,单个处理属性时请注意传参");
        }
        if (!ProductUtil.ENUM_ATTR_TYPE.equals(one_attr.getOne_attr().getAttr_type()) && !ProductUtil.STRING_ATTR_TYPE.equals(one_attr.getOne_attr().getAttr_type())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "无法识别的属性类型: attr_type=" + one_attr.getOne_attr().getAttr_type());
        }
        if (ProductUtil.ENUM_ATTR_TYPE.equals(one_attr.getOne_attr().getAttr_type()) && UtilValidate.isEmpty(one_attr.getOne_attr().getAttr_item_list())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "枚举属性的枚举条目集合不能是空值");
        }

        try {
            ProductCategoryAttr productCategoryAttr = new ProductCategoryAttr();
            productCategoryAttr.setProd_cate_id(one_attr.getProduct_category_id());
            productCategoryAttr.setAttr_type(one_attr.getOne_attr().getAttr_type());
            productCategoryAttr.setAttr_name(one_attr.getOne_attr().getAttr_name());
            productCategoryAttr.insert();

            if (ProductUtil.ENUM_ATTR_TYPE.equals(one_attr.getOne_attr().getAttr_type())) {
                ProdCateEnumAttrRequestVO prodCateEnumAttrRequestVO = new ProdCateEnumAttrRequestVO();
                prodCateEnumAttrRequestVO.setCategory_attr_id(productCategoryAttr.getId());
                prodCateEnumAttrRequestVO.setMany_items(one_attr.getOne_attr().getAttr_item_list());
                Result result = saveManyEnumItem(prodCateEnumAttrRequestVO);
                if (result.isFailed()) {
                    throw new BizException(result.getMsg());
                }
            }
            return new Result(CodeEnum.SUCCESS);
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("保存新增产品分类属性失败", e);
            }
            throw new BizException("保存新增产品分类属性失败", e);
        }
    }

    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result saveManyAttr(ProdCateAttrHandleResquestVO prodCateAttrHandleResquestVO) throws BizException {
        if (prodCateAttrHandleResquestVO == null
                || prodCateAttrHandleResquestVO.getProduct_category_id() == null
                || prodCateAttrHandleResquestVO.getProduct_category_id() <= 0) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品分类ID不能是空值");
        }
        if (UtilValidate.isEmpty(prodCateAttrHandleResquestVO.getMany_attr())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品属性集合不能是空值,批量处理属性时请注意传参");
        }

        Result result = null;
        for (ProdCateAttrRequestVO vo : prodCateAttrHandleResquestVO.getMany_attr()) {
            prodCateAttrHandleResquestVO.setOne_attr(vo);
            result = saveOneAttr(prodCateAttrHandleResquestVO);
            if (result.isFailed()) {
                return result;
            }
        }
        return result;
    }

    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result updateProductCategory(ProductCategoryRequestVO product_category) throws BizException, ServerException {
        Result result = updateProductCategoryForBase(product_category);
        if (result.isFailed()) {
            return result;
        }
        ProdCateAttrHandleResquestVO prodCateAttrHandleResquestVO = new ProdCateAttrHandleResquestVO();
        prodCateAttrHandleResquestVO.setMany_attr(product_category.getAttr_list());
        prodCateAttrHandleResquestVO.setProduct_category_id(product_category.getProduct_category_id());
        result = updateManyAttr(prodCateAttrHandleResquestVO);
        if (result.isFailed()) {
            throw new BizException(result.getMsg());
        }
        return new Result(CodeEnum.SUCCESS);
    }

    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result updateProductCategoryForBase(ProductCategoryRequestVO product_category)throws BizException, ServerException {
        if (product_category == null || product_category.getProduct_category_id() == null || product_category.getProduct_category_id() <= 0) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品分类ID不能是空值");
        }
        if (product_category.getCategory_pid() != null && product_category.getCategory_pid() > 0L && product_category.getProduct_category_id().equals(product_category.getCategory_pid())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品分类的上级分类ID不能是自己");
        }
        if (UtilValidate.isEmpty(product_category.getCategory_name())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品分类名称不能是空值");
        }
        product_category.setCategory_name(product_category.getCategory_name().trim());
        try {
            if (product_category.getCategory_pid() != null && product_category.getCategory_pid() > 0L && categoryHasProduct(product_category.getCategory_pid())) {
                return new Result(CodeEnum.FAIL_BUSINESS, "ID为:[" + product_category.getCategory_pid() + "]的分类下已经有商品,不能在该分类下创建子级分类");
            }

            //加载商品分类
            ProductCategory db_category = super.selectById(product_category.getProduct_category_id());
            if (db_category == null) {
                return new Result(CodeEnum.FAIL_BUSINESS, "不存在ID为:[" + product_category.getProduct_category_id() + "]的商品分类");
            }

            //验证分类名称是否重复
            if (ProductCategoryUtil.prodCateNameExists(db_category.getId(), product_category.getCategory_name(), null, super.baseMapper)) {
                return new Result(CodeEnum.FAIL_BUSINESS, "分类名称:[" + product_category.getCategory_name() + "]已存在");
            }

            db_category.setCategory_name(UtilValidate.isEmpty(db_category.getCategory_name()) ? null : db_category.getCategory_name().trim());

            boolean changed = !product_category.getCategory_name().equals(db_category.getCategory_name());
            if (!changed) {
                Long new_pid = product_category.getCategory_pid() == null ? 0L : product_category.getCategory_pid();
                Long old_pid = db_category.getCategory_pid() == null ? 0L : db_category.getCategory_pid();
                changed = !new_pid.equals(old_pid);
            }


            /**
             * 检查商品分类的节点位置是否发生改变，如果发生改变需要对排序号进行重新设置
             */
            Long new_pid = product_category.getCategory_pid() == null ? 0L : product_category.getCategory_pid();
            Long old_pid = db_category.getCategory_pid() == null ? 0L : db_category.getCategory_pid();
            if (!new_pid.equals(old_pid)) {
                db_category.setSort_num(calculateAndReturnCateSortNum(product_category.getCategory_pid()));
            }

            db_category.setCategory_pid(product_category.getCategory_pid());
            db_category.setCategory_name(product_category.getCategory_name());
            db_category.setCategory_url(product_category.getCategory_url());
            db_category.setUpdate_time(new Date());
            db_category.updateAllColumnById();

            if (changed) {
                /**
                 * 分类名称发生改变时，同步商品的分类名称
                 * 1、得到当前分类的上级分类名称
                 * 2、得到当前分类的下级分类数据（ID与名称）
                 * 3、同步修改商品的分类名称
                 */

                //步骤1
                String parent_cate_name = getParentCateName(db_category, false);

                //步骤2
                List<ProductCategoryResponseVO> son_tree_list = searchSonCateList(db_category.getId());

                if (UtilValidate.isEmpty(parent_cate_name) && UtilValidate.isEmpty(son_tree_list)) {
                    //没有父级分类，也没有子级分类，说明当前分类是个一级分类，直接修改这个分类下的所有商品分类名称
                    productMapper.updateProdCateName(UtilMisc.toMap("category_name", db_category.getCategory_name(), "prod_cate_id", db_category.getId()));
                } else if (UtilValidate.isNotEmpty(parent_cate_name) && UtilValidate.isEmpty(son_tree_list)) {
                    productMapper.updateProdCateName(UtilMisc.toMap("category_name", parent_cate_name + " - " + db_category.getCategory_name(), "prod_cate_id", db_category.getId()));
                } else if (UtilValidate.isEmpty(parent_cate_name) && UtilValidate.isNotEmpty(son_tree_list)) {
                    List<ProductCategoryResponseVO> line_style_list = searchSonCateListAsLineStyle(son_tree_list);
                    for (ProductCategoryResponseVO line_style_item : line_style_list) {
                        productMapper.updateProdCateName(UtilMisc.toMap("category_name", db_category.getCategory_name() + " - " + line_style_item.getCategory_name(), "prod_cate_id", line_style_item.getProduct_category_id()));
                    }
                } else if (UtilValidate.isNotEmpty(parent_cate_name) && UtilValidate.isNotEmpty(son_tree_list)) {
                    List<ProductCategoryResponseVO> line_style_list = searchSonCateListAsLineStyle(son_tree_list);
                    for (ProductCategoryResponseVO line_style_item : line_style_list) {
                        productMapper.updateProdCateName(UtilMisc.toMap("category_name", parent_cate_name + " - " + db_category.getCategory_name() + " - " + line_style_item.getCategory_name(), "prod_cate_id", line_style_item.getProduct_category_id()));
                    }
                }
            }

            //商品分类变化后向缓存发出数据需要重新加载的通知
            try {
                redisService.put2(ProductCategoryUtil.PRODUCT_CATEGORY_CHANGED_FLAG, ProductCategoryUtil.NEED_RELOAD, ProductCategoryUtil.NEED_RELOAD_VALUE, null);
            } catch (Exception e) {
                log.error("保存新增商品分类-调用缓存服务发出缓存数据失效通知失败", e);
            }

            return new Result(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.error("更新商品分类处理失败", e);
            throw new BizException("更新商品分类处理失败", e);
        }
    }

    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result saveManyEnumItem(ProdCateEnumAttrRequestVO prodCateEnumAttrRequestVO) throws BizException {
        if (UtilValidate.isEmpty(prodCateEnumAttrRequestVO.getMany_items())) {
            return new Result(CodeEnum.FAIL_BUSINESS, "枚举条目集合不能是空值");
        }

        for (ProdCateAttrEnumItemRequestVO vo : prodCateEnumAttrRequestVO.getMany_items()) {
            prodCateEnumAttrRequestVO.setOne_item(vo);
            saveOneEnumItem(prodCateEnumAttrRequestVO);
        }

        return new Result(CodeEnum.SUCCESS);
    }

    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result saveOneEnumItem(ProdCateEnumAttrRequestVO prodCateEnumAttrRequestVO) throws BizException {
        if (prodCateEnumAttrRequestVO == null || prodCateEnumAttrRequestVO.getCategory_attr_id() == null || prodCateEnumAttrRequestVO.getCategory_attr_id() <= 0) {
            return new Result(CodeEnum.FAIL_BUSINESS, "属性ID不能是空值");
        }
        if (prodCateEnumAttrRequestVO.getOne_item() == null
                || UtilValidate.isEmpty(prodCateEnumAttrRequestVO.getOne_item().getAttr_value())
                || UtilValidate.isEmpty(prodCateEnumAttrRequestVO.getOne_item().getAttr_value().trim())) {
            return new Result(CodeEnum.FAIL_BUSINESS, "枚举条目值不能是空值");
        }

        try {

            //EntityWrapper wrapper = new EntityWrapper();
            //wrapper.eq("", prodCateEnumAttrRequestVO.getCategory_attr_id());
            //if (categoryAttrMapper.selectCount(wrapper) <= 0) {
            //    return new Result(CodeEnum.FAIL_BUSINESS, "不存在ID为:[" + prodCateEnumAttrRequestVO.getCategory_attr_id() + "]的枚举属性");
            //}

            ProductCategoryAttrItem item = new ProductCategoryAttrItem();
            item.setProd_cate_attr_id(prodCateEnumAttrRequestVO.getCategory_attr_id());
            item.setAttr_value(prodCateEnumAttrRequestVO.getOne_item().getAttr_value().trim());
            item.insert();
            return new Result(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.error("保存新增枚举属性条目处理失败", e);
            throw new BizException("保存新增枚举属性条目处理失败", e);
        }
    }

    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result updateManyAttr(ProdCateAttrHandleResquestVO prodCateAttrHandleResquestVO) throws BizException {
        if (prodCateAttrHandleResquestVO.getProduct_category_id() == null || prodCateAttrHandleResquestVO.getProduct_category_id() <= 0) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "属性ID不能是空值");
        }
        //验证产品分类是否存在
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("id", prodCateAttrHandleResquestVO.getProduct_category_id());
        if (super.selectCount(wrapper) <= 0) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "ID为:[" + prodCateAttrHandleResquestVO.getProduct_category_id() + "]产品分类不存在");
        }

        //加载分类下的所有属性
        wrapper = new EntityWrapper();
        wrapper.eq("prod_cate_id", prodCateAttrHandleResquestVO.getProduct_category_id());
        wrapper.setSqlSelect("id");
        List<ProductCategoryAttr> attr_list = categoryAttrMapper.selectList(wrapper);
        Set<Long> db_id_set = new HashSet<>(UtilValidate.isEmpty(attr_list) ? 0 : attr_list.size());
        if (UtilValidate.isNotEmpty(attr_list)) {
            for (ProductCategoryAttr attr : attr_list) {
                db_id_set.add(attr.getId());
            }
        }

        Set<Long> submit_id_set = new HashSet<>(UtilValidate.isEmpty(prodCateAttrHandleResquestVO.getMany_attr()) ? 0 : prodCateAttrHandleResquestVO.getMany_attr().size());
        if (UtilValidate.isNotEmpty(prodCateAttrHandleResquestVO.getMany_attr())) {
            for (ProdCateAttrRequestVO vo : prodCateAttrHandleResquestVO.getMany_attr()) {
                if (vo.getProd_cate_attr_id() != null && vo.getProd_cate_attr_id() > 0L) {
                    submit_id_set.add(vo.getProd_cate_attr_id());
                }
            }
        }

        try {
            //处理新增和修改的
            if (UtilValidate.isNotEmpty(prodCateAttrHandleResquestVO.getMany_attr())) {
                for (ProdCateAttrRequestVO vo : prodCateAttrHandleResquestVO.getMany_attr()) {
                    if (vo.getProd_cate_attr_id() == null || vo.getProd_cate_attr_id() <= 0L) {
                        prodCateAttrHandleResquestVO.setOne_attr(vo);
                        Result saveOneAttrResult = saveOneAttr(prodCateAttrHandleResquestVO);
                        if (saveOneAttrResult.isFailed()) {
                            throw new BizException(saveOneAttrResult.getMsg());
                        }
                        continue;
                    }
                    Result updateOneAttrResult = updateOneAttr(vo);
                    if (updateOneAttrResult.isFailed()) {
                        throw new BizException(updateOneAttrResult.getMsg());
                    }
                }
            }

            //找出需要删除的
            List<Long> need_to_delete = new ArrayList<>();
            for (Long db_id : db_id_set) {
                if (!submit_id_set.contains(db_id)) {
                    need_to_delete.add(db_id);
                }
            }
            if (UtilValidate.isNotEmpty(need_to_delete)) {
                DeleteManyRequestVO deleteManyRequestVO = new DeleteManyRequestVO();
                deleteManyRequestVO.setId_list(need_to_delete);
                Result deleteManyAttrResult = deleteManyAttr(deleteManyRequestVO);
                if (deleteManyAttrResult.isFailed()) {
                    throw new BizException(deleteManyAttrResult.getMsg());
                }
            }
            return new Result(CodeEnum.SUCCESS);
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("分类属性修改处理失败", e);
            throw new BizException("分类属性修改处理失败", e);
        }
    }

    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result updateOneAttr(ProdCateAttrRequestVO attr) throws BizException {
        Result check = checkProdCateAttr(attr);
        if (check.isFailed()) {
            return check;
        }
        try {
            //加载属性数据
            ProductCategoryAttr categoryAttr = categoryAttrMapper.selectById(attr.getProd_cate_attr_id());
            if (categoryAttr == null) {
                return new Result(CodeEnum.FAIL_BUSINESS, "ID为:[" + attr.getProd_cate_attr_id() + "]的分类属性配置不存在");
            }
            //根据修改后属性类型和修改前的属性类型分别做出处理
            if (attr.getAttr_type().equals(categoryAttr.getAttr_type())) {
                /**属性类型前后一致时，首先考虑属性名称是否变化，如果发生变化需要将所有引用了该分类属性的商品属性中的数据进行同步
                 * 其次在属性类型为枚举时，需要对枚举条目做一次先删除后新增的操作，这个操作不考虑商品属性对这些条目的引用关系
                 */
                if (!attr.getAttr_name().trim().equals(categoryAttr.getAttr_name().trim())) {
                    EntityWrapper wrapper = new EntityWrapper();
                    wrapper.eq("prod_cate_attr_id", attr.getProd_cate_attr_id());
                    ProductAttribute productAttribute = new ProductAttribute();
                    productAttribute.setAttr_name(attr.getAttr_name());
                    prodAttrMapper.update(productAttribute, wrapper);

                    categoryAttr.setAttr_name(attr.getAttr_name());
                    categoryAttr.updateById();
                }

                //枚举属性条目的处理
                if (ProductUtil.ENUM_ATTR_TYPE.equals(attr.getAttr_type())) {
                    ProdCateEnumAttrRequestVO prodCateEnumAttrRequestVO = new ProdCateEnumAttrRequestVO();
                    prodCateEnumAttrRequestVO.setCategory_attr_id(categoryAttr.getId());
                    prodCateEnumAttrRequestVO.setMany_items(attr.getAttr_item_list());
                    updateManyEnumItem(prodCateEnumAttrRequestVO);
                }
            } else {
                //属性类型前后不一致的处理，属性类型发生变化，先删除商品属性对这个属性的所有引用
                prodAttrMapper.deleteByMap(UtilMisc.toMap("prod_cate_attr_id", attr.getProd_cate_attr_id()));
                categoryAttr.setAttr_name(attr.getAttr_name());
                if (categoryAttr.getAttr_type().equals(ProductUtil.ENUM_ATTR_TYPE)) {
                    //删除全部枚举条目
                    attrItemMapper.deleteByMap(UtilMisc.toMap("prod_cate_attr_id", categoryAttr.getId()));
                } else {
                    //新增枚举条目
                    for (ProdCateAttrEnumItemRequestVO vo_item : attr.getAttr_item_list()) {
                        ProductCategoryAttrItem bo_item = new ProductCategoryAttrItem();
                        bo_item.setAttr_value(vo_item.getAttr_value());
                        bo_item.setProd_cate_attr_id(attr.getProd_cate_attr_id());
                        bo_item.insert();
                    }
                }
                categoryAttr.setAttr_type(attr.getAttr_type());
                categoryAttr.updateById();
            }
            return new Result(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.error("保存更新属性处理失败", e);
            throw new BizException("保存更新属性处理失败", e);
        }
    }

    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result deleteProductCategory(ProdCateIdRequestVO prodCateDeleteRequestVO) throws BizException {
        if (prodCateDeleteRequestVO == null || prodCateDeleteRequestVO.getProduct_category_id() == null || prodCateDeleteRequestVO.getProduct_category_id() <= 0) {
            return new Result(CodeEnum.SUCCESS);
        }
        try {
            //验证是否有子分类
            if (categoryHasSon(prodCateDeleteRequestVO.getProduct_category_id())) {
                return new Result(CodeEnum.FAIL_BUSINESS, "删除的商品分类存在下级分类,不能删除");
            }
            //验证分类下是否有商品
            if (categoryHasProduct(prodCateDeleteRequestVO.getProduct_category_id())) {
                return new Result(CodeEnum.FAIL_BUSINESS, "删除的商品分类存在商品,不能删除");
            }
            //加载分类的所有属性，取得所有分类属性的ID集合
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.eq("prod_cate_id", prodCateDeleteRequestVO.getProduct_category_id());
            wrapper.setSqlSelect("id");
            List<ProductCategoryAttr> cate_attr_list = categoryAttrMapper.selectList(wrapper);
            if (UtilValidate.isNotEmpty(cate_attr_list)) {
                List<Long> cate_attr_ids = new ArrayList<>(cate_attr_list.size());
                for (ProductCategoryAttr item : cate_attr_list) {
                    cate_attr_ids.add(item.getId());
                }
                //删除分类下所有属性的属性条目
                wrapper = new EntityWrapper();
                wrapper.in("prod_cate_attr_id", cate_attr_ids);
                attrItemMapper.delete(wrapper);

                //删除分类下所有属性
                categoryAttrMapper.deleteBatchIds(cate_attr_ids);
            }
            //删除分类
            super.deleteById(prodCateDeleteRequestVO.getProduct_category_id());

            //商品分类变化后向缓存发出数据需要重新加载的通知
            try {
                redisService.put2(ProductCategoryUtil.PRODUCT_CATEGORY_CHANGED_FLAG, ProductCategoryUtil.NEED_RELOAD, ProductCategoryUtil.NEED_RELOAD_VALUE, null);
            } catch (Exception e) {
                log.error("删除商品分类-调用缓存服务发出缓存数据失效通知失败", e);
            }
        } catch (Exception e) {
            throw new BizException("删除商品分类处理失败", e);
        }
        return new Result(CodeEnum.SUCCESS);
    }

    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result deleteOneAttr(DeleteOneRequestVO deleteOneAttrRequestVO) throws BizException {
        if (deleteOneAttrRequestVO == null || deleteOneAttrRequestVO.getId() == null || deleteOneAttrRequestVO.getId() <= 0) {
            return new Result(CodeEnum.SUCCESS);
        }
        try {
            //删除商品属性对这些属性的引用
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.eq("prod_cate_attr_id", deleteOneAttrRequestVO.getId());
            prodAttrMapper.delete(wrapper);

            //删除这些属性的条目
            wrapper = new EntityWrapper();
            wrapper.eq("prod_cate_attr_id", deleteOneAttrRequestVO.getId());
            attrItemMapper.delete(wrapper);

            //删除属性
            categoryAttrMapper.deleteById(deleteOneAttrRequestVO.getId());

            return new Result(CodeEnum.SUCCESS);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("删除属性处理失败", e);
            }
            throw new BizException("删除属性处理失败", e);
        }
    }

    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result deleteManyAttr(DeleteManyRequestVO deleteManyAttrRequestVO) throws BizException {
        if (UtilValidate.isEmpty(deleteManyAttrRequestVO)) {
            return new Result(CodeEnum.SUCCESS);
        }
        try {
            //删除商品属性对这些属性的引用
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.in("prod_cate_attr_id", deleteManyAttrRequestVO.getId_list());
            prodAttrMapper.delete(wrapper);

            //删除这些属性的条目
            wrapper = new EntityWrapper();
            wrapper.in("prod_cate_attr_id", deleteManyAttrRequestVO.getId_list());
            attrItemMapper.delete(wrapper);

            //删除属性
            categoryAttrMapper.deleteBatchIds(deleteManyAttrRequestVO.getId_list());
            return new Result(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.error("删除属性处理失败", e);
            throw new BizException("删除属性处理失败", e);
        }
    }

    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result updateOneEnumItem(ProdCateAttrEnumItemRequestVO prodCateAttrEnumItemRequestVO) throws BizException {
        if (prodCateAttrEnumItemRequestVO == null
                ||prodCateAttrEnumItemRequestVO.getAttr_enum_item_id() == null
                || prodCateAttrEnumItemRequestVO.getAttr_enum_item_id() <= 0) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "枚举属性条目ID不能是空值");
        }
        if (UtilValidate.isEmpty(prodCateAttrEnumItemRequestVO.getAttr_value())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "枚举属性条目值不能是空值");
        }
        try {
            ProductCategoryAttrItem db_item = attrItemMapper.selectById(prodCateAttrEnumItemRequestVO.getAttr_enum_item_id());
            if (db_item == null) {
                return new Result(CodeEnum.FAIL_BUSINESS, "不存在ID为:[" + prodCateAttrEnumItemRequestVO.getAttr_enum_item_id() + "]的枚举属性条目");
            }
            //更新条目值
            db_item.setAttr_value(prodCateAttrEnumItemRequestVO.getAttr_value().trim());
            db_item.updateById();

            //更新引用了该条目的商品属性数据
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.eq("prod_cate_attr_item_id", prodCateAttrEnumItemRequestVO.getAttr_enum_item_id());
            ProductAttribute productAttribute = new ProductAttribute();
            productAttribute.setAttr_value(db_item.getAttr_value());
            prodAttrMapper.update(productAttribute, wrapper);
            return new Result(CodeEnum.SUCCESS);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("保存更新枚举属性条目处理失败", e);
            }
            throw new BizException("保存更新枚举属性条目处理失败", e);
        }
    }

    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result updateManyEnumItem(ProdCateEnumAttrRequestVO prodCateEnumAttrRequestVO) throws BizException {
        if (prodCateEnumAttrRequestVO == null || prodCateEnumAttrRequestVO.getCategory_attr_id() == null || prodCateEnumAttrRequestVO.getCategory_attr_id() <= 0) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "枚举属性ID不能是空值");
        }

        if (UtilValidate.isEmpty(prodCateEnumAttrRequestVO.getMany_items())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "枚举属性条目集合不能是空值");
        }

        ProductCategoryAttr attr = categoryAttrMapper.selectById(prodCateEnumAttrRequestVO.getCategory_attr_id());
        if (attr == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "不存在ID为:[" + prodCateEnumAttrRequestVO.getCategory_attr_id() + "]的枚举属性");
        }
        if (!ProductUtil.ENUM_ATTR_TYPE.equals(attr.getAttr_type())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "ID为:[" + prodCateEnumAttrRequestVO.getCategory_attr_id() + "]的产品分类属性不是枚举类型的属性");
        }

        //找出修改的，有ID的就是修改的
        Set<Long> submit_item_id_set = new HashSet<>(prodCateEnumAttrRequestVO.getMany_items().size());
        for (ProdCateAttrEnumItemRequestVO vo : prodCateEnumAttrRequestVO.getMany_items()) {
            if (vo.getAttr_enum_item_id() != null && vo.getAttr_enum_item_id() > 0) {
                submit_item_id_set.add(vo.getAttr_enum_item_id());
            }
        }

        //加载所有枚举条目值
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.setSqlSelect("id");
        wrapper.eq("prod_cate_attr_id", attr.getId());
        List<ProductCategoryAttrItem> db_item_list = attrItemMapper.selectList(wrapper);
        Set<Long> db_item_id_set = new HashSet<>(UtilValidate.isEmpty(db_item_list) ? 0 : db_item_list.size());
        for (ProductCategoryAttrItem db_item : db_item_list) {
            db_item_id_set.add(db_item.getId());
        }

        //找出需要删除的条目
        List<Long> need_to_delete = new ArrayList<>();
        for (Long db_id : db_item_id_set) {
            if (!submit_item_id_set.contains(db_id)) {
                need_to_delete.add(db_id);
            }
        }

        //处理新增的条目和修改的条目
        for (ProdCateAttrEnumItemRequestVO vo : prodCateEnumAttrRequestVO.getMany_items()) {
            if (vo.getAttr_enum_item_id() != null && vo.getAttr_enum_item_id() > 0) {
                updateOneEnumItem(vo);
                continue;
            }
            prodCateEnumAttrRequestVO.setOne_item(vo);
            saveOneEnumItem(prodCateEnumAttrRequestVO);
        }

        //处理需要删除的
        if (UtilValidate.isNotEmpty(need_to_delete)) {
            DeleteManyRequestVO deleteManyRequestVO = new DeleteManyRequestVO();
            deleteManyRequestVO.setId_list(need_to_delete);
            deleteManyEnumItem(deleteManyRequestVO);
        }

        return new Result(CodeEnum.SUCCESS);
    }

    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result deleteOneEnumItem(DeleteOneRequestVO deleteOneRequestVO) throws BizException {
        if (deleteOneRequestVO == null || deleteOneRequestVO.getId() == null || deleteOneRequestVO.getId() <= 0) {
            return new Result(CodeEnum.SUCCESS);
        }
        try {
            //删除商品属性对这个枚举条目的引用
            prodAttrMapper.deleteByMap(UtilMisc.toMap("prod_cate_attr_item_id", deleteOneRequestVO.getId()));
            //删除枚举属性条目
            attrItemMapper.deleteById(deleteOneRequestVO.getId());
            return new Result(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.error("删除枚举属性条目处理失败", e);
            throw new BizException("删除枚举属性条目处理失败", e);
        }
    }

    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result deleteManyEnumItem(DeleteManyRequestVO deleteManyRequestVO) throws BizException {
        if (deleteManyRequestVO == null || UtilValidate.isEmpty(deleteManyRequestVO.getId_list())) {
            return new Result(CodeEnum.SUCCESS);
        }
        DeleteOneRequestVO deleteOneRequestVO = new DeleteOneRequestVO();
        for (Long id : deleteManyRequestVO.getId_list()) {
            deleteOneRequestVO.setId(id);
            Result result = deleteOneEnumItem(deleteOneRequestVO);
            if (result.isFailed()) {
                return result;
            }
        }
        return new Result(CodeEnum.SUCCESS);
    }

    /**
     * @author 王光银
     * @methodName: upProdCateSort
     * @methodDesc: 上移一个商品分类的展示位置
     * @description:
     * @param: [idRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-07-11 15:17
     **/
    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result upProdCateSort(IdRequestVO idRequestVO) throws ServerException, BizException {
        return upDownProdCateSort(idRequestVO.getId(), 1);
    }

    /**
     * @author 王光银
     * @methodName: downProdCateSort
     * @methodDesc: 下移一个商品分类的展示位置
     * @description:
     * @param: [idRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-07-11 15:17
     **/
    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result downProdCateSort(IdRequestVO idRequestVO) throws ServerException, BizException {
        return upDownProdCateSort(idRequestVO.getId(), 0);
    }

    /**
     * @author 王光银
     * @methodName: upDownProdCateSort
     * @methodDesc: 上移或下移一个商品分类的展示顺序
     * @description: up_down 1上移，其他任意值下移
     * @param:[prod_cate_id, up_down]
     * @return
     * @create 2018-07-11 15:26
     **/
    private Result upDownProdCateSort(Long prod_cate_id, int up_down) {
        if (prod_cate_id == null || prod_cate_id <= 0L) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品分类ID不能是空值");
        }
        ProductCategory productCategory = super.selectById(prod_cate_id);
        if (productCategory == null) {
            return new Result(CodeEnum.FAIL_BUSINESS, "不存在ID:[" + prod_cate_id + "]的商品分类");
        }

        EntityWrapper wrapper = new EntityWrapper();
        if (productCategory.getCategory_pid() == null || productCategory.getCategory_pid() <= 0L) {
            wrapper.isNull("category_pid");
        } else {
            wrapper.eq("category_pid", productCategory.getCategory_pid());
        }

        //1上移，其他任意值下移
        if (1 == up_down) {
            wrapper.orderBy("sort_num", true);
            wrapper.gt("sort_num", productCategory.getSort_num());
        } else {
            wrapper.orderBy("sort_num", false);
            wrapper.lt("sort_num", productCategory.getSort_num());
        }

        List<ProductCategory> list = super.selectList(wrapper);
        if (UtilValidate.isEmpty(list)) {
            return new Result(CodeEnum.SUCCESS);
        }
        ProductCategory exchange_target = list.remove(0);
        int exchange_target_sort_num = exchange_target.getSort_num();
        exchange_target.setSort_num(productCategory.getSort_num());
        productCategory.setSort_num(exchange_target_sort_num);

        productCategory.updateById();
        exchange_target.updateById();

        //商品分类变化后向缓存发出数据需要重新加载的通知
        try {
            redisService.put2(ProductCategoryUtil.PRODUCT_CATEGORY_CHANGED_FLAG, ProductCategoryUtil.NEED_RELOAD, ProductCategoryUtil.NEED_RELOAD_VALUE, null);
        } catch (Exception e) {
            log.error("修改商品分类排序-调用缓存服务发出缓存数据失效通知失败", e);
        }

        return new Result(CodeEnum.SUCCESS);
    }

    public Boolean categoryHasProduct(Long category_id) {
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("prod_cate_id", category_id);
        if (productMapper.selectCount(wrapper) > 0) {
            return true;
        }
        return false;
    }

    public Boolean categoryHasSon(Long category_id) {
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("category_pid", category_id);
        if (super.selectCount(wrapper) > 0) {
            return true;
        }
        return false;
    }

    public String generateCategoryTreeDesc(Long category_id) {
        if (category_id == null || category_id <= 0) {
            return null;
        }
        ProductCategory category = super.selectById(category_id);
        if (category == null) {
            return null;
        }
        StringBuilder str = new StringBuilder(category.getCategory_name());
        while (category.getCategory_pid() != null && category.getCategory_pid() > 0L) {
            category = super.selectById(category.getCategory_pid());
            if (category == null) {
                break;
            }
            str.insert(0, category.getCategory_name() + " - ");
        }
        return str.toString();
    }

    private Result checkProdCateAttr(ProdCateAttrRequestVO attr) {
        if (attr == null || attr.getProd_cate_attr_id() == null || attr.getProd_cate_attr_id() <= 0) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "属性ID不能是空值");
        }
        if (UtilValidate.isEmpty(attr.getAttr_name())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "属性名称不能是空值");
        }
        if (UtilValidate.isEmpty(attr.getAttr_type())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "属性类型不能是空值");
        }
        if (!ProductUtil.ENUM_ATTR_TYPE.equals(attr.getAttr_type()) && !ProductUtil.STRING_ATTR_TYPE.equals(attr.getAttr_type())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "无法识别的属性类型: attr_type=" + attr.getAttr_type());
        }
        if (ProductUtil.ENUM_ATTR_TYPE.equals(attr.getAttr_type()) && UtilValidate.isEmpty(attr.getAttr_item_list())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "属性类型为枚举时，枚举条目集合不能是空值");
        }
        return new Result(CodeEnum.SUCCESS);
    }

    private Integer calculateAndReturnCateSortNum(Long parent_cate_id) {
        Integer sort_num = super.baseMapper.selectMaxNumByCondition(parent_cate_id);
        return sort_num == null ? 1 : sort_num + 1;
    }

    private String getParentCateName(ProductCategory current_category, boolean parent_empty_return_self) {
        if (current_category.getId() == null || current_category.getId() <= 0) {
            return "";
        }
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.setSqlSelect("category_name", "category_pid");
        wrapper.eq("id", current_category.getId());
        ProductCategory thisSelf = super.selectOne(wrapper);
        if (thisSelf.getCategory_pid() == null || thisSelf.getCategory_pid() <= 0L) {
            if (parent_empty_return_self) {
                return thisSelf.getCategory_name();
            }
            return "";
        }

        wrapper = new EntityWrapper();
        wrapper.setSqlSelect("category_name", "category_pid");
        wrapper.eq("id", thisSelf.getCategory_pid());
        ProductCategory parent = super.selectOne(wrapper);
        if (parent == null) {
            return parent_empty_return_self ? thisSelf.getCategory_name() : "";
        }
        if (parent.getCategory_pid() != null && parent.getCategory_pid() > 0L) {
            ProductCategory thisParent = new ProductCategory();
            thisParent.setId(parent.getCategory_pid());
            return getParentCateName(thisParent, true) + " - " + parent.getCategory_name();
        }
        return UtilValidate.isEmpty(parent.getCategory_name()) ? "" : parent.getCategory_name();
    }

    private List<ProductCategoryResponseVO> searchSonCateList(Long parent_cate_id) {
        try {
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.setSqlSelect("id", "category_name");
            wrapper.eq("category_pid", parent_cate_id);
            List<ProductCategory> category_list = super.selectList(wrapper);
            if (UtilValidate.isEmpty(category_list)) {
                return null;
            }

            //组织树形结构
            List<ProductCategoryResponseVO> tree_list = new ArrayList<>(category_list.size());

            for (ProductCategory category : category_list) {
                ProductCategoryResponseVO vo = new ProductCategoryResponseVO();
                tree_list.add(vo);

                vo.setProduct_category_id(category.getId());
                vo.setCategory_name(category.getCategory_name());
                if (categoryHasSon(category.getId())) {
                    vo.setSub_list(searchSonCateList(category.getId()));
                }
            }

            return tree_list;
        } catch (Exception e) {
            log.error("加载分类数据树结构失败", e);
            throw new BizException("加载分类数据树结构失败", e);
        }
    }

    private List<ProductCategoryResponseVO> searchSonCateListAsLineStyle(List<ProductCategoryResponseVO> tree_list) {
        List<ProductCategoryResponseVO> line_style_list = new ArrayList<>(tree_list.size() * 5);
        for (ProductCategoryResponseVO treeItem : tree_list) {
            if (UtilValidate.isEmpty(treeItem.getSub_list())) {
                line_style_list.add(treeItem);
            } else {
                for (ProductCategoryResponseVO sub_list_item : treeItem.getSub_list()) {
                    sub_list_item.setCategory_name(treeItem.getCategory_name() + " - " + sub_list_item.getCategory_name());
                }
                line_style_list.addAll(searchSonCateListAsLineStyle(treeItem.getSub_list()));
            }
        }
        return line_style_list;
    }
}
