package com.aiurt.common.util;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataValidateUtils {
    /**
     * 判断输入的文本是否 是 是或否
     *
     * @param s
     * @return
     */
    public static boolean isSituation(String s) {
        return "是".equals(s) || "否".equals(s);
    }

    /**
     * 判断输入的文本是否是男女
     *
     * @param s
     * @return
     */
    public static boolean isSex(String s) {
        return "男".equals(s) || "女".equals(s);
    }

    /**
     * 判断时间格式是否是yyyy-MM 或者 yyyy/MM
     *
     * @param s
     * @return
     */
    public static boolean isCorrectDateFormat(String s) {
        return strToDate(s, "yyyy-MM") || strToDate(s, "yyyy/MM");
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
     * 工具-字符串-转换Sql查询IN中使用的格式
     * 效果：a,b==>'a','b'
     * @param str
     * @return
     */
    public static String strToDbin(String str){
        return String.format("'%s'", StringUtils.join(str.split(","),"','"));
    }

}
