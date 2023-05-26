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
    /**
     * 大屏温湿度查询模式：0获取当前整点时刻的温湿度
     */
    int MODE_0 = 0;
    /**
     * 大屏温湿度查询模式：1获取当天(每个整点时刻)
     */
    int MODE_1 = 1;
    /**
     * 大屏温湿度查询模式：2获取近一周（每天14点整）
     */
    int MODE_2 = 2;
    /**
     * 大屏温湿度查询模式：3获取近30天（每天14点整）
     */
    int MODE_3 = 3;
    /**
     * 时刻：14点整
     */
    String HOUR_14 = "14";
    /**
     * 获取近一周时
     * 时间间隔：7天
     */
    Integer INTERVAL_7 = 7;
    /**
     * 获取近30天时
     * 时间间隔：30天
     */
    Integer INTERVAL_30 = 30;
}
