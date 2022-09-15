package com.aiurt.modules.largescream.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.aiurt.modules.largescream.constant.FaultLargeConstant;


import java.util.Date;

/**
 * 大屏巡视模块获取本周、上周、本月、上月的时间工具类
 */
public class FaultLargeDateUtil {

    /**
     * 获取参数日期所在周时间范围，格式如下:yyyy-MM-dd 00:00:00~yyyy-MM-dd 23:59:59
     *
     * @param date
     * @return
     */
    public static String getThisWeek(Date date) {
        DateTime start = DateUtil.beginOfWeek(date);
        DateTime end = DateUtil.endOfWeek(date);
        String thisWeek = DateUtil.format(start, "yyyy-MM-dd 00:00:00") + "~" + DateUtil.format(end, "yyyy-MM-dd 23:59:59");
        return thisWeek;
    }

    /**
     * 获取参数日期上周时间范围，格式如下:yyyy-MM-dd 00:00:00~yyyy-MM-dd 23:59:59
     *
     * @param date
     * @return
     */
    public static String getLastWeek(Date date) {
        date = DateUtil.offsetWeek(date, -1);
        DateTime start = DateUtil.beginOfWeek(date);
        DateTime end = DateUtil.endOfWeek(date);
        String lastWeek = DateUtil.format(start, "yyyy-MM-dd 00:00:00") + "~" + DateUtil.format(end, "yyyy-MM-dd 23:59:59");
        return lastWeek;
    }

    /**
     * 获取参数日期所在月时间范围，格式如下:yyyy-MM-dd 00:00:00~yyyy-MM-dd 23:59:59
     *
     * @param date
     * @return
     */
    public static String getThisMonth(Date date) {
        DateTime start = DateUtil.beginOfMonth(date);
        DateTime end = DateUtil.endOfMonth(date);
        String thisMonth = DateUtil.format(start, "yyyy-MM-dd 00:00:00") + "~" + DateUtil.format(end, "yyyy-MM-dd 23:59:59");
        return thisMonth;
    }

    /**
     * 获取参数日期上个月时间范围，格式如下:yyyy-MM-dd 00:00:00~yyyy-MM-dd 23:59:59
     *
     * @param date
     * @return
     */
    public static String getLastMonth(Date date) {
        date = DateUtil.offsetMonth(date, -1);
        DateTime start = DateUtil.beginOfMonth(date);
        DateTime end = DateUtil.endOfMonth(date);
        String lastMonth = DateUtil.format(start, "yyyy-MM-dd 00:00:00") + "~" + DateUtil.format(end, "yyyy-MM-dd 23:59:59");
        return lastMonth;
    }

    /**
     * 根据时间类型获取时间范围,1本周、2上周、3本月、4上月
     */
    public static String getDateTime(Integer timeType) {
        // 默认本周
        String date = getThisWeek(new Date());
        if (FaultLargeConstant.LAST_WEEK.equals(timeType)) {
            date = getLastWeek(new Date());
        } else if (FaultLargeConstant.THIS_MONTH.equals(timeType)) {
            date = getThisMonth(new Date());
        } else if (FaultLargeConstant.LAST_MONTH.equals(timeType)) {
            date = getLastMonth(new Date());
        }
        return date;
    }
}
