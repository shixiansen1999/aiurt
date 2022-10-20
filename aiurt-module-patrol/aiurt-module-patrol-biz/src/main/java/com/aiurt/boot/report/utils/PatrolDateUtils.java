package com.aiurt.boot.report.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/9/26
 * @desc
 */
public class PatrolDateUtils {

    /**
     * 计算周数
     * @param startDate
     * @param endDate
     * @return
     */
    public static long countTwoDayWeek(String startDate, String endDate)
    {
        String nowDay = DateUtil.format(new Date(), "yyyy-MM-dd");
        Date todayDate = DateUtil.parse(nowDay);
        Date start = DateUtil.parse(startDate, "yyyy-MM-dd");
        Date end = DateUtil.parse(endDate, "yyyy-MM-dd");
        //开始时间大于等于当前时间
        if (start.after(todayDate) || start.equals(todayDate)) {
            return 0;
        }
        //开始时间小于当前时间
        else {
            //结束时间小于当前时间
            if (end.before(todayDate)) {
                int startYear = DateUtil.year(start);
                int endYear = DateUtil.year(end);
                //结束年份大于开始年份
                if (endYear > startYear) {
                    //当前时间的周一
                    Date nowMonday = DateUtil.beginOfWeek(new Date());
                    //结束时间的周一
                    Date endMonday = DateUtil.beginOfWeek(end);
                    //同一天
                    boolean endSameTime = DateUtil.isSameTime(nowMonday, endMonday);
                    //开始时间的周一
                    DateTime startMonday = DateUtil.beginOfWeek(start);
                    long betweenDay = 0;
                    if (endSameTime) {
                        //当前时间的周日
                        Date nowSunday = DateUtil.endOfWeek(new Date());
                        //上周日
                        DateTime endSunday = DateUtil.offsetWeek(nowSunday, -1);
                        betweenDay = DateUtil.between(startMonday, endSunday, DateUnit.DAY) + 1;
                    } else {
                        //结束时间的周日
                        DateTime endSunday = DateUtil.endOfWeek(end);
                        betweenDay = DateUtil.between(startMonday, endSunday, DateUnit.DAY) + 1;
                    }
                    return betweenDay / 7;
                }
                //结束年份小于等于开始年份
                else {
                    //当前时间的周一
                    Date nowMonday = DateUtil.beginOfWeek(new Date());
                    //开始时间的周一
                    DateTime startMonday = DateUtil.beginOfWeek(start);
                    //结束时间的周一
                    Date endMonday = DateUtil.beginOfWeek(end);
                    //同一天
                    boolean startSameTime = DateUtil.isSameTime(nowMonday, startMonday);
                    boolean endSameTime = DateUtil.isSameTime(nowMonday, endMonday);
                    //开始时间、结束时间都是同一天
                    if (startSameTime && endSameTime) {
                        return 0;
                    }
                    //开始时间、结束时间都不是同一天
                    if (!startSameTime && !endSameTime) {
                        //结束时间的周日
                        DateTime endSunday = DateUtil.endOfWeek(end);
                        long betweenDay = DateUtil.between(startMonday, endSunday, DateUnit.DAY) + 1;
                        return betweenDay / 7;
                    } else {
                        //当前时间的周日
                        Date nowSunday = DateUtil.endOfWeek(new Date());
                        //上周日
                        DateTime endSunday = DateUtil.offsetWeek(nowSunday, -1);
                        long betweenDay = DateUtil.between(startMonday, endSunday, DateUnit.DAY) + 1;
                        return betweenDay / 7;
                    }
                }
            }
            //结束时间等于当前时间
            if (end.equals(todayDate)) {
                //当前时间的周一
                Date nowMonday = DateUtil.beginOfWeek(new Date());
                //开始时间的周一
                DateTime startMonday = DateUtil.beginOfWeek(start);
                //同一天
                boolean startSameTime = DateUtil.isSameTime(nowMonday, startMonday);
                long betweenDay = 0;
                //同一天
                if (startSameTime) {
                    return betweenDay;
                } else {
                    //当前时间的上周日
                    Date nowSunday = DateUtil.endOfWeek(new Date());
                    DateTime nowLastSunday = DateUtil.offsetDay(nowSunday, -7);
                    betweenDay = DateUtil.between(startMonday, nowLastSunday, DateUnit.DAY) + 1;
                    return betweenDay/7;
                }

            }
            //结束时间大于当前时间
            if(end.after(todayDate))
            {
                //当前时间的周一
                Date nowMonday = DateUtil.beginOfWeek(new Date());
                //开始时间的周一
                DateTime startMonday = DateUtil.beginOfWeek(start);
                //同一天
                boolean startSameTime = DateUtil.isSameTime(nowMonday, startMonday);
                long betweenDay = 0;
                //同一天
                if (startSameTime) {
                    return betweenDay;
                } else {
                    //当前时间的上周日
                    Date nowSunday = DateUtil.endOfWeek(new Date());
                    DateTime nowLastSunday = DateUtil.offsetDay(nowSunday, -7);
                    betweenDay = DateUtil.between(startMonday, nowLastSunday, DateUnit.DAY) + 1;
                    return betweenDay/7;
                }
            }
        }
        return 0;

    }

    /**
     * 推算平均每周的漏检数的时间范围
     * @param startDate
     * @param endDate
     * @return
     */
    public static String startEndDateWeek(String startDate, String endDate) {
        Date todayDate = DateUtil.date();
        Date start = DateUtil.parse(startDate, "yyyy-MM-dd");
        Date end = DateUtil.parse(endDate, "yyyy-MM-dd");
        int startMonth = DateUtil.month(start) + 1;
        int endMonth = DateUtil.month(end) + 1;
        //开始时间大于等于当前时间
        String thisWeek = DateUtil.format(start, "yyyy-MM-dd 00:00:00") + "~" + DateUtil.format(end, "yyyy-MM-dd 23:59:59");
        if (start.after(todayDate) || start.equals(DateUtil.date())) {
            return thisWeek;
        }
        //开始时间小于当前时间
        else {
            //结束时间小于当前时间
            if (end.before(todayDate)) {
                int startYear = DateUtil.year(start);
                int endYear = DateUtil.year(end);
                //结束年份大于开始年份
                if (endYear > startYear) {
                    //当前时间的周一
                    Date nowMonday = DateUtil.beginOfWeek(new Date());
                    //结束时间的周一
                    Date endMonday = DateUtil.beginOfWeek(end);
                    //同一天
                    boolean endSameTime = DateUtil.isSameTime(nowMonday, endMonday);
                    if (endSameTime) {
                        //当前时间的周日
                        Date nowSunday = DateUtil.endOfWeek(new Date());
                        //上周的周日
                        Date endSunday = DateUtil.offsetWeek(nowSunday, -1);
                        //推算开始时间的漏检日期
                        String startDateScope = getOmitDateScope(start);
                        String s = startDateScope.split("~")[0];
                        //推算结束时间的漏检日期
                        String endDateScope = getOmitDateScope(endSunday);
                        String e = endDateScope.split("~")[1];
                        //拼接返回
                        thisWeek = DateUtil.format(DateUtil.parse(s), "yyyy-MM-dd 00:00:00") + "~" + DateUtil.format(DateUtil.parse(e), "yyyy-MM-dd 23:59:59");
                        return thisWeek;
                    } else {
                        //推算开始时间的漏检日期
                        String startDateScope = getOmitDateScope(start);
                        String s = startDateScope.split("~")[0];
                        //推算结束时间的漏检日期
                        String endDateScope = getOmitDateScope(end);
                        String e = endDateScope.split("~")[1];
                        //拼接返回
                        thisWeek = DateUtil.format(DateUtil.parse(s), "yyyy-MM-dd 00:00:00") + "~" + DateUtil.format(DateUtil.parse(e), "yyyy-MM-dd 23:59:59");
                        return thisWeek;
                    }

                }
                //结束年份小于等于开始年份
                else {
                    //结束月份大于开始月份
                    if (endMonth > startMonth) {
                        //当前时间的周一
                        Date nowMonday = DateUtil.beginOfWeek(new Date());
                        //结束时间的周一
                        Date endMonday = DateUtil.beginOfWeek(end);
                        //同一天
                        boolean endSameTime = DateUtil.isSameTime(nowMonday, endMonday);
                        if (endSameTime) {
                            return thisWeek;
                        } else {
                            //推算开始时间的漏检日期
                            String startDateScope = getOmitDateScope(start);
                            String s = startDateScope.split("~")[0];
                            //推算结束时间的漏检日期
                            String endDateScope = getOmitDateScope(end);
                            String e = endDateScope.split("~")[1];
                            //拼接返回
                            thisWeek = DateUtil.format(DateUtil.parse(s), "yyyy-MM-dd 00:00:00") + "~" + DateUtil.format(DateUtil.parse(e), "yyyy-MM-dd 23:59:59");
                            return thisWeek;
                        }

                    }
                    //结束月份小于等于开始月份
                    else {
                        //当前时间的周一
                        Date nowMonday = DateUtil.beginOfWeek(new Date());
                        //开始、结束时间的周一
                        Date startMonday = DateUtil.beginOfWeek(start);
                        Date endMonday = DateUtil.beginOfWeek(end);
                        //同一天
                        boolean startSameTime = DateUtil.isSameTime(nowMonday, startMonday);
                        boolean endSameTime = DateUtil.isSameTime(nowMonday, endMonday);
                        //都是周一
                        if (startSameTime && endSameTime) {
                            return thisWeek;
                        }
                        //开始时间不是周一,结束时间是
                        if (!startSameTime && endSameTime) {
                            //当前时间的周日
                            Date nowSunday = DateUtil.endOfWeek(new Date());
                            //上周的周日
                            Date endSunday = DateUtil.offsetWeek(nowSunday, -1);
                            //推算开始时间的漏检日期
                            String startDateScope = getOmitDateScope(start);
                            String s = startDateScope.split("~")[0];
                            //推算结束时间的漏检日期
                            String endDateScope = getOmitDateScope(endSunday);
                            String e = endDateScope.split("~")[1];
                            //拼接返回
                            thisWeek = DateUtil.format(DateUtil.parse(s), "yyyy-MM-dd 00:00:00") + "~" + DateUtil.format(DateUtil.parse(e), "yyyy-MM-dd 23:59:59");
                            return thisWeek;
                        }
                        //都不是
                        else {   //推算开始时间的漏检日期
                            String startDateScope = getOmitDateScope(start);
                            String s = startDateScope.split("~")[0];
                            //推算结束时间的漏检日期
                            String endDateScope = getOmitDateScope(end);
                            String e = endDateScope.split("~")[1];
                            //拼接返回
                            thisWeek = DateUtil.format(DateUtil.parse(s), "yyyy-MM-dd 00:00:00") + "~" + DateUtil.format(DateUtil.parse(e), "yyyy-MM-dd 23:59:59");
                            return thisWeek;
                        }
                    }
                }
            }
            //结束时间大于等于当前时间
            else {
                int startYear = DateUtil.year(start);
                int endYear = DateUtil.year(end);
                //结束年份大于开始年份
                if (endYear > startYear) {
                    //当前时间的周一
                    Date nowMonday = DateUtil.beginOfWeek(new Date());
                    //开始时间的周一
                    Date startMonday = DateUtil.beginOfWeek(start);
                    //同一天
                    boolean startSameTime = DateUtil.isSameTime(nowMonday, startMonday);
                    if (startSameTime) {
                        return thisWeek;
                    } else {
                        //推算开始时间的漏检日期
                        String startDateScope = getOmitDateScope(start);
                        String s = startDateScope.split("~")[0];
                        Date endSunday = DateUtil.endOfWeek(new Date());
                        Date lastEndSunday = DateUtil.offsetWeek(endSunday, -1);
                        //推算结束时间的漏检日期
                        String endDateScope = getOmitDateScope(lastEndSunday);
                        String e = endDateScope.split("~")[1];
                        //拼接返回
                        thisWeek = DateUtil.format(DateUtil.parse(s), "yyyy-MM-dd 00:00:00") + "~" + DateUtil.format(DateUtil.parse(e), "yyyy-MM-dd 23:59:59");
                        return thisWeek;
                    }
                } else {
                    //推算开始时间的漏检日期
                    String startDateScope = getOmitDateScope(start);
                    String s = startDateScope.split("~")[0];
                    //推算当前时间的漏检日期
                    //当前时间的周日
                    Date nowSunday = DateUtil.endOfWeek(new Date());
                    //上周的周日
                    Date endSunday = DateUtil.offsetWeek(nowSunday, -1);
                    String endDateScope = getOmitDateScope(endSunday);
                    String e = endDateScope.split("~")[1];
                    //拼接返回
                    thisWeek = DateUtil.format(DateUtil.parse(s), "yyyy-MM-dd 00:00:00") + "~" + DateUtil.format(DateUtil.parse(e), "yyyy-MM-dd 23:59:59");
                    return thisWeek;
                }
            }

        }

    }

    /**
     * 推算平均每月的漏检数的时间范围
     * @param startDate
     * @param endDate
     * @return
     */

    public static String startEndDateMonth(String startDate, String endDate) {
        String today = DateUtil.format(new Date(), "yyyy-MM");
        Date s = DateUtil.parse(startDate, "yyyy-MM");
        Date e = DateUtil.parse(endDate, "yyyy-MM");
        Date n = DateUtil.parse(today, "yyyy-MM");
        Date start = DateUtil.parse(startDate, "yyyy-MM-dd");
        Date end = DateUtil.parse(endDate, "yyyy-MM-dd");
        String thisWeek = DateUtil.format(start, "yyyy-MM-dd 00:00:00") + "~" + DateUtil.format(end, "yyyy-MM-dd 23:59:59");
        //开始时间大于等于当前时间
        if(s.after(n)||s.equals(n))
        {
            return thisWeek;
        }
        //开始时间小于当前时间
        else
        {
            //获取当月的一号
            Date firstDay = DateUtil.beginOfMonth(new Date());
            //开始时间大于当月一号
            if(start.after(firstDay))
            {
                return thisWeek;
            }
            //开始时间小于当月一号
            else
            {
                //结束时间大于当月一号
                if(end.after(firstDay))
                {
                    //获取上个月最后一天
                    Date lastMonthDay = DateUtil.offsetDay(firstDay,-1);
                    //推算开始时间的漏检日期
                    String startDateScope = getOmitDateScope(start);
                    String startOmitDate = startDateScope.split("~")[0];
                    //推算结束时间的漏检日期
                    String endDateScope = getOmitDateScope(lastMonthDay);
                    String endOmitDate = endDateScope.split("~")[1];
                    //拼接返回
                    thisWeek = DateUtil.format(DateUtil.parse(startOmitDate), "yyyy-MM-dd 00:00:00") + "~" + DateUtil.format(DateUtil.parse(endOmitDate), "yyyy-MM-dd 23:59:59");
                    return thisWeek;
                }
                //结束时间小于当月一号
                else
                {
                    //推算开始时间的漏检日期
                    String startDateScope = getOmitDateScope(start);
                    String startOmitDate = startDateScope.split("~")[0];
                    //推算结束时间的漏检日期
                    String endDateScope = getOmitDateScope(end);
                    String endOmitDate = endDateScope.split("~")[1];
                    //拼接返回
                    thisWeek = DateUtil.format(DateUtil.parse(startOmitDate), "yyyy-MM-dd 00:00:00") + "~" + DateUtil.format(DateUtil.parse(endOmitDate), "yyyy-MM-dd 23:59:59");
                    return thisWeek;
                }
            }

        }
    }

    public static String getOmitDateScope(Date date) {
        // 参数日期所在周的周一
        Date monday = DateUtil.beginOfWeek(date);
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate localDate = monday.toInstant().atZone(zoneId).toLocalDate();
        if (Calendar.FRIDAY == DateUtil.dayOfWeek(date) || Calendar.SATURDAY == DateUtil.dayOfWeek(date)
                || Calendar.SUNDAY == DateUtil.dayOfWeek(date)) {
            // 周一往后3天，星期四
            Date thursday = Date.from(localDate.plusDays(3).atStartOfDay().atZone(zoneId).toInstant());
            return DateUtil.format(monday, "yyyy-MM-dd 00:00:00").concat("~").concat(DateUtil.format(thursday, "yyyy-MM-dd 23:59:59"));
        } else {
            // 周一往前3天，星期五
            Date friday = Date.from(localDate.minusDays(3).atStartOfDay().atZone(zoneId).toInstant());
            // 周一往前1天，星期天
            Date sunday = Date.from(localDate.minusDays(1).atStartOfDay().atZone(zoneId).toInstant());
            return DateUtil.format(friday, "yyyy-MM-dd 00:00:00").concat("~").concat(DateUtil.format(sunday, "yyyy-MM-dd 23:59:59"));
        }
    }
}
