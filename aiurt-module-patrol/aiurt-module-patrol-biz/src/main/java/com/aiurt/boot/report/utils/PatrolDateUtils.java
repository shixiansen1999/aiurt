package com.aiurt.boot.report.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

import java.text.SimpleDateFormat;
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
     *   判断是否是同一周
     */
    public static boolean isSameDate(String date1, String date2)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = null;
        Date d2 = null;
        try
        {
            d1 = format.parse(date1);
            d2 = format.parse(date2);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setFirstDayOfWeek(Calendar.MONDAY);//西方周日为一周的第一天，咱得将周一设为一周第一天
        cal2.setFirstDayOfWeek(Calendar.MONDAY);
        cal1.setTime(d1);
        cal2.setTime(d2);
        int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
        if (subYear == 0)// subYear==0,说明是同一年
        {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)) {
                return true;
            }
        }
        else if (subYear == 1 && cal2.get(Calendar.MONTH) == 11) //subYear==1,说明cal比cal2大一年;java的一月用"0"标识，那么12月用"11"
        {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)) {
                return true;
            }
        }
        else if (subYear == -1 && cal1.get(Calendar.MONTH) == 11)//subYear==-1,说明cal比cal2小一年
        {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 计算周数
     * @param startDate
     * @param endDate
     * @return
     */
    public static Integer countTwoDayWeek(String startDate, String endDate,boolean sameDate)
    {

        Date start = DateUtil.parse(startDate);
        DateTime end = DateUtil.parse(endDate);
        Calendar cal=Calendar.getInstance();
        cal.setTime(start);
        long time1=cal.getTimeInMillis();
        cal.setTime(end);
        long time2=cal.getTimeInMillis();
        long betweenDays=(time2-time1)/(1000*3600*24);
        Double days=Double.parseDouble(String.valueOf(betweenDays));
        if((days/7)>0 && (days/7)<=1&&sameDate==true){
            //不满一周的按一周算
            return 1;
        }
        else if((days/7)>0 && (days/7)<=1&&sameDate==false)
        {
            return  1+1;
        }
        else if(sameDate==false){
            int weekNumber= 2;
            //周一是相等
            DateTime startThisMonDay = DateUtil.beginOfWeek(start);
            DateTime endThisMonDay = DateUtil.beginOfWeek(end);

            DateTime stayDay = DateUtil.offsetDay(startThisMonDay, 7);
            DateTime endDay = DateUtil.offsetDay(endThisMonDay, -7);
            //开始时间的下周一与结束时间的周一是否是同一天
            boolean nowIsMonDay = DateUtil.isSameDay(endThisMonDay, stayDay);
            //开始时间的下周一与结束时间的上周一，是否是同一天
            boolean sameTime = DateUtil.isSameDay(stayDay, endDay);
            //本周一不是当前，开始和结束的周一是同一天
            if(sameTime==true&&nowIsMonDay==false)
            {
                return  weekNumber+1;
            }
            if(nowIsMonDay==true)
            {
                return  weekNumber;
            }
            if(sameTime==false&&nowIsMonDay==false)
            {
                Date day = DateUtil.endOfWeek(endDay);
                DateTime lastDay = DateUtil.offsetDay(day, 1);
                long betweenDay = DateUtil.between(stayDay, lastDay, DateUnit.DAY);
                Long number = betweenDay/7;
                return weekNumber+number.intValue();
            }
            else
            {
                return null;
            }

        }
        else if((days/7)==0){
            return 0;
        }else{
            //负数返还null
            return null;
        }

    }

    /**
     * 计算周数
     * @param startDate
     * @param endDate
     * @return
     */
    public static long countTwoDayWeek(String startDate, String endDate)
    {
        Date todayDate = DateUtil.date();
        Date start = DateUtil.parse(startDate, "yyyy-MM-dd");
        Date end = DateUtil.parse(endDate, "yyyy-MM-dd");
        int startMonth = DateUtil.month(start)+1;
        int endMonth = DateUtil.month(end)+1;
        //开始时间大于等于当前时间
        if(start.after(todayDate)||start.equals(DateUtil.date()))
        {
            return  0;
        }
        //开始时间小于当前时间
        else
        {
            //结束时间小于当前时间
            if(end.before(todayDate))
            {
                int startYear = DateUtil.year(start);
                int endYear = DateUtil.year(end);
                //结束年份大于开始年份
                if(endYear>startYear) {
                    //结束月份大于开始月份
                    if(endMonth>startMonth)
                    {
                        //开始时间的周一
                        DateTime startMonday = DateUtil.beginOfWeek(start);
                        //结束时间的周日
                        DateTime endSunday = DateUtil.endOfWeek(end);
                        long betweenDay = DateUtil.between(startMonday, endSunday, DateUnit.DAY)+1;
                        return betweenDay/7;
                    }
                    //结束月份小于等于开始月份
                    else
                    {
                        //开始时间的周一
                        DateTime startMonday = DateUtil.beginOfWeek(start);
                        //结束时间的周日
                        DateTime endSunday = DateUtil.endOfWeek(end);
                        long betweenDay = DateUtil.between(startMonday, endSunday, DateUnit.DAY)+1;
                        return betweenDay/7;
                    }
                }
                //结束年份小于等于开始年份
                else {
                    //结束月份大于开始月份
                    if(endMonth>startMonth)
                    {
                        //开始时间的周一
                        DateTime startMonday = DateUtil.beginOfWeek(start);
                        //结束时间的周日
                        DateTime endSunday = DateUtil.endOfWeek(end);
                        long betweenDay = DateUtil.between(startMonday, endSunday, DateUnit.DAY)+1;
                        return betweenDay/7;
                    }
                    //结束月份小于等于开始月份
                    else
                    {
                        //开始时间的周一
                        DateTime startMonday = DateUtil.beginOfWeek(start);
                        //结束时间的周日
                        DateTime endSunday = DateUtil.endOfWeek(end);
                        long betweenDay = DateUtil.between(startMonday, endSunday, DateUnit.DAY)+1;
                        return betweenDay/7;
                    }
                }
            }
            //结束时间大于等于当前时间
            if(end.equals(DateUtil.date())||end.after(todayDate))
            {
                int startYear = DateUtil.year(start);
                int endYear = DateUtil.year(end);
                //结束年份大于开始年份
                if(endYear>startYear) {
                    //结束月份大于开始月份
                    if(endMonth>startMonth)
                    {
                        //开始时间的周一
                        DateTime startMonday = DateUtil.beginOfWeek(start);
                        //当前时间的上周日
                        Date nowSunday = DateUtil.endOfWeek(new Date());
                        DateTime nowLastSunday = DateUtil.offsetDay(nowSunday, -7);
                        long betweenDay = DateUtil.between(startMonday, nowLastSunday, DateUnit.DAY)+1;
                        return betweenDay/7;
                    }
                    //结束月份小于等于开始月份
                    else
                    {
                        //开始时间的周一
                        DateTime startMonday = DateUtil.beginOfWeek(start);
                        //结束时间的周日
                        DateTime endSunday = DateUtil.endOfWeek(end);
                        long betweenDay = DateUtil.between(startMonday, endSunday, DateUnit.DAY)+1;
                        return betweenDay/7;
                    }
                }
                //结束年份小于等于开始年份
                else
                {
                    //结束月份大于开始月份
                    if(endMonth>startMonth)
                    {
                        //开始时间的周一
                        DateTime startMonday = DateUtil.beginOfWeek(start);
                        //当前时间的上周日
                        Date nowSunday = DateUtil.endOfWeek(new Date());
                        DateTime nowLastSunday = DateUtil.offsetDay(nowSunday, -7);
                        long betweenDay = DateUtil.between(startMonday, nowLastSunday, DateUnit.DAY)+1;
                        return betweenDay/7;

                    }
                    //结束月份小于等于开始月份
                    else
                    {
                        //开始时间的周一
                        DateTime startMonday = DateUtil.beginOfWeek(start);
                        //当前时间的上周日
                        Date nowSunday = DateUtil.endOfWeek(new Date());
                        DateTime nowLastSunday = DateUtil.offsetDay(nowSunday, -7);
                        long betweenDay = DateUtil.between(startMonday, nowLastSunday, DateUnit.DAY)+1;
                        return betweenDay/7;
                    }
                }
            }
        }
        return 0;

    }

    /**
     * 比较两个日期：相等（0）、之前（1）、之后（2）
     * @param tagDateTime
     * @return
     */
    public static Integer belongCalendarBefore(String tagDateTime) {
        Date fomatDate1=DateUtil.parse(tagDateTime,"yyyy-MM-dd");
        String today= DateUtil.today();
        Date date = DateUtil.parse(today);
        //比较两个日期
        int result=date.compareTo(fomatDate1);
        //如果日期相等返回0
        if(result==0){
            return 0;
        }else if(result<0){
            //小于0，参数date1就是在date2之后
            return 2;
        }else{
            //大于0，参数date1就是在date2之前
            return 1;
        }
    }
}
