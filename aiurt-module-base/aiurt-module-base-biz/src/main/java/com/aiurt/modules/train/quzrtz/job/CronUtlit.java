package com.aiurt.modules.train.quzrtz.job;

import com.aiurt.modules.train.exam.entity.BdExamRecord;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zwl
 */
public class CronUtlit {

    public static String formatDateByPattern(Date date, String dateFormat){
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String formatTimeStr = null;
        if (date != null) {
            formatTimeStr = sdf.format(date);
        }
        return formatTimeStr;
    }

    public static String getCron(Date  date){
        String dateFormat="ss mm HH dd MM ? yyyy";
        return formatDateByPattern(date, dateFormat);
    }

}
