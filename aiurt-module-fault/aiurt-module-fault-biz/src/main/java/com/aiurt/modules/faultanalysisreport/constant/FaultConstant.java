package com.aiurt.modules.faultanalysisreport.constant;
/**
 * 2022/6/29
 * 常用常量
 */
public class FaultConstant {
    /**管理员角色*/
    public final static String ADMIN = "admin";
    /**维修工班长角色*/
    public final static String MAINTENANCE_WORKER = "banzhang";
    /**专业技术负责人角色*/
    public final static String PROFESSIONAL_TECHNICAL_DIRECTOR = "jishuyuan";

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
