package com.aiurt.boot.modules.manage.anno;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaulValidate {
    /**
     * 判断为空
     *
     * @param param
     * @return
     */
    public static boolean isNotEmpty(String param) {
        if (StringUtils.isNotEmpty(param)) {
            return true;
        }
        return false;
    }

    /**
     * 数字只能是正整数
     *
     * @param num
     * @return
     */
    public static boolean isStyleNum(String num) {
        String regex = "^[+]{0,1}(\\d+)$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(num);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 判断时间格式
     *
     * @return
     */
    public static boolean isCorrectDateFormat(String param, String dateFormats) {
        String formats[] = dateFormats.split(",");
        if (formats != null && formats.length > 0) {
            for (String format : formats) {
                if (strToDate(param, format)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean strToDate(String dateStr, String formatStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        Date date = null;
        try {
            // 注意格式需要与上面一致，不然会出现异常
            date = sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean isYesOrNo(String s) {
        return "是".equals(s) || "否".equals(s);
    }

    public static boolean isYes(String s) {
        return "是".equals(s);
    }

    public static int changeOneOrZero(String s) {
        return "是".equals(s) ? 1 : 0;
    }
}
