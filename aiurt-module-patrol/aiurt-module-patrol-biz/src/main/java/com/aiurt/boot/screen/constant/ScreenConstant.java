package com.aiurt.boot.screen.constant;
/**
 * @author JB
 * @Description: 大屏巡视模块业务常量
 */
public interface ScreenConstant {
    /**
     * 本周
     */
    Integer THIS_WEEK = 1;
    /**
     * 上周
     */
    Integer LAST_WEEK = 2;
    /**
     * 本月
     */
    Integer THIS_MONTH = 3;
    /**
     * 上个月
     */
    Integer LAST_MONTH = 4;
    /**
     * 获取本周、上周、本月、上月的时间范围的时间分隔符
     */
    String TIME_SEPARATOR = "~";
}
