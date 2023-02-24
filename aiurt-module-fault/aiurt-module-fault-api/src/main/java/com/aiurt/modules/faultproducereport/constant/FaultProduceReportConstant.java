package com.aiurt.modules.faultproducereport.constant;

public interface FaultProduceReportConstant {
    /**
     * 待提交
     */
    public static final Integer TO_SUBMITTED = 0;

    /**
     * 技术员审核
     */
    public static final Integer TE_PASS = 1;

    /**
     * 技术员驳回
     */
    public static final Integer TE_REVIEW = 2;

    /**
     * 分部主任审核
     */
    public static final Integer HD_PASS = 3;

    /**
     * 分部主任驳回
     */
    public static final Integer HD_REJECTED = 4;


    /**
     * 已通过
     */
    public static final Integer PASS = 5;

}
