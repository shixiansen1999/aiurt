package com.aiurt.modules.largescream.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.aiurt.modules.largescream.constant.FaultLargeConstant;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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



    /**
     *获取指定月份的开始日期和结束日期
     * @author: lkj
     */
    public static Map<String, String> getMonthFirstAndLast(Integer month) {
        HashMap<String, String> map = new HashMap<>(2);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH,month);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        String firstDay = sdf().format(cal.getTime());

        Calendar cal2 = Calendar.getInstance();
        cal2.set(Calendar.MONTH,month);
        cal2.set(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH), cal2.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        cal2.set(Calendar.DAY_OF_MONTH, cal2.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal2.set(Calendar.HOUR_OF_DAY, 23);
        String lastDay = sdf().format(cal2.getTime());

        map.put("firstDay", firstDay);
        map.put("lastDay", lastDay);

        return map;
    }

    public static SimpleDateFormat sdf() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

}
