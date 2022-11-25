package com.aiurt.boot.constant;

/**
 * 施工模块常量
 */
public interface ConstructionConstant {
    /**
     * 周计划令待提审状态
     */
    Integer FORM_STATUS_0 = 0;
    /**
     * 周计划令待审核状态
     */
    Integer FORM_STATUS_1 = 1;
    /**
     * 周计划令审核中状态
     */
    Integer FORM_STATUS_2 = 2;
    /**
     * 周计划令已驳回状态
     */
    Integer FORM_STATUS_3 = 3;
    /**
     * 周计划令已取消状态
     */
    Integer FORM_STATUS_4 = 4;
    /**
     * 周计划令已通过状态
     */
    Integer FORM_STATUS_5 = 5;
    /**
     * 角色未审批状态
     */
    Integer APPROVE_STATUS_0 = 0;
    /**
     * 角色同意状态
     */
    Integer APPROVE_STATUS_1 = 1;
    /**
     * 角色驳回状态
     */
    Integer APPROVE_STATUS_2 = 2;
    /**
     * 计划类型-正常计划
     */
    Integer PLAN_TYPE_1 = 1;
    /**
     * 计划类型-日补充计划
     */
    Integer PLAN_TYPE_2 = 2;
    /**
     * 计划类型-临时补修计划
     */
    Integer PLAN_TYPE_3 = 3;
}
