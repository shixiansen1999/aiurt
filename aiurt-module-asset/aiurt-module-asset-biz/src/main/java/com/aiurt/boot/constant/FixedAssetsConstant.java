package com.aiurt.boot.constant;

/**
 * 固定资产管理常量类
 */
public interface FixedAssetsConstant {
    /**
     * 盘点状态-待下发
     */
    Integer status_0 = 0;
    /**
     * 盘点状态-执行中
     */
    Integer status_1 = 1;
    /**
     * 盘点状态-待审核
     */
    Integer status_2 = 2;
    /**
     * 盘点状态-已完成
     */
    Integer status_3 = 3;
    /**
     * 盘点结果-盘平
     */
    Integer check_result_0 = 0;
    /**
     * 盘点结果-盘盈
     */
    Integer check_result_1 = 1;
    /**
     * 盘点结果-盘亏
     */
    Integer check_result_2 = 2;
}
