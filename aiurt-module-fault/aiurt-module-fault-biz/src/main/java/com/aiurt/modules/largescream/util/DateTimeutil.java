package com.aiurt.modules.largescream.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022-09-13 18:17
 */
public class DateTimeutil {
    public static SimpleDateFormat sdf() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    //获取当天的开始时间
    public static String getDayBegin() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return sdf().format(cal.getTime());
    }

    public static void main(String[] args) {
        System.out.println("今天0点时间：" + getDayBegin());
        System.out.println("今天24点时间：" + getDayEnd());
    }

    //获取当天的结束时间
    public static String getDayEnd() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return sdf().format(cal.getTime());
    }

    //获取昨天的开始时间
    public static String getBeginDayOfYesterday() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return sdf().format(cal.getTime());
    }

    //获取昨天的结束时间
    public static String getEndDayOfYesterDay() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return sdf().format(cal.getTime());
    }

    // 获得本周一0点时间
    public static String getTimesWeekmorning() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        Calendar c = Calendar.getInstance();
        int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0) {
            day_of_week = 7;
        }
        c.add(Calendar.DATE, -day_of_week + 1);
        return format.format(c.getTime());
    }

    // 获得本周日24点时间
    public static String getTimesWeeknight() {
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        Calendar c = Calendar.getInstance();
        int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0){
            day_of_week = 7;
        }
        c.add(Calendar.DATE, -day_of_week + 7);
        return formatDate.format(c.getTime());
    }

    //根据当前日期获得最近n周的日期区间（不包含本周）
    public static String getFromToDate(SimpleDateFormat sdf, Date date, int n, int option, int k) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int offset = 0 == option ? 1 - dayOfWeek : 7 - dayOfWeek;
        int amount = 0 == option ? offset - (n - 1  + k) * 7 : offset - k * 7;
        calendar.add(Calendar.DATE, amount);
        return sdf.format(calendar.getTime());
    }

    // 获取上周的开始时间
    public static String getBeginDayOfLastWeek() {
        //上周日期
        SimpleDateFormat sdf = sdf();
        String beginDate = getFromToDate(sdf, new Date(), 1, 0, 1);

        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(beginDate));
        }catch (Exception e){
            e.printStackTrace();
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return sdf.format(calendar.getTime());
    }

    // 获取上周的结束时间
    public static String getEndDayOfLastWeek() {
        //上周日期
        SimpleDateFormat sdf = sdf();
        String endDate = getFromToDate(sdf, new Date(), 1, 1, 1);

        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(endDate));
        }catch (Exception e){
            e.printStackTrace();
        }
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return sdf.format(calendar.getTime());
    }

    // 获得本月第一天0点时间
    public static String getTimesMonthmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return sdf().format(cal.getTime());
    }

    // 获得本月最后一天24点时间
    public static String getTimesMonthnight() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        return sdf().format(cal.getTime());
    }

    // 获得上月第一天0点时间
    public static String getTimesLastMonthmorning() {
        //上月日期
        Calendar c=Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        SimpleDateFormat sdf = sdf();
        String gtimelast = sdf.format(c.getTime()); //上月
        int lastMonthMaxDay=c.getActualMaximum(Calendar.DAY_OF_MONTH);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), lastMonthMaxDay, 23, 59, 59);

        //按格式输出
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-01 00:00:00");
        String gtime = sdf2.format(c.getTime()); //上月第一天
        return gtime;
    }
    // 获得上月最后一天24点时间
    public static String getTimesLastMonthnight() {
        //上月日期
        Calendar c=Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        SimpleDateFormat sdf = sdf();
        String gtimelast = sdf.format(c.getTime()); //上月
        int lastMonthMaxDay=c.getActualMaximum(Calendar.DAY_OF_MONTH);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), lastMonthMaxDay, 23, 59, 59);

        //按格式输出
        String gtime = sdf.format(c.getTime()); //上月最后一天
        return gtime;
    }
}
