package com.aiurt.modules.faultanalysisreport.constants;
/**
 * 2022/6/29
 * @author: lkj
 * 常用常量
 */
public interface FaultConstant {
    /**待审批*/
    public final static Integer PENDING = 0;

    /**已审批*/
    public final static Integer APPROVED = 1;

    /**已驳回*/
    public final static Integer REJECTED = 2;

    /**已通过*/
    public final static Integer PASSED = 1;

    /**未通过*/
    public final static Integer NO_PASS = 0;
}
