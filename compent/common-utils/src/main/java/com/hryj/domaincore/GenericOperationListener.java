package com.hryj.domaincore;

/**
 * @author 王光银
 * @className: GenericOperationListener
 * @description:
 * @create 2018/8/27 0027 14:35
 **/
public interface GenericOperationListener {

    /**
     * 监听器描述
     * @return
     */
    String getDescription();

    /**
     * 是否执行这个监听操作,  true执行，false不执行
     * @return
     */
    boolean isExec();

    /**
     * 执行领域对象的某一个操作前的监听处理
     * @param model
     */
    ExecuteOperationResult toDo(GenericDomainModel model);

}
