package com.aiurt.boot.constant;

/**
 * 固定资产管理常量类
 */
public interface FixedAssetsConstant {
    /**
     * 盘点状态-待下发
     */
    Integer STATUS_0 = 0;
    /**
     * 盘点状态-执行中
     */
    Integer STATUS_1 = 1;
    /**
     * 盘点状态-待审核
     */
    Integer STATUS_2 = 2;
    /**
     * 盘点状态-已完成
     */
    Integer STATUS_3 = 3;
    /**
     * 盘点结果-盘平
     */
    Integer CHECK_RESULT_0 = 0;
    /**
     * 盘点结果-盘盈
     */
    Integer CHECK_RESULT_1 = 1;
    /**
     * 盘点结果-盘亏
     */
    Integer CHECK_RESULT_2 = 2;
    /**
     * 审核结果-驳回
     */
    Integer AUDIT_RESULT_0 = 0;
    /**
     * 审核结果-通过
     */
    Integer AUDIT_RESULT_1 = 1;
}
