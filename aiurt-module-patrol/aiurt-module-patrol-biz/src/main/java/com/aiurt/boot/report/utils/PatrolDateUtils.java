package com.aiurt.boot.report.utils;

import cn.hutool.core.date.DateTime;
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
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        }
        else if (subYear == 1 && cal2.get(Calendar.MONTH) == 11) //subYear==1,说明cal比cal2大一年;java的一月用"0"标识，那么12月用"11"
        {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        }
        else if (subYear == -1 && cal1.get(Calendar.MONTH) == 11)//subYear==-1,说明cal比cal2小一年
        {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
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

        DateTime start = DateUtil.parse(startDate);
        DateTime end = DateUtil.parse(endDate);
        Calendar cal=Calendar.getInstance();

        cal.setTime(start);

        long time1=cal.getTimeInMillis();

        cal.setTime(end);

        long time2=cal.getTimeInMillis();

        long between_days=(time2-time1)/(1000*3600*24);
        Double days=Double.parseDouble(String.valueOf(between_days));
        if((days/7)>0 && (days/7)<=1&&sameDate==true){
            //不满一周的按一周算
            return 1;
        }
        else if((days/7)>0 && (days/7)<=1&&sameDate==false)
        {
            return  1+1;
        }
        else if(days/7>1){
            int day= days.intValue();
            if(day%7>0){
                return day/7+1;
            }else{
                return day/7;
            }
        }else if((days/7)==0){
            return 0;
        }else{
            //负数返还null
            return null;
        }

    }
}
