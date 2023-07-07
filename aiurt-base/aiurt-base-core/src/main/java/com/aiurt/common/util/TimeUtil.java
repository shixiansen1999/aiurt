package com.aiurt.common.util;

import cn.hutool.core.util.ObjectUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 时间工具类
 * @author zwl
 */
public class TimeUtil {
    private final static SimpleDateFormat SHORT_SDF = new SimpleDateFormat("yyyy-MM-dd");
    private final static SimpleDateFormat LONG_SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 获得本周的第一天，周一
     *
     * @return
     */
    public static Date getCurrentWeekDayStartTime() {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        try {
            //周一
            c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            c.setTime(LONG_SDF.parse(SHORT_SDF.format(c.getTime()) + " 00:00:00"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c.getTime();
    }

    /**
     * 获得本周的最后一天，周日
     *
     * @return
     */
    public static Date getCurrentWeekDayEndTime() {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        try {
            //周日
            c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            c.setTime(LONG_SDF.parse(SHORT_SDF.format(c.getTime()) + " 23:59:59"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c.getTime();
    }

    /**
     * 本年的开始时间
     *
     * @return
     */
    public static Date getCurrentYearStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.YEAR));
        return cal.getTime();
    }

    /**
     * 本年的结束时间
     *
     * @return
     */
    public static Date getCurrentYearEndTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getCurrentYearStartTime());
        cal.add(Calendar.YEAR, 1);
        return cal.getTime();
    }

    /**
     * 获取时间差
     */
    public static String dateDiff(Date startTime, Date endTime) {
        if (ObjectUtil.isEmpty(startTime) || ObjectUtil.isEmpty(endTime)) {
            return null;
        }
        long diff = endTime.getTime() - startTime.getTime();
        long days = diff / (1000 * 60 * 60 * 24);
        //获取时
        long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
        long s = (diff / 1000 - days * 24 * 60 * 60 - hours * 60 * 60 - minutes * 60);
        return days + "天" + hours + "时" + minutes + "分" + s + "秒";
    }


    /**
     * 获取上周周几的日期,默认一周从周一开始
     * @param dayOfWeek
     * @param weekOffset
     * @return
     */
    public static Date getDayOfWeek(int dayOfWeek,int weekOffset){
        return getDayOfWeek(Calendar.MONDAY,dayOfWeek,weekOffset);
    }

    /**
     * 获取上(下)周周几的日期
     * @param firstDayOfWeek {@link Calendar}
     * 值范围 <code>SUNDAY</code>,
     * <code>MONDAY</code>, <code>TUESDAY</code>, <code>WEDNESDAY</code>,
     * <code>THURSDAY</code>, <code>FRIDAY</code>, and <code>SATURDAY</code>
     * @param dayOfWeek {@link Calendar}
     * @param weekOffset  周偏移，上周为-1，本周为0，下周为1，以此类推
     * @return
     */
    public static Date getDayOfWeek(int firstDayOfWeek,int dayOfWeek,int weekOffset){
        if(dayOfWeek>Calendar.SATURDAY || dayOfWeek<Calendar.SUNDAY){
            return null;
        }
        if(firstDayOfWeek>Calendar.SATURDAY || firstDayOfWeek < Calendar.SUNDAY){
            return null;
        }
        Calendar date=Calendar.getInstance(Locale.CHINA);
        date.setFirstDayOfWeek(firstDayOfWeek);
        //周数减一，即上周
        date.add(Calendar.WEEK_OF_MONTH,weekOffset);
        //日子设为周几
        date.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        //时分秒全部置0
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        try {
            date.setTime(LONG_SDF.parse(SHORT_SDF.format(date.getTime()) + " 00:00:00"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static Date getFirstDay(int month) {
        // 获取Calendar类的实例
        Calendar c = Calendar.getInstance();
        // 设置月份，因为月份从0开始，所以用month - 1
        c.set(Calendar.MONTH, month - 1);
        // 设置日期
        c.set(Calendar.DAY_OF_MONTH, 1);

        return c.getTime();
    }

    public static Date getLastDay(int month) {
        // 获取Calendar类的实例
        Calendar c = Calendar.getInstance();
        // 设置月份，因为月份从0开始，所以用month - 1
        c.set(Calendar.MONTH, month - 1);
        // 获取当前时间下，该月的最大日期的数字
        int lastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        // 将获取的最大日期数设置为Calendar实例的日期数
        c.set(Calendar.DAY_OF_MONTH, lastDay);

        return c.getTime();
    }

    /*public static void main(String[] args) {
        System.out.println(getFirstDay(1));
        System.out.println(getLastDay(2));
    }*/
    /**时间格式校验*/
    public static boolean isLegalDate(int length, String sDate,String format) {
        if ((sDate == null) || (sDate.length() != length)) {
            return false;
        }
        DateFormat formatter = new SimpleDateFormat(format);
        try {
            Date date = formatter.parse(sDate);
            return sDate.equals(formatter.format(date));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 时间转化，转成1天2时5分9秒这种形式
     * @param second 输入时间，单位：秒
     * @return 转成的时间，转成1天2时5分9秒这种形式
     */
    public static String translateTime(Integer second){
        if (ObjectUtil.isNull(second)){
            return null;
        }
        // 天
        int days = second / (24 * 60 * 60);
        // 时，对24 * 60 * 60取余数(只有对24 * 60 * 60的余数部分才会转化成时，大于24 * 60 * 60的部分会转化成天)，再除以3600转成时
        int hours = (second % (24 * 60 * 60)) / (60 * 60);
        // 分，对3600取余数(只有对3600的余数部分才会转化成分，大于3600的部分会转化成时)，再除以60转成分
        int minutes = (second % 3600) / 60;
        // 秒，直接对60取余
        int seconds = second % 60;

        StringBuilder timeStringBuilder = new StringBuilder();
        if (days > 0) {
            timeStringBuilder.append(days).append("天");
        }
        if (hours > 0) {
            timeStringBuilder.append(hours).append("小时");
        }
        if (minutes > 0) {
            timeStringBuilder.append(minutes).append("分");
        }
        if (seconds > 0) {
            timeStringBuilder.append(seconds).append("秒");
        }
        if (days == 0 && hours == 0 && minutes == 0 && second == 0){
            return "0秒";
        }
        return timeStringBuilder.toString();
    }
}
